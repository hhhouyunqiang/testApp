package com.ringdata.ringinterview.capi.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * @Author: bella_wang
 * @Description:
 * @Date: Create in 2020/5/13 15:10
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI wxapi;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_wxentry);

        wxapi = WXAPIFactory.createWXAPI(this, "wx09658b7d50b0fcee");
        wxapi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        wxapi.handleIntent(intent, this);
    }
    /**
     * 微信发送请求到第三方应用时，会回调到该方法
     */
    @Override
    public void onReq(BaseReq baseReq) {
        // 这里不作深究
    }

    /**
     * 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
     * app发送消息给微信，处理返回消息的回调
     */
    @Override
    public void onResp(BaseResp baseResp) {
        String result = null;
        switch (baseResp.errCode) {
            // 正确返回
            case BaseResp.ErrCode.ERR_OK:
                switch (baseResp.getType()) {
                    // ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX是微信分享，api自带
                    case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
                        // 只是做了简单的finish操作
                        result="分享成功";
                        finish();
                        break;
                    default:
                        finish();
                        break;
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result="分享取消";
                finish();
                break;
            default:
                // 错误返回
                switch (baseResp.getType()) {
                    // 微信分享
                    case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
                        Log.i("WXEntryActivity" , ">>>errCode = " + baseResp.errCode);
                        finish();
                        break;
                    default:
                        break;
                }
                result="分享失败";
                finish();
                break;
        }
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }

    private class PARAM_TITLE {
    }

    private class PARAM_TARGET_URL {
    }
}
