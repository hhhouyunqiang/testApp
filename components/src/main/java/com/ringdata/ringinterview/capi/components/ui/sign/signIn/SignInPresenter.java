package com.ringdata.ringinterview.capi.components.ui.sign.signIn;

import android.content.Context;
import android.graphics.Color;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ringdata.base.net.RestClient;
import com.ringdata.base.net.callback.IError;
import com.ringdata.base.net.callback.ISuccess;
import com.ringdata.base.util.NetworkUtil;
import com.ringdata.base.util.encrypt.MD5Util;
import com.ringdata.ringinterview.capi.components.App;
import com.ringdata.ringinterview.capi.components.constant.Msg;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.ApiUrl;
import com.ringdata.ringinterview.capi.components.data.FileData;
import com.ringdata.ringinterview.capi.components.data.MessageEvent;
import com.ringdata.ringinterview.capi.components.data.ResponseJson;
import com.ringdata.ringinterview.capi.components.data.dao.UserDao;
import com.ringdata.ringinterview.capi.components.data.model.User;
import com.ringdata.ringinterview.survey.SurveyAccess;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

/**
 * Created by admin on 2018/5/23.
 */

public class SignInPresenter implements SignInContract.Presenter {
    private final SignInContract.View mView;
    private final Context mContext;

    public SignInPresenter(SignInContract.View view, Context context) {
        this.mView = view;
        this.mContext = context;
    }

    @Override
    public void start() {
        EventBus.getDefault().register(this);
        initData();
    }

