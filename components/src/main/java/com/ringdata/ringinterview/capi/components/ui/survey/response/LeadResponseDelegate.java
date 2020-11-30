package com.ringdata.ringinterview.capi.components.ui.survey.response;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.ringinterview.capi.components.App;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.FileData;
import com.ringdata.ringinterview.capi.components.data.model.Response;
import com.ringdata.ringinterview.survey.SurveyAccess;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by admin on 2018/7/5.
 */

public class LeadResponseDelegate extends LatteDelegate {

    private int modeType;
    private String msg;
    private Response response;
    private String newVersionResponseId;
    @BindView(R2.id.iv_logo_lead_response)
    ImageView iv_lead_response;
    @BindView(R2.id.tv_lead_response)
    TextView tv_lead_response;

    @OnClick(R2.id.icTv_topbar_back)
    void onClickBack() {
        pop();
    }

    @OnClick(R2.id.bt_lead_response)
    void onClickBeginResponse() {
        String jsFilename = response.getUrl();
        if (StringUtils.isEmpty(jsFilename)) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("没有找到问卷文件！");
            return;
        }
        final File jsFile = new File(FileData.getQuestionnaireDir(SPUtils.getInstance().getInt(SPKey.USERID)) + jsFilename);
        if (!jsFile.exists()) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("问卷文件没有成功下载！");
            return;
        }
        Intent intent = new Intent(getContext(), ResponseActivity.class);
        intent.putExtra("msg", msg);
        intent.putExtra("id", response.getId());
        intent.putExtra("moduleId", response.getModuleId());
        intent.putExtra("moduleCode", response.getModuleCode());
        intent.putExtra("modeType", modeType);
        intent.putExtra("questionnaireId", response.getQuestionnaireId());
        intent.putExtra("newVersionResponseId", newVersionResponseId);
        SurveyAccess.instance.showSurvey(getContext(), intent, jsFile);
        pop();
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_lead_response;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        modeType = getArguments().getInt("modeType");
        msg = getArguments().getString("msg");
        response = (Response) getArguments().getSerializable("response");
        newVersionResponseId = getArguments().getString("newVersionResponseId");
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        String url = App.orgHost + SPUtils.getInstance().getString(SPKey.LOGOURL);
        Glide.with(getContext())
                .load(url)
                //.error(R.drawable.logo)//失败
                .priority(Priority.HIGH)//设置下载优先级
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .thumbnail(0.1f)
                .into(iv_lead_response);

    }

    public static LeadResponseDelegate newInstance(Integer modeType, String msg, Response response, String newVersionResposneId) {

        Bundle args = new Bundle();
        args.putSerializable("response", response);
        args.putInt("modeType", modeType);
        args.putString("msg", msg);
        args.putString("newVersionResponseId", newVersionResposneId);
        LeadResponseDelegate fragment = new LeadResponseDelegate();
        fragment.setArguments(args);
        return fragment;
    }
}
