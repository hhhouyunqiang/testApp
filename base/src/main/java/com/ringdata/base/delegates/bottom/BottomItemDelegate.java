package com.ringdata.base.delegates.bottom;


import com.ringdata.base.delegates.LatteDelegate;

public abstract class BottomItemDelegate extends LatteDelegate {
    // 再点一次退出程序时间设置
    private static final long WAIT_TIME = 1000L;
    private long TOUCH_TIME = 0;

    public final boolean onBackPressedSupport() {
        _mActivity.moveTaskToBack(true);
//        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
//            _mActivity.finish();
//        } else {
//            TOUCH_TIME = System.currentTimeMillis();
//            Toast.makeText(_mActivity, "双击退出" + Latte.getApplicationContext().getString(R.string.app_name), Toast.LENGTH_SHORT).show();
//        }

        return true;
    }
}
