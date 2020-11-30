package com.ringdata.ringinterview.capi.components.ui.survey.questionnaire.qrc;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.base.net.RestClient;
import com.ringdata.base.net.callback.IError;
import com.ringdata.base.net.callback.ISuccess;
import com.ringdata.base.util.NetworkUtil;
import com.ringdata.base.util.ZXingUtil;
import com.ringdata.ringinterview.capi.components.App;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.ApiUrl;
import com.ringdata.ringinterview.capi.components.data.FileData;
import com.ringdata.ringinterview.capi.components.data.ResponseJson;
import com.ringdata.ringinterview.capi.components.helper.ShareHelper;
import com.ringdata.ringinterview.capi.components.utils.AppUtil;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import java.io.File;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by admin on 17/11/14.
 */

public class QuestionnaireQRCodeDelegate extends LatteDelegate {

    @BindView(R2.id.iv_questionnaire_qrcode)
    ImageView iv_QRCode;

    private Integer currentUserId;
    private String qrcCode;
    private ShareHelper mShareManager;
    private Bitmap qrBitmap;

    @Override
    public Object setLayout() {
        return R.layout.delegate_survey_questionnaire_qrcode;
    }

    public static QuestionnaireQRCodeDelegate newInstance(String qrcCode) {
        Bundle args = new Bundle();
        args.putInt("userId", SPUtils.getInstance().getInt(SPKey.USERID));
        args.putString("qrcCode", qrcCode);
        QuestionnaireQRCodeDelegate fragment = new QuestionnaireQRCodeDelegate();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        currentUserId = getArguments().getInt("userId");
        qrcCode = getArguments().getString("qrcCode");
        mShareManager = ShareHelper.getInstance(getContext());
    }

    @OnClick(R2.id.icTv_topbar_back)
    void onClickBack() {
        back();
    }

//    @OnClick(R2.id.btn_topbar_right)
//    void onClickRight() {
//        getCodeFromNet();
//    }

