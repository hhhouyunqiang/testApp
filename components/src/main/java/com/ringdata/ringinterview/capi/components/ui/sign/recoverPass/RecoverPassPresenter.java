package com.ringdata.ringinterview.capi.components.ui.sign.recoverPass;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.Patterns;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ToastUtils;
import com.ringdata.base.net.RestClient;
import com.ringdata.base.net.callback.IError;
import com.ringdata.base.net.callback.ISuccess;
import com.ringdata.base.util.NetworkUtil;
import com.ringdata.ringinterview.capi.components.data.ApiUrl;
import com.ringdata.ringinterview.capi.components.data.ResponseJson;

/**
 * Created by bella_wang on 2020/3/23.
 */

public class RecoverPassPresenter implements RecoverPassContract.Presenter {
    private final RecoverPassContract.View mView;
    private final Context mContext;

    private CountDownTimer timer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            mView.countDownCheckBtn((int)(millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            mView.reEnableCheckBtn();
        }
    };

    public RecoverPassPresenter(RecoverPassContract.View view, Context context) {
        this.mView = view;
        this.mContext = context;
    }

    @Override
    public void start() {

    }

    @Override
    public void sendCheckNum(String mobile) {
        if (mobile.isEmpty()) {
            mView.showError("请输入手机号码");
            return;
        } else if (!Patterns.PHONE.matcher(mobile).matches()) {
            mView.showError("手机号码格式不对！");
            return;
        }
        if (!NetworkUtil.isNetworkAvailable()) {
            mView.showError("无网络连接");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("telephone", mobile);
        RestClient.builder().url(ApiUrl.SEND_CODE)
                .tag(mView.toString())
                .loader(mContext, "正在发送验证码")
                .raw(jsonObject.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        ResponseJson responseJson = new ResponseJson(response);
                        if (responseJson.getSuccess()) {
                            ToastUtils.setBgColor(Color.GRAY);
                            ToastUtils.showShort("发送成功");
                            mView.disableCheckBtn();
                            timer.start();
                        } else {
                            mView.showError(responseJson.getMsg());
                            mView.enableCheckBtn();
                        }
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
                .post();
    }

    @Override
    public void resetPassByMobile(String phone, String code, String pwd) {
        if (phone.isEmpty()) {
            mView.showError("请输入手机号码");
            return;
        } else if (!Patterns.PHONE.matcher(phone).matches()) {
            mView.showError("手机号码格式不对！");
            return;
        }
        if (code.isEmpty()) {
            mView.showError("请输入验证码");
            return;
        }
        if (pwd.isEmpty()) {
            mView.showError("请输入密码");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("telephone", phone);
        jsonObject.put("dynamicPass", code);
        jsonObject.put("password", pwd);
        RestClient.builder().url(ApiUrl.RESET_PWD_PHONE)
                .tag(mView.toString())
                .loader(mContext, "请稍候")
                .raw(jsonObject.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        ResponseJson responseJson = new ResponseJson(response);
                        if (responseJson.getSuccess()) {
                            mView.showSignInDelegateUI();
                        } else {
                            mView.showError(responseJson.getMsg());
                        }
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
                .post();
    }

    @Override
    public void matchUserAndEmail(String name, String email, String code) {
        if (name.isEmpty()) {
            mView.showError("请输入用户名");
            return;
        }
        if (email.isEmpty()) {
            mView.showError("请输入邮箱");
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mView.showError("邮箱格式不对！");
            return;
        }
        if (code.isEmpty()) {
            mView.showError("请输入图形验证码");
            return;
        }
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("email", email);
        jsonObject.put("code", code);
        RestClient.builder().url(ApiUrl.USER_MATCH_EMAIL)
                .tag(mView.toString())
                .loader(mContext, "请稍候")
                .raw(jsonObject.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        ResponseJson responseJson = new ResponseJson(response);
                        if (responseJson.getSuccess()) {
                            if (responseJson.getDataAsBoolean()) {
                                resetPassByEmail(jsonObject.toJSONString());
                            } else {
                                mView.showError("用户名和邮箱不匹配");
                            }
                        } else {
                            mView.showError(responseJson.getMsg());
                        }
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
                .post();
    }

    private void resetPassByEmail(String params) {
        RestClient.builder().url(ApiUrl.RESET_PWD_EMAIL)
                .tag(mView.toString())
                .loader(mContext, "请稍候")
                .raw(params)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        ResponseJson responseJson = new ResponseJson(response);
                        if (responseJson.getSuccess()) {
                            mView.showSignInDelegateUI();
                        } else {
                            mView.showError(responseJson.getMsg());
                        }
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
                .post();
    }

    @Override
    public void destroy() {
        timer.cancel();
    }
}
