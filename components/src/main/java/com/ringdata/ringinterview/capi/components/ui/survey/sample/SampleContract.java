package com.ringdata.ringinterview.capi.components.ui.survey.sample;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.BasePresenter;
import com.ringdata.base.BaseView;
import com.ringdata.ringinterview.capi.components.data.model.Sample;
import com.ringdata.ringinterview.capi.components.data.model.Survey;

import java.util.List;

/**
 * Created by admin on 2018/6/25.
 */

public interface SampleContract {

    interface Presenter extends BasePresenter {

        void getSampleListFromNet();

        void getSampleListFromLocal();

        void querySampleListFromNet(String query, int status, int sort);

        void queryFromLocal(String query);

        void queryListByStatus(int status);

        void queryListByOrder(int sort);

        void checkIsCanDelete(Sample sample);

        void deleteSample(Sample sample);

    }

    interface View extends BaseView {

        void initMyView();

        void initSurveyView(int id, String name, int status, String type, Survey survey);

        void showSampleAddView(boolean canAdd, int assign, String surveyType);

        void showDeleteAlertCommit(Sample sample);

        void showSampleList(List<MultiItemEntity> list);

        void showNetErrorView(String msg);

        void toast(String msg);

    }
}
