package com.lease.fw.ui.toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lease.fw.ui.R;
import com.lease.fw.ui.UICentre;
import com.lease.fw.ui.config.ToastConfig;

/**
 * 统一的吐司提示工具类
 * created time: 2019-11-08
 * author: cqt
 * description: 根据配置信息展示吐司提示
 */
public class ToastUtil {

    private static final String TAG = "ToastUtil";

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;
    private static boolean initialized;
    private static Mode sDefaultMode;
    private static ToastUtilHandler sHandler;
    private static Toast sToast;
    private static int sDuration;

    private ToastUtil() {}

    /**
     * Initialize util.
     */
    private static void initialize() {
        if (initialized) {
            return;
        }

        if (UICentre.getInstance().getContext() == null) {
            throw new NullPointerException("When you want to use ToastUtil, " +
                    "you must setup context by UICentre.getInstance().withContext at first.");
        }

        sContext = UICentre.getInstance().getContext().getApplicationContext();
        sDefaultMode = UICentre.getInstance().getUiConfig().getToastConfig().getMode();
        sHandler = new ToastUtilHandler(Looper.getMainLooper());
        initialized = true;
    }

    /**
     * Display mode
     */
    public enum Mode {

        /**
         * Show as a normal toast. This mode could be user-definable.  This is the default.
         */
        NORMAL,

        /**
         * When the toast is shown to the user , the text will be replaced if call the show() method again.  This mode could be user-definable.
         */
        REPLACEABLE
    }

    /**
     * Show a toast with the text form a resource.
     *
     * @param resId The resource id of the string resource to use.
     */
    public static void show(int resId) {
        initialize();
        show(sContext.getText(resId), false, sDefaultMode);
    }

    /**
     * Show a toast.
     *
     * @param text The text to show.
     */
    public static void show(CharSequence text) {
        initialize();
        show(text, false, sDefaultMode);
    }

    /**
     * Show a toast with the text form a resource.
     *
     * @param resId        The resource id of the string resource to use.
     * @param durationLong Whether the toast show for a long period of time?
     */
    public static void show(int resId, boolean durationLong) {
        initialize();
        show(sContext.getText(resId), durationLong, sDefaultMode);
    }

    /**
     * Show a toast.
     *
     * @param text         The text to show.
     * @param durationLong Whether the toast show for a long period of time?
     */
    public static void show(CharSequence text, boolean durationLong) {
        initialize();
        show(text, durationLong, sDefaultMode);
    }

    /**
     * Show a toast with the text form a resource.
     *
     * @param resId The resource id of the string resource to use.
     * @param mode  The display mode to use.  Either {@link Mode#NORMAL} or {@link Mode#REPLACEABLE}
     */
    public static void show(int resId, Mode mode) {
        initialize();
        show(sContext.getText(resId), false, mode);
    }

    /**
     * Show a toast.
     *
     * @param text The text to show.
     * @param mode The display mode to use.  Either {@link Mode#NORMAL} or {@link Mode#REPLACEABLE}
     */
    public static void show(CharSequence text, Mode mode) {
        initialize();
        show(text, false, mode);
    }

    /**
     * Show a toast with the text form a resource.
     *
     * @param resId        resId The resource id of the string resource to use.
     * @param durationLong Whether the toast show for a long period of time?
     * @param mode         The display mode to use.  Either {@link Mode#NORMAL} or {@link Mode#REPLACEABLE}
     */
    public static void show(int resId, boolean durationLong, Mode mode) {
        initialize();
        show(sContext.getText(resId), durationLong, mode);
    }

    /**
     * Show a toast.
     *
     * @param text         The text to show.
     * @param durationLong Whether the toast show for a long period of time?
     * @param mode         The display mode to use.  Either {@link Mode#NORMAL} or {@link Mode#REPLACEABLE}
     */
    @SuppressLint("ShowToast")
    public static void show(CharSequence text, boolean durationLong, Mode mode) {
        final int duration = durationLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        if(checkIsMainThread()) {
            if (mode != Mode.REPLACEABLE) {
                createToast(text, duration).show();
                return;
            }

            if (sToast == null || sDuration != duration) {
                sDuration = duration;
                sToast = createToast(text, duration);
            } else {
                try {
                    if(UICentre.getInstance().getUiConfig()
                            .getToastConfig().isCustomRes()) {
                        ((TextView) sToast.getView().findViewById(R.id.toast_text_id))
                                .setText(text);
                    }else {
                        sToast.setText(text);
                    }
                } catch (RuntimeException e) {
                    sToast = createToast(text, duration);
                }
            }
            sToast.show();
        }else {
            final ToastInfo toastInfo = new ToastInfo();
            toastInfo.durationLong = durationLong;
            toastInfo.mode = mode;
            toastInfo.text = text;
            sHandler.obtainMessage(ToastUtilHandler.MSG_POST_TOAST, toastInfo).sendToTarget();
        }
    }

    /**
     * check current Thread is UI Thread
     * @return
     */
    private static boolean checkIsMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    /**
     * Create toast view.
     * @param text Toast show text.
     * @param duration Toast show duration.
     * @return Toast view.
     */
    @SuppressLint("ShowToast")
    private static Toast createToast(CharSequence text, int duration) {
        ToastConfig toastConfig = UICentre.getInstance().getUiConfig().getToastConfig();
        Toast toast;
        if(toastConfig.isCustomRes()
                && toastConfig.getCustomResId() > 0) {
            // Custom show toast view
            View customView = LayoutInflater.from(sContext).inflate(toastConfig.getCustomResId(), null);
            TextView tvText = customView.findViewById(R.id.toast_text_id);
            toast = new Toast(sContext);
            toast.setDuration(duration);
            toast.setView(customView);
            tvText.setText(text);
        }else {
            toast = Toast.makeText(sContext, text, duration);
        }

        toast.setGravity(toastConfig.getGravity(), 0, 0);
        return toast;
    }
}
