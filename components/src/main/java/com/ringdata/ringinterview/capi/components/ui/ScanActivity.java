package com.ringdata.ringinterview.capi.components.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.ringdata.ringinterview.capi.components.R;

/**
 * @Author: bella_wang
 * @Description:
 * @Date: Create in 2020/4/13 15:30
 */
public class ScanActivity extends CaptureActivity {
    private DecoratedBarcodeView dbv; //条形码扫描视图
    private CaptureManager capture; //条形码扫描管理器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan);
        dbv = (DecoratedBarcodeView) findViewById(R.id.dbv);

        TextView backBtn = (TextView) findViewById(R.id.icTv_topbar_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        capture = new CaptureManager(this, dbv);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return dbv.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}
