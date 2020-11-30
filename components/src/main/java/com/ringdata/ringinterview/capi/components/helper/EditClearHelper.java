package com.ringdata.ringinterview.capi.components.helper;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

/**
 * Created by admin on 17/12/8.
 */

public class EditClearHelper {

    public static void init(final EditText editText, final View clearView) {

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                if(editText.getText().length()!=0){
                    clearView.setVisibility(View.VISIBLE);
                } else {
                    clearView.setVisibility(View.INVISIBLE);
                }
            }
        });
        /**焦点变化监听**/
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                // TODO Auto-generated method stub
                if(editText.getText().length()!=0){
                    //删除图标显示
                    clearView.setVisibility(View.VISIBLE);
                } else {
                    //删除图标隐藏
                    clearView.setVisibility(View.INVISIBLE);
                }
                if(arg1){
                    //得到焦点
                } else {
                    //失去焦点，删除图标隐藏
                    clearView.setVisibility(View.INVISIBLE);
                }
            }
        });

        //删除图标的点击监听事件
        clearView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // 执行清空EditText数据
                editText.setText("");
            }
        });
    }
}
