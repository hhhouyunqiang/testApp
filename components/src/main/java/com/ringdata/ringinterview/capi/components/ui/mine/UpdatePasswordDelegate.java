package com.ringdata.ringinterview.capi.components.ui.mine;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.base.net.RestClient;
import com.ringdata.base.net.callback.IError;
import com.ringdata.base.net.callback.ISuccess;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.ApiUrl;
import com.ringdata.ringinterview.capi.components.data.ResponseJson;
import com.ringdata.ringinterview.capi.components.data.dao.UserDao;
import com.ringdata.ringinterview.capi.components.data.model.User;
import com.ringdata.ringinterview.capi.components.ui.sign.signIn.SignInDelegate;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by admin on 17/11/7.
 */

public class UpdatePasswordDelegate extends LatteDelegate {
    @BindView(R2.id.et_oldPassword)
    EditText et_oldPassword;

    @BindView(R2.id.et_newPassword)
    EditText et_newPassword;

    @BindView(R2.id.et_confirmPassword)
    EditText et_confirmPassword;

    @Override
    public Object setLayout() {
        return R.layout.delegate_mine_updatepassword;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
    }

    @OnClick(R2.id.icTv_topbar_back)
    void onClickBack() {
        back();
    }

    @OnClick(R2.id.bt_mine_updatepassword)
    void onClickUpdatePassword(){
        String oldPassword = et_oldPassword.getText().toString().trim();
        String newPassword = et_newPassword.getText().toString().trim();
        String confirmPassword = et_confirmPassword.getText().toString().trim();
        if(oldPassword.isEmpty()){
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("请输入原密码！");
            return;
        }
        if(newPassword.isEmpty()) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("请输入新密码！");
            return;
        }
        if(!newPassword.equals(confirmPassword)){
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("新密码和确认密码不一致！");
            return;
        }
        String token = SPUtils.getInstance().getString(SPKey.ACCESS_TOKEN);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("oldPass", oldPassword);
        jsonObject.put("newPass", newPassword);
        RestClient.builder().url(ApiUrl.UPDATE_PASSWORD)
                .header("Authorization", "Bearer " + token)
                .raw(jsonObject.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        ResponseJson responseJson = new ResponseJson(response);
                        if (responseJson.getSuccess()) {
                            ToastUtils.setBgColor(Color.GRAY);
                            ToastUtils.showShort(responseJson.getMsg());
                            User user = UserDao.query(SPUtils.getInstance().getInt(SPKey.USERID));
                            user.setPassword("");
                            UserDao.replace(user);
                            SPUtils.getInstance().put(SPKey.PASSWORD,"");
                            startWithPop(new SignInDelegate());
                        } else {
                            ToastUtils.setBgColor(Color.GRAY);
                            ToastUtils.showShort(responseJson.getMsg());
                        }
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        JSONObject error = JSONObject.parseObject(msg);
                        if (error.containsKey("message")) {
                            ToastUtils.setBgColor(Color.GRAY);
                            ToastUtils.showShort(error.getString("message"));
                        } else {
                            ToastUtils.setBgColor(Color.GRAY);
                            ToastUtils.showShort(msg);
                        }
                    }
                })
                .build()
                .post();
    }

}