    @OnClick(R2.id.view_survey_qrc_share)
    void onClickShare() {
//        final String url = App.orgHost.substring(0, App.orgHost.length() - 4) + "8200/ringsurvey/w/" + qrcCode;
        final String url = App.orgHost + "/w/" + qrcCode;
        if (AppUtil.isPackageInstalled("com.tencent.mm")) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", qrcCode);
            RestClient.builder().url(App.orgHost + ApiUrl.SHARE_CAWI)
                    .raw(jsonObject.toJSONString())
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            ResponseJson responseJson = new ResponseJson(response);
                            if (responseJson.getSuccess()) {
                                JSONObject data = responseJson.getDataAsObject();
                                final String title = data.getString("title");
                                final String content = data.getString("desc");
                                String imageUrl = data.getString("imgUrl");
                                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon);
                                ShareHelper.ShareContentWebpage mShareContentText = (ShareHelper.ShareContentWebpage) mShareManager.getShareContentWebpage(title, content, url,imageUrl, bitmap);
                                mShareManager.shareByWebchat(mShareContentText, ShareHelper.WECHAT_SHARE_WAY_WEBPAGE);
//                                if (imageUrl != null && !"".equals(imageUrl)) {
//                                    AppUtil.url2Bitmap(mHandler, imageUrl);
//                                } else {
//
//                                }
                            }
                        }
                    })
                    .error(new IError() {
                        @Override
                        public void onError(int code, String msg) {
                            Log.e("ERROR", msg);
                        }
                    })
                    .build()
                    .post();
        } else {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("您还没有安装微信，请先安装微信客户端");
        }
    }

    @OnClick(R2.id.view_survey_qrc_copy)
    void onClickCopy() {
        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
//        String copyUrl = App.orgHost.substring(0, App.orgHost.length() - 4) + "8200/ringsurvey/w/" + qrcCode;
        String copyUrl = App.orgHost + "/w/" + qrcCode;
        if (TextUtils.isEmpty(copyUrl)) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("获取二维码失败");
            return;
        }
        //创建ClipData对象
        ClipData clipData = ClipData.newPlainText("simple text copy", copyUrl);
        //添加ClipData对象到剪切板中
        clipboardManager.setPrimaryClip(clipData);
        ToastUtils.setBgColor(Color.GRAY);
        ToastUtils.showShort("复制成功");
    }
    @OnClick(R2.id.view_qqsurvey_qrc_share)
    void onClickQQShare() {
        final String url = App.orgHost + "/w/" + qrcCode;
        if (AppUtil.isPackageInstalled("com.tencent.mobileqq")) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", qrcCode);
            RestClient.builder().url(App.orgHost + ApiUrl.SHARE_CAWI)
                    .raw(jsonObject.toJSONString())
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            ResponseJson responseJson = new ResponseJson(response);
                            if (responseJson.getSuccess()) {
                                JSONObject data = responseJson.getDataAsObject();
                                final String title = data.getString("title");
                                final String content = data.getString("desc");
                                String imageUrl = data.getString("imgUrl");
                                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon);
                                ShareHelper.ShareContentWebpage mShareContentText = (ShareHelper.ShareContentWebpage) mShareManager.getShareContentWebpage(title, content, url, imageUrl,bitmap);
                                mShareManager.onClickShare(getActivity(),mShareContentText,new BaseUiListener());
                            }
                        }
                    })
                    .error(new IError() {
                        @Override
                        public void onError(int code, String msg) {
                            Log.e("ERROR", msg);
                        }
                    })
                    .build()
                    .post();
        } else {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("您还没有安装微信，请先安装微信客户端");
        }




    }
    @OnClick(R2.id.view_survey_qrc_download)
    void onClickDownload() {
        String dir = FileData.getQuestionnaireDir(currentUserId);
        String fileName = qrcCode + ".jpg";
        if (AppUtil.bitmapToFile(qrBitmap, dir, fileName)) {
            // 其次把文件插入到系统图库
            File newFile = new File(dir, fileName);
            try {
                MediaStore.Images.Media.insertImage(getContext().getContentResolver(), newFile.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e) {
                ToastUtils.setBgColor(Color.GRAY);
                ToastUtils.showShort("同步到图库失败");
                e.printStackTrace();
            }
            // 最后通知图库更新
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(newFile);
            intent.setData(uri);
            getContext().sendBroadcast(intent);
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("保存成功，请至图库查看");
        } else {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("保存失败");
        }
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        if (!TextUtils.isEmpty(qrcCode)) {
            showORCImage(qrcCode);
            return;
        }
//        getCodeFromNet();
    }

    private void showORCImage(String code) {
        if (TextUtils.isEmpty(code)) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("二维码生成失败");
            return;
        }
//        String url = App.orgHost.substring(0, App.orgHost.length() - 4) + "8200/ringsurvey/w/" + code;
        String url = App.orgHost + "/w/" + code;
        qrBitmap = ZXingUtil.createQRCodeBitmap(url, 200, 200);
        iv_QRCode.setImageBitmap(qrBitmap);
    }

    private class BaseUiListener implements IUiListener {//QQ和Qzone分享回调

        @Override
        public void onCancel() {
            // TODO Auto-generated method stub
        }

        @Override
        public void onWarning(int i) {
        }

        @Override
        public void onComplete(Object arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onError(UiError arg0) {
            // TODO Auto-generated method stub

        }



    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareManager.getTencent().onActivityResultData(requestCode, resultCode, data, new BaseUiListener());
    }

    private void getCodeFromNet() {
        if (!NetworkUtil.isNetworkAvailable()) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("网络异常");
            return;
        }
//        HashMap<String, Object> hm = new HashMap<>();
//        hm.put("organizationId", SPUtils.getInstance().getInt(SPKey.ORG_ID));
//        hm.put("surveyId", SPUtils.getInstance().getInt(SPKey.SURVEY_ID));
//        hm.put("responseGuid", responseId);
//        hm.put("moduleId", modelId);
//        hm.put("sampleGuid", SPUtils.getInstance().getString(SPKey.SAMPLE_ID));
//        hm.put("questionnaireId", qid);
//        hm.put("userId", SPUtils.getInstance().getInt(SPKey.USERID));
//        RestClient.builder().tag(this).url(App.orgHost + ApiUrl.QRC)
//                .params("paramData", JSON.toJSONString(hm))
//                .loader(getActivity(), "刷新中")
//                .success(new ISuccess() {
//                    @Override
//                    public void onSuccess(String httpRresponse) {
//                        final ResponseJson responseJson = new ResponseJson(httpRresponse);
//                        if (responseJson.getSuccess()) {
//                            JSONObject jsonObject = responseJson.getDataAsObject();
//                            if (jsonObject == null) {
//                                ToastUtils.showShort("二维码获取失败");
//                                return;
//                            }
//                            String code = jsonObject.getString("code");
//                            showORCImage(code);
//                            if (!TextUtils.isEmpty(responseId)) {
//                                ResponseDao.updateQRC(code, SPUtils.getInstance().getInt(SPKey.USERID), responseId);
//                            }
//                        } else {
//                            ToastUtils.showShort(responseJson.getMsg());
//                        }
//                    }
//                })
//                .build()
//                .post();
    }
}
