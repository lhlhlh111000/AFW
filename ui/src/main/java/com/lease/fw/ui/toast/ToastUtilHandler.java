package com.lease.fw.ui.toast;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * 处理非UI线程下的吐司提示
 * created time: 2019-11-08
 * author: cqt
 * description:
 */
public class ToastUtilHandler extends Handler {

    static final int MSG_POST_TOAST = 1;

    ToastUtilHandler(Looper mainLooper) {
        super(mainLooper);
    }

    @Override
    public void handleMessage(Message msg) {
        ToastInfo info = (ToastInfo) msg.obj;
        switch (msg.what) {
            case MSG_POST_TOAST:
                ToastUtil.show(info.text, info.durationLong, info.mode);
                break;
        }
    }
}
