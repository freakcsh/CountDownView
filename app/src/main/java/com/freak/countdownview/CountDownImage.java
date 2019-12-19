package com.freak.countdownview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Freak on 2019/12/19.
 */
public class CountDownImage extends LinearLayout {
    private int width = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int height = ViewGroup.LayoutParams.WRAP_CONTENT;
    private ImageView mImageView;
    private static final int UPDATE_UI_CODE = 102;
    private boolean isContinue = false;// 是否开启倒计时
    private int timeStamp;// 倒计时时间（单位秒）
    private List<Integer> mList;
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();// 缓存线程池

    public CountDownImage(Context context) {
        this(context, null);
    }

    public CountDownImage(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CountDownImage);
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.CountDownImage_CountDownImageWidth) {
                width = typedArray.getDimensionPixelOffset(attr, width);
            } else if (attr == R.styleable.CountDownImage_CountDownImageHeight) {
                height = typedArray.getDimensionPixelOffset(attr, height);
            }
        }

        typedArray.recycle();
        mList = new ArrayList<>();
        // 设置总体布局属性
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER_VERTICAL);
        ViewGroup.LayoutParams params = new LayoutParams(width, height);
        mImageView = new ImageView(context);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        this.addView(mImageView);
    }

    /**
     * 设置倒计时-时间戳
     *
     * @param resources 倒计时时间
     * @return CountDownView
     */
    public CountDownImage setCountDownResource(@DrawableRes int... resources) {
        for (int resource : resources) {
            mList.add(resource);
        }
        this.timeStamp = resources.length;
        return this;
    }

    /**
     * 开启倒计时
     *
     * @return CountDownView
     */
    public CountDownImage startCountDown() {
        if (timeStamp <= 1) {
            this.isContinue = false;
        } else {
            this.isContinue = true;
            countDown();
        }
        return this;
    }

    /**
     * 实现倒计时的功能
     */
    private void countDown() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isContinue) {
                        isContinue = timeStamp-- >= 0;
                        Message message = new Message();
                        message.obj = timeStamp;
                        message.what = UPDATE_UI_CODE;
                        myHandler.sendMessage(message);
                        // 沉睡一秒
                        Thread.sleep(1000);
                    }
                    isContinue = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (mExecutorService == null || mExecutorService.isShutdown())
            mExecutorService = Executors.newCachedThreadPool();
        mExecutorService.execute(thread);
    }

    /**
     * 关闭倒计时
     *
     * @return CountDownView
     */
    public CountDownImage stopCountDown() {
        this.timeStamp = 0;
        this.mList.clear();
        return this;
    }

    /**
     * 销毁
     */
    public void destoryCountDownView() {
        if (mExecutorService != null)
            mExecutorService.shutdownNow();
        if (myHandler != null) {
            myHandler.removeCallbacksAndMessages(null);
            myHandler = null;
        }
    }

    private Handler myHandler = new CountDownImage.MyHandler(this);

    static class MyHandler extends Handler {
        // 定义一个对象用来引用Activity中的方法
        private final WeakReference<CountDownImage> mCountDownView;

        MyHandler(CountDownImage countDownView) {
            mCountDownView = new WeakReference<>(countDownView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CountDownImage currentCountDownView = mCountDownView.get();
            switch (msg.what) {
                case UPDATE_UI_CODE:// 刷新UI
                    if (msg.obj != null) {
                        int times = (int) msg.obj;
                        if (times >= 0) {
                            currentCountDownView.mImageView.setImageResource(currentCountDownView.mList.get(times));
                        }
                    }
                    // 倒计时结束
                    if (!currentCountDownView.isContinue) {
                        if (currentCountDownView.countDownEndListener != null)
                            currentCountDownView.countDownEndListener.onCountDownEnd();
                    }

                    break;
            }
        }
    }

    /**
     * 设置倒计时结束接口
     */
    public interface CountDownEndListener {
        void onCountDownEnd();
    }

    private CountDownEndListener countDownEndListener;

    public CountDownImage setCountDownEndListener(CountDownEndListener countDownEndListener) {
        this.countDownEndListener = countDownEndListener;
        return this;
    }
}
