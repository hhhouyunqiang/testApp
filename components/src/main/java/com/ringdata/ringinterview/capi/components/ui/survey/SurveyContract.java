package com.ringdata.ringinterview.capi.components.ui.survey;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.BasePresenter;
import com.ringdata.base.BaseView;

import java.util.List;

/**
 * Created by admin on 2018/6/25.
 */

public interface SurveyContract {
    interface Presenter extends BasePresenter {

        void getSurveyListFromNet();

        void getSurveyListFromLocal();

        void querySurveyListFromNet(String keyword, String type, int status, int sort);

        void queryListByKeyword(String keyword);

        void queryListByType(String type);

        void queryListByStatus(int status);

        void queryListByOrder(int sort);

        void scanLogIn(String scanKey);

        void joinProject(String scanKey);

    }

    interface View extends BaseView {

        void initMyView();

        void initTitleView(String title);
        
        void refreshTopRightMessageView(int count);

        void showSurveyList(List<MultiItemEntity> list);

        void showSyncErrorView(String msg);

        void showNetErrorView(String title, String msg);

    }
}
