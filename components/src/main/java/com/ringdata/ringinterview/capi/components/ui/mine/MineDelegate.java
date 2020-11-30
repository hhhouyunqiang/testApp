package com.ringdata.ringinterview.capi.components.ui.mine;

import android.Manifest;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.base.net.RestClient;
import com.ringdata.base.net.callback.ISuccess;
import com.ringdata.base.ui.camera.LatteCamera;
import com.ringdata.base.util.GUIDUtil;
import com.ringdata.base.util.callback.CallbackManager;
import com.ringdata.base.util.callback.CallbackType;
import com.ringdata.base.util.callback.IGlobalCallback;
import com.ringdata.ringinterview.capi.components.App;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.ApiUrl;
import com.ringdata.ringinterview.capi.components.data.FileData;
import com.ringdata.ringinterview.capi.components.data.ResponseJson;
import com.ringdata.ringinterview.capi.components.data.dao.UserDao;
import com.ringdata.ringinterview.capi.components.data.model.User;
import com.ringdata.ringinterview.capi.components.helper.GlideCircleTransform;
import com.ringdata.ringinterview.capi.components.ui.sign.signIn.SignInDelegate;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * Created by admin on 17/10/20.
 */

public class MineDelegate extends LatteDelegate {
    @BindView(R2.id.iv_mine_headimage)
    ImageView iv_headimage;
    @BindView(R2.id.tv_mine_name)
    TextView tv_name;

    private int userId = -1;
    private String headImageFilePath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = SPUtils.getInstance().getInt(SPKey.USERID);
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_mine;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }

    private void initView() {
        User user = UserDao.query(userId);
        if (user == null) {
            return;
        }
        tv_name.setText(user.getName());
        String filePath = SPUtils.getInstance().getString(SPKey.HEADIMAGE_FILEPATH);
        if (filePath != null && !"".equals(filePath)) {
            headImageFilePath = filePath;
            loadHeadImage(true);
        } else {
            headImageFilePath = user.getAvatarPath();
            loadHeadImage(false);
        }
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        initView();
    }

//    @OnClick(R2.id.ll_main_syncdata)
//    void onClickSyncData() {
//        getSupportDelegate().start(new SyncDataDelegate());
//    }

    @OnClick(R2.id.ll_mine_updatepassword)
    void onClickUpdatePassword() {
        getSupportDelegate().start(new UpdatePasswordDelegate());
    }

    @OnClick(R2.id.ll_mine_setting)
    void onClickSetting() {
        getSupportDelegate().start(new MSettingDelegate());
    }

    @OnClick(R2.id.ll_mine_about)
    void onClickAbout() {
        getSupportDelegate().start(new AboutDelegate());
    }

    @OnClick(R2.id.ll_mine_updateversion)
    void onClickCheckVersion() {

    }

    @OnClick(R2.id.iv_mine_headimage)
    void onClickTakePhoto(final View view) {
        if (!NetworkUtils.isAvailableByPing()) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("网络不可用！");
            return;
        }

        //开始照相机或选择图片
        CallbackManager.getInstance()
                .addCallback(CallbackType.ON_OVAL_CROP, new IGlobalCallback<File>() {
                    @Override
                    public void executeCallback(File args) {
                        final File file = (File) args;
                        RestClient.builder()
                                .loader(getActivity(), "头像上传中")
                                .url(App.orgHost + ApiUrl.USER_UPLOAD_HEADIMAGE)
                                .file(file)
                                .success(new ISuccess() {
                                    @Override
                                    public void onSuccess(String response) {
                                        ResponseJson responseJson = new ResponseJson(response);
                                        if (responseJson.getSuccess()) {
                                            headImageFilePath = Uri.fromFile(file).getPath();
                                            SPUtils.getInstance().put(SPKey.HEADIMAGE_FILEPATH, headImageFilePath);
                                            loadHeadImage(true);
                                        } else {
                                            ToastUtils.setBgColor(Color.GRAY);
                                            ToastUtils.showShort(responseJson.getMsg());
                                        }
                                    }
                                })
                                .build()
                                .uploadFile();
                    }
                });

        RxPermissions rxPermission = new RxPermissions(getActivity());
        rxPermission
                .request(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean permission) throws Exception {
                        if (permission) {
                            SPUtils.getInstance().put(SPKey.CAMERA_IF_HEADIMAGE, true);
                            LatteCamera.start(getActivity(), FileData.USER + GUIDUtil.getGuidStr() + ".png");
                        }
                    }
                });

    }

    private void loadHeadImage(boolean ifLocal) {
        String imagePath = "";
        if (ifLocal) {
            imagePath = headImageFilePath;
        } else {
            imagePath = App.orgHost + "/appapi" + headImageFilePath;
        }
        Glide.with(getActivity())
                .load(imagePath)
                .error(R.drawable.default_headimage)//失败
                .priority(Priority.IMMEDIATE)//设置下载优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new GlideCircleTransform(getActivity()))
                .into(iv_headimage);
    }

    @OnClick(R2.id.bt_mine_signout)
    void onClickSignOut() {
        RestClient.cancelAll();
        User user = UserDao.query(userId);
        if (user != null) {
            user.setPassword("");
            UserDao.replace(user);
            SPUtils.getInstance().put(SPKey.PASSWORD, "");
            getSupportDelegate().start(new SignInDelegate());
        } else {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("退出失败");
        }
    }

    @OnClick(R2.id.icTv_topbar_back)
    void onClickBack() {
        back();
    }
}
