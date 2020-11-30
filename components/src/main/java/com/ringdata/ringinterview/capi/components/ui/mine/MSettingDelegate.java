package com.ringdata.ringinterview.capi.components.ui.mine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.CacheUtils;
import com.blankj.utilcode.util.CleanUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.SPUtils;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.DaoUtil;
import com.ringdata.ringinterview.capi.components.data.FileData;
import com.ringdata.ringinterview.capi.components.ui.sign.signIn.SignInDelegate;

import java.io.File;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by admin on 17/11/7.
 */

public class MSettingDelegate extends LatteDelegate {

    @BindView(R2.id.sc_mine_setting_save_account)
    SwitchCompat sc_SaveAccount;
    @BindView(R2.id.sc_mine_setting_auto_syncdata)
    SwitchCompat sc_AutoSyncData;
    @BindView(R2.id.sc_mine_setting_media_wifi)
    SwitchCompat sc_MediaData;
    @BindView(R2.id.tv_data_size)
    TextView tvDataSize;

    @OnClick(R2.id.icTv_topbar_back)
    void onClickBack() {
        back();
    }

    @OnClick(R2.id.rl_clear_data)
    void onClickClearData() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CleanUtils.deleteFilesInDir(FileData.ROOT);
                        DaoUtil.clearLocalData();
                        //DBOperation.init(getContext().getApplicationContext());
                        //DBOperation.instanse.openDataBase();
                        //DBOperation.instanse.createLocalTables(TableSQL.tableSqlHashMap());
                        startWithPop(new SignInDelegate());
                    }
                }).create();

        dialog.setCancelable(false);
        dialog.setTitle("清除本地数据（不可逆）");
        dialog.setMessage("请谨慎操作");
        dialog.show();
    }

    private void refreshDataSizeView() {
        tvDataSize.setText(getDataSize());
    }

    private String getDataSize() {
        long dataSize = CacheUtils.getInstance(new File(FileData.ROOT)).getCacheSize();
        long dbSize = CacheUtils.getInstance(new File(FileData.DBPath)).getCacheSize();
        ConvertUtils.byte2FitMemorySize(dataSize + dbSize);
        return ConvertUtils.byte2FitMemorySize(dataSize);
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_mine_setting;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }

    private void initView() {
        if (SPUtils.getInstance().getBoolean(SPKey.SETTING_SAVE_ACCOUNT)) {
            sc_SaveAccount.setChecked(true);
        } else {
            sc_SaveAccount.setChecked(false);
        }

        if (SPUtils.getInstance().getBoolean(SPKey.SETTING_AUTO_SYNC_DATA)) {
            sc_AutoSyncData.setChecked(true);
        } else {
            sc_AutoSyncData.setChecked(false);
        }

        if (SPUtils.getInstance().getBoolean(SPKey.SETTING_SYNC_MEDIA_WIFI)) {
            sc_MediaData.setChecked(true);
        } else {
            sc_MediaData.setChecked(false);
        }

        tvDataSize.setText(getDataSize());
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        initView();
    }

    @OnCheckedChanged(R2.id.sc_mine_setting_save_account)
    void saveAccount(SwitchCompat view) {
        if (view.isChecked()) {
            sc_SaveAccount.setChecked(true);
            SPUtils.getInstance().put(SPKey.SETTING_SAVE_ACCOUNT, true);
        } else {
            sc_SaveAccount.setChecked(false);
            SPUtils.getInstance().put(SPKey.SETTING_SAVE_ACCOUNT, false);
            SPUtils.getInstance().put(SPKey.PASSWORD, "");
        }
    }

    @OnCheckedChanged(R2.id.sc_mine_setting_auto_syncdata)
    void autoSyncData(SwitchCompat view) {
        if (view.isChecked()) {
            sc_AutoSyncData.setChecked(true);
            SPUtils.getInstance().put(SPKey.SETTING_AUTO_SYNC_DATA, true);
        } else {
            sc_AutoSyncData.setChecked(false);
            SPUtils.getInstance().put(SPKey.SETTING_AUTO_SYNC_DATA, false);
        }
    }

    @OnCheckedChanged(R2.id.sc_mine_setting_media_wifi)
    void autoMediaWifi(SwitchCompat view) {
        if (view.isChecked()) {
            sc_MediaData.setChecked(true);
            SPUtils.getInstance().put(SPKey.SETTING_SYNC_MEDIA_WIFI, true);
        } else {
            sc_MediaData.setChecked(false);
            SPUtils.getInstance().put(SPKey.SETTING_SYNC_MEDIA_WIFI, false);
        }
    }
}
