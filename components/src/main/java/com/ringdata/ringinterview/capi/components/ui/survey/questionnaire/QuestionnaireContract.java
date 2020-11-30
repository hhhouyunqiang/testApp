package com.ringdata.ringinterview.capi.components.ui.survey.questionnaire;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.BasePresenter;
import com.ringdata.base.BaseView;
import com.ringdata.ringinterview.capi.components.data.model.Questionnaire;
import com.ringdata.ringinterview.capi.components.data.model.Response;
import com.ringdata.ringinterview.capi.components.data.model.Sample;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018/6/28.
 */

public interface QuestionnaireContract {

    interface Presenter extends BasePresenter {

        void syncData();

        void updateSample(Sample sample);

        void deleteSubResponse(Response response);

        void getSubQuestionnaireListFromLocal(Integer groupId);

        void getMainQuestionnaireListFromLocal(Integer groupId);

        void getCurrentListFromLocal(Integer groupId);

        void addSubResponse(Questionnaire questionnaire);

        void openSampleStatus();

        boolean setSampleStatusFinish();

        void openResponse(Response response);

        void openResponseWithType(Response response, int type);

    }

    interface View extends BaseView {

        void initMyView(List<Questionnaire> cats);

        void initTitleView();

        void initTitleTableView();

        void initRightView(boolean visible);

        void initSampleInfoView(int status, String remarks, String visitTime);

        void showQuestionnaireList(List<MultiItemEntity> list);

        void showBottomMenu(ArrayList options, Response response);

        void showResponseActivity(Integer modeType, String msg, Response response, String newVersionResponseId);

        void showLeadResponseDelegate(Integer modeType, String msg, Response response, String newVersionResponseId);

        void showNetErrorView(String msg);

        void showDeleteAlertCommit(Response response);

        void showSampleCommitDialog(final Sample sample);

        void refreshTitleView(int tabType);

    }
}