    @Override
    public void signIn(final String username, final String password) {
        if (username.isEmpty()) {
            mView.showError("用户名不能为空！");
            return;
        }
        if (password.isEmpty()) {
            mView.showError("密码不能为空！");
            return;
        }
        if (!NetworkUtil.isNetworkAvailable()) {
            localCheckLogin(username, password);
            return;
        }
        RestClient.builder().url(ApiUrl.LOGIN)
                .tag(mView.toString())
                .loader(mContext, "登录中")
                .params("grant_type", "password")
                .params("username", username)
                .params("password", MD5Util.md5(password))
                .params("clientType", "RS_APP")
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        ResponseJson responseJson = new ResponseJson(response);
                        final JSONObject userInfo = responseJson.getAsObjectByKey("userInfo");
                        String accessToken = responseJson.getAsStringByKey("access_token");
                        String refreshToken = responseJson.getAsStringByKey("refresh_token");

                        int id = userInfo.getIntValue("userId");
                        String orgHost = App.orgHost;
                        SPUtils.getInstance().put(SPKey.ACCESS_TOKEN, accessToken);
                        SPUtils.getInstance().put(SPKey.REFRESH_TOKEN, refreshToken);
                        getUserInfo(id, username, password, orgHost);
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        JSONObject error = JSONObject.parseObject(msg);
                        if (error.containsKey("message")) {
                            mView.showError(error.getString("message"));
                        } else {
                            mView.showError(msg);
                        }
                    }
                })
                .build()
                .tokenPost();
    }

    private void getUserInfo(final int userId, final String username, final String password, final String orgHost) {
        String s=App.orgHost + ApiUrl.USER_INFO;
        RestClient.builder().url(App.orgHost + ApiUrl.USER_INFO)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        ResponseJson responseJson = new ResponseJson(response);
                        if (!responseJson.getSuccess()) {
                            mView.showError(responseJson.getMsg());
                        } else {
                            JSONObject jsonObject = responseJson.getDataAsObject();
                            if (jsonObject != null) {
                                //登录成功保存或更新数据到数据库
                                User user = new User();
                                user.setId(userId);
                                user.setName(username);
                                user.setRole(jsonObject.getInteger("role"));
                                user.setAvatarPath(jsonObject.getString("avatarPath"));
                                user.setPassword(password);
                                user.setOrgHost(orgHost);
                                boolean isSuccess = UserDao.replace(user);
                                if (!isSuccess) {
                                    mView.showError("数据保存失败");
                                }
                                SPUtils.getInstance().put(SPKey.USERID, userId);
                                SPUtils.getInstance().put(SPKey.USERNAME, username);
                                if (SPUtils.getInstance().getBoolean(SPKey.SETTING_SAVE_ACCOUNT)) {
                                    SPUtils.getInstance().put(SPKey.PASSWORD, password);
                                }
                                mView.showHomeDelegateUI();
//                                checkJSVersion();
                            } else {
                                mView.showError("获取用户信息失败");
                            }
                        }
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        mView.showError(msg);
                    }
                })
                .build()
                .post();
    }

    @Override
    public void checkJSVersion() {
        RestClient.builder().url(App.orgHost + ApiUrl.UPDATE_JS)
//                .params("organizationCode", SPUtils.getInstance().getString(SPKey.ORG_CODE))
                .loader(mContext, "检查更新")
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        ResponseJson responseJson = new ResponseJson(response);
                        if (responseJson.getSuccess()) {
                            int localJSVersionCode = SurveyAccess.instance.getLocalSurveyJSVersion();
                            JSONObject dataJsonObject = responseJson.getDataAsObject();
                            if (dataJsonObject != null) {
                                final int versionCode = dataJsonObject.getInteger("surveyJsCode");
                                final String surveyJsUrl = dataJsonObject.getString("surveyJsUrl");
                                if (versionCode > localJSVersionCode) {
                                    downloadSurveyZIP(surveyJsUrl, versionCode);
                                } else {
                                    mView.showHomeDelegateUI();
                                }
                            } else {
                                mView.showHomeDelegateUI();
                            }
                        } else {
                            mView.showHomeDelegateUI();
                        }
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        ToastUtils.setBgColor(Color.GRAY);
                        ToastUtils.showShort("问卷引擎更新失败");
                        mView.showHomeDelegateUI();
                    }
                })
                .build()
                .post();
    }

    private void downloadSurveyZIP(String urlPath, final int versionCode) {
        RestClient.builder().url(urlPath)
                .filePath(FileData.UPDATE_JS + "www.zip")
                .loader(mContext, "引擎更新中")
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        new Thread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        SurveyAccess.instance.updateSurveyJS(new File(FileData.UPDATE_JS + "www.zip"), versionCode);
                                        EventBus.getDefault().post(new MessageEvent(Msg.R_E_SURVEY_WWW, this));
                                    }
                                }).start();
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        ToastUtils.setBgColor(Color.GRAY);
                        ToastUtils.showShort("问卷引擎更新失败");
                        mView.showHomeDelegateUI();
                    }
                })
                .build()
                .download();
    }

    @Override
    public void localCheckLogin(String username, String password) {
        User user = UserDao.query(username, password);
        if (user != null) {
            SPUtils.getInstance().put(SPKey.USERID, user.getId());
            SPUtils.getInstance().put(SPKey.USERNAME, username);
            if (SPUtils.getInstance().getBoolean(SPKey.SETTING_SAVE_ACCOUNT)) {
                SPUtils.getInstance().put(SPKey.PASSWORD, password);
            }
            mView.showHomeDelegateUI();
        } else {
            mView.showError("离线登录失败");
        }
    }

    @Override
    public void initData() {
        mView.refreshDescription(SPUtils.getInstance().getString(SPKey.APP_SIGNIN_DESCRIPT, ""));
        User user = UserDao.query(SPUtils.getInstance().getInt(SPKey.USERID));
        if (user != null) {
            if (!mView.isActive()) {
                return;
            }
            if (SPUtils.getInstance().getBoolean(SPKey.SETTING_SAVE_ACCOUNT)) {
                mView.refreshLogoView(user.getName(), user.getPassword());
            } else {
                mView.refreshLogoView(user.getName(), "");
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        Object bindTag = event.getBindTag();
        int tag = event.getTag();
        if (tag == Msg.R_E_SURVEY_WWW) {
            mView.showHomeDelegateUI();
        }
    }

    @Override
    public void destroy() {
        EventBus.getDefault().unregister(this);
    }
}
