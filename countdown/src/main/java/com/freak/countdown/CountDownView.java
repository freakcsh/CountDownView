package com.freak.countdown;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 自定义倒计时View
 * <p>
 * 使用方法
 * countdownView.setCountTime(3660) // 设置倒计时时间戳
 * .setHourTvBackgroundRes(R.mipmap.panicbuy_time)
 * .setHourTvTextColorHex("#FFFFFF")
 * .setHourTvGravity(CountDownView.CountDownViewGravity.GRAVITY_CENTER)
 * .setHourTvTextSize(21)
 * <p>
 * .setHourColonTvBackgroundColorHex("#00FFFFFF")
 * .setHourColonTvSize(18, 0)
 * <p>
 * .setHourColonTvTextColorHex("#FF7198")
 * .setHourColonTvGravity(CountDownView.CountDownViewGravity.GRAVITY_CENTER)
 * .setHourColonTvTextSize(21)
 * <p>
 * .setMinuteTvBackgroundRes(R.mipmap.panicbuy_time)
 * .setMinuteTvTextColorHex("#FFFFFF")
 * .setMinuteTvTextSize(21)
 * <p>
 * .setMinuteColonTvSize(18, 0)
 * .setMinuteColonTvTextColorHex("#FF7198")
 * .setMinuteColonTvTextSize(21)
 * <p>
 * .setSecondTvBackgroundRes(R.mipmap.panicbuy_time)
 * .setSecondTvTextColorHex("#FFFFFF")
 * .setSecondTvTextSize(21)
 * <p>
 * //                .setTimeTvWH(18, 40)
 * //                .setColonTvSize(30)
 * <p>
 * // 开启倒计时
 * .startCountDown()
 * <p>
 * // 设置倒计时结束监听
 * .setCountDownEndListener(new CountDownView.CountDownEndListener() {
 *
 * @Override public void onCountDownEnd() {
 * Toast.makeText(MainActivity.this, "倒计时结束", Toast.LENGTH_SHORT).show();
 * }
 * });
 * <p>
 * 暂停倒计时
 * countdownView.pauseCountDown();
 * <p>
 * 停止倒计时
 * countdownView.stopCountDown();
 */
public class CountDownView extends LinearLayout {
    private final String TAG = "CountDownView";
    private static final int UPDATE_UI_CODE = 101;

    public enum CountDownViewGravity {
        GRAVITY_CENTER,
        GRAVITY_LEFT,
        GRAVITY_RIGHT,
        GRAVITY_TOP,
        GRAVITY_BOTTOM
    }

    private Context context;
    private TextView hourTextView, minuteTextView, secondTextView, hourColonTextView, minuteColonTextView, secondColonTextView;
    private long timeStamp;// 倒计时时间戳
    private boolean isContinue = false;// 是否开启倒计时
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();// 缓存线程池
    //小时与分钟分隔文字 例如19:20
    private final String mChineseHourSeparate = "小时";
    //分钟与秒钟的分隔文字
    private final String mChineseMinuteSeparate = "分钟";
    //秒钟后面的显示文字，只针对中文分隔时才显示
    private final String mChineseSecondSeparate = "秒";

    //小时与分钟分隔文字 例如19:20
    private final String mHourSeparate = ":";
    //分钟与秒钟的分隔文字
    private final String mMinuteSeparate = ":";
    //秒钟后面的显示文字，只针对中文分隔时才显示
    private final String mSecondSeparate = "";

    private int separateType = symbol;
    private static final int symbol = 0;
    private static final int chinese = 1;

    private int gravity = Gravity.CENTER;
    private static final int GRAVITY_CENTER = 0;
    private static final int GRAVITY_LEFT = 1;
    private static final int GRAVITY_RIGHT = 2;
    private static final int GRAVITY_TOP = 3;
    private static final int GRAVITY_BOTTOM = 4;

    private int separateTextColor = Color.BLACK;
    private int separateTextSize = 12;
    private int timeTextColor = Color.BLACK;
    private int timeTextSize = 12;
    private int hourBackground;
    private int minuteBackground;
    private int secondBackground;
    private int separateBackground;

    private int separateTextStyle = Typeface.NORMAL;
    private int timeTextStyle = Typeface.NORMAL;

    private int timeWidth = 50;
    private int timeHeight = 50;

    private int separateWidth;
    private int separateHeight;

    public CountDownView(Context context) {
        this(context, null);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context, attrs);
    }

    // 初始化方法
    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CountDownView);
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.CountDownView_CountDownViewSeparateType) {
                separateType = typedArray.getInt(attr, separateType);
            } else if (attr == R.styleable.CountDownView_CountDownViewTextGravity) {
                gravity = typedArray.getInt(attr, gravity);
            } else if (attr == R.styleable.CountDownView_CountDownViewSeparateTextColor) {
                separateTextColor = typedArray.getColor(attr, separateTextColor);
            } else if (attr == R.styleable.CountDownView_CountDownViewTimeTextColor) {
                timeTextColor = typedArray.getColor(attr, timeTextColor);
            } else if (attr == R.styleable.CountDownView_CountDownViewTimeTextSize) {
                timeTextSize = typedArray.getDimensionPixelSize(attr, timeTextSize);
            } else if (attr == R.styleable.CountDownView_CountDownViewSeparateTextSize) {
                separateTextSize = typedArray.getDimensionPixelSize(attr, separateTextSize);
            } else if (attr == R.styleable.CountDownView_CountDownViewHourBackground) {
                hourBackground = typedArray.getResourceId(attr, hourBackground);
            } else if (attr == R.styleable.CountDownView_CountDownViewMinuteBackground) {
                minuteBackground = typedArray.getResourceId(attr, minuteBackground);
            } else if (attr == R.styleable.CountDownView_CountDownViewSecondBackground) {
                secondBackground = typedArray.getResourceId(attr, secondBackground);
            } else if (attr == R.styleable.CountDownView_CountDownViewTimeTextStyle) {
                timeTextStyle = typedArray.getInt(attr, timeTextStyle);
            } else if (attr == R.styleable.CountDownView_CountDownViewSeparateTextStyle) {
                separateTextStyle = typedArray.getInt(attr, separateTextStyle);
            } else if (attr == R.styleable.CountDownView_CountDownViewSeparateBackground) {
                separateBackground = typedArray.getResourceId(attr, separateBackground);
            } else if (attr == R.styleable.CountDownView_CountDownViewTimeWidth) {
                timeWidth = typedArray.getDimensionPixelOffset(attr, timeWidth);
            } else if (attr == R.styleable.CountDownView_CountDownViewTimeHeight) {
                timeHeight = typedArray.getDimensionPixelOffset(attr, timeHeight);
            }
        }
        switch (gravity) {
            case GRAVITY_CENTER:
                gravity = Gravity.CENTER;
                break;
            case GRAVITY_LEFT:
                gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                break;
            case GRAVITY_RIGHT:
                gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                break;
            case GRAVITY_TOP:
                gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                break;
            case GRAVITY_BOTTOM:
                gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                break;
        }
        switch (timeTextStyle) {
            case 0:
                timeTextStyle = Typeface.BOLD;
                break;
            case 1:
                timeTextStyle = Typeface.NORMAL;
                break;
            case 2:
                timeTextStyle = Typeface.ITALIC;
                break;
            default:
                break;
        }
        switch (separateTextStyle) {
            case 0:
                separateTextStyle = Typeface.BOLD;
                break;
            case 1:
                separateTextStyle = Typeface.NORMAL;
                break;
            case 2:
                separateTextStyle = Typeface.ITALIC;
                break;
            default:
                break;
        }
        // 设置总体布局属性
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER_VERTICAL);
        ViewGroup.LayoutParams params = new LayoutParams(timeWidth, timeHeight);
        // 添加子控件
        // 小时控件
        hourTextView = new TextView(this.context);
        hourTextView.setTextColor(timeTextColor);
        hourTextView.setBackgroundResource(hourBackground);
        hourTextView.setTextSize(timeTextSize);
        hourTextView.setIncludeFontPadding(false);
        hourTextView.setGravity(gravity);
        hourTextView.setTypeface(Typeface.defaultFromStyle(timeTextStyle));
        hourTextView.setLayoutParams(params);
        this.addView(hourTextView);
        // 小时冒号控件
        hourColonTextView = new TextView(this.context);
        hourColonTextView.setTextColor(separateTextColor);
        hourColonTextView.setBackgroundResource(separateBackground);
        hourColonTextView.setTextSize(separateTextSize);
        hourColonTextView.setText((separateType == 0 ? mHourSeparate : mChineseHourSeparate));
        hourColonTextView.setGravity(gravity);
        hourColonTextView.setIncludeFontPadding(false);
        hourColonTextView.setTypeface(Typeface.defaultFromStyle(separateTextStyle));
        this.addView(hourColonTextView);
        // 分钟控件
        minuteTextView = new TextView(this.context);
        minuteTextView.setTextColor(timeTextColor);
        minuteTextView.setBackgroundResource(minuteBackground);
        minuteTextView.setTextSize(timeTextSize);
        minuteTextView.setGravity(gravity);
        minuteTextView.setIncludeFontPadding(false);
        minuteTextView.setTypeface(Typeface.defaultFromStyle(timeTextStyle));
        minuteTextView.setLayoutParams(params);
        this.addView(minuteTextView);
        // 分钟冒号控件
        minuteColonTextView = new TextView(this.context);
        minuteColonTextView.setTextColor(separateTextColor);
        minuteColonTextView.setBackgroundResource(separateBackground);
        minuteColonTextView.setTextSize(separateTextSize);
        minuteColonTextView.setText((separateType == 0 ? mMinuteSeparate : mChineseMinuteSeparate));
        minuteColonTextView.setGravity(gravity);
        minuteColonTextView.setIncludeFontPadding(false);
        minuteColonTextView.setTypeface(Typeface.defaultFromStyle(separateTextStyle));
        this.addView(minuteColonTextView);
        // 秒控件
        secondTextView = new TextView(this.context);
        secondTextView.setTextColor(timeTextColor);
        secondTextView.setBackgroundResource(secondBackground);
        secondTextView.setTextSize(timeTextSize);
        secondTextView.setGravity(gravity);
        secondTextView.setIncludeFontPadding(false);
        secondTextView.setTypeface(Typeface.defaultFromStyle(timeTextStyle));
        secondTextView.setLayoutParams(params);
        this.addView(secondTextView);
        if (separateType != 0) {
            secondColonTextView = new TextView(context);
            secondColonTextView.setTextColor(separateTextColor);
            secondColonTextView.setBackgroundResource(separateBackground);
            secondColonTextView.setTextSize(separateTextSize);
            secondColonTextView.setText((mChineseSecondSeparate));
            secondColonTextView.setGravity(gravity);
            secondColonTextView.setIncludeFontPadding(false);
            secondColonTextView.setTypeface(Typeface.defaultFromStyle(separateTextStyle));
            this.addView(secondColonTextView);
        }
    }

    /**
     * 设置时间控件尺寸
     *
     * @param width  宽 0取默认值
     * @param height 高 0取默认值
     * @return CountDownView
     */
    public CountDownView setTimeTvWH(int width, int height) {
        if (width > 0 && height > 0) {
            ViewGroup.LayoutParams params = new LayoutParams(width, height);
            hourTextView.setLayoutParams(params);
            minuteTextView.setLayoutParams(params);
            secondTextView.setLayoutParams(params);
        }
        return this;
    }

    /**
     * 设置时间控件字体大小
     *
     * @param size 字体大小
     * @return CountDownView
     */
    public CountDownView setTimeTvSize(float size) {
        hourTextView.setTextSize(size);
        minuteTextView.setTextSize(size);
        secondTextView.setTextSize(size);
        return this;
    }

    /**
     * 设置时间控件字体颜色
     *
     * @param colorHex 字体颜色十六进制 “#FFFFFF”
     * @return CountDownView
     */
    public CountDownView setTimeTvTextColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        hourTextView.setTextColor(color);
        minuteTextView.setTextColor(color);
        secondTextView.setTextColor(color);
        return this;
    }

    /**
     * 设置时间控件背景
     *
     * @param colorHex 背景颜色16进制“#FFFFFF”
     * @return CountDownView
     */
    public CountDownView setTimeTvBackgroundColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        hourTextView.setBackgroundColor(color);
        minuteTextView.setBackgroundColor(color);
        secondTextView.setBackgroundColor(color);
        return this;
    }

    /**
     * 设置时间控件背景
     *
     * @param res 背景资源ID
     * @return CountDownView
     */
    public CountDownView setTimeTvBackgroundRes(int res) {
        hourTextView.setBackgroundResource(res);
        minuteTextView.setBackgroundResource(res);
        secondTextView.setBackgroundResource(res);
        return this;
    }

    /**
     * 修改时间控件背景
     *
     * @param drawable 背景资源Drawable
     * @return CountDownView
     */
    public CountDownView setTimeTvBackground(Drawable drawable) {
        if (drawable != null) {
            hourTextView.setBackground(drawable);
            minuteTextView.setBackground(drawable);
            secondTextView.setBackground(drawable);
        }
        return this;
    }

    /**
     * 设置时间控件内部字体位置 - 默认居中
     *
     * @param countDownViewGravity 左上右下中
     */
    public CountDownView setTimeTvGravity(CountDownViewGravity countDownViewGravity) {
        int gravity = Gravity.CENTER;
        if (countDownViewGravity == CountDownViewGravity.GRAVITY_BOTTOM) {
            gravity = Gravity.BOTTOM;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_CENTER) {
            gravity = Gravity.CENTER;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_LEFT) {
            gravity = Gravity.START;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_RIGHT) {
            gravity = Gravity.END;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_TOP) {
            gravity = Gravity.TOP;
        }
        hourTextView.setGravity(gravity);
        minuteTextView.setGravity(gravity);
        secondTextView.setGravity(gravity);
        return this;
    }


    /**
     * 设置冒号控件尺寸
     *
     * @param width  宽 0取默认值
     * @param height 高 0取默认值
     * @return CountDownView
     */
    public CountDownView setColonTvWH(int width, int height) {
        ViewGroup.LayoutParams params = new LayoutParams(width, height);
        hourColonTextView.setLayoutParams(params);
        minuteColonTextView.setLayoutParams(params);
        return this;
    }

    /**
     * 设置冒号控件字体大小
     *
     * @param size 字体大小
     * @return CountDownView
     */
    public CountDownView setColonTvSize(float size) {
        hourColonTextView.setTextSize(size);
        minuteColonTextView.setTextSize(size);
        return this;
    }

    /**
     * 设置冒号控件字体颜色
     *
     * @param colorHex 字体颜色十六进制 “#FFFFFF”
     * @return CountDownView
     */
    public CountDownView setColonTvTextColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        hourColonTextView.setTextColor(color);
        minuteColonTextView.setTextColor(color);
        return this;
    }

    /**
     * 设置冒号控件背景
     *
     * @param colorHex 背景颜色16进制“#FFFFFF”
     * @return CountDownView
     */
    public CountDownView setColonTvBackgroundColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        hourColonTextView.setBackgroundColor(color);
        minuteColonTextView.setBackgroundColor(color);
        return this;
    }

    /**
     * 设置冒号控件背景
     *
     * @param res 背景资源ID
     * @return CountDownView
     */
    public CountDownView setColonTvBackgroundRes(int res) {
        hourColonTextView.setBackgroundResource(res);
        minuteColonTextView.setBackgroundResource(res);
        return this;
    }

    /**
     * 修改冒号控件背景
     *
     * @param drawable 背景资源Drawable
     * @return CountDownView
     */
    public CountDownView setColonTvBackground(Drawable drawable) {
        if (drawable != null) {
            hourColonTextView.setBackground(drawable);
            minuteColonTextView.setBackground(drawable);
        }
        return this;
    }

    /**
     * 设置冒号控件内部字体位置 - 默认居中
     *
     * @param countDownViewGravity 左上右下中
     */
    public CountDownView setColonTvGravity(CountDownViewGravity countDownViewGravity) {
        int gravity = Gravity.CENTER;
        if (countDownViewGravity == CountDownViewGravity.GRAVITY_BOTTOM) {
            gravity = Gravity.BOTTOM;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_CENTER) {
            gravity = Gravity.CENTER;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_LEFT) {
            gravity = Gravity.START;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_RIGHT) {
            gravity = Gravity.END;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_TOP) {
            gravity = Gravity.TOP;
        }
        hourColonTextView.setGravity(gravity);
        minuteColonTextView.setGravity(gravity);
        return this;
    }


    /**
     * 设置小时控件尺寸
     *
     * @param width  宽 0取默认值
     * @param height 高 0取默认值
     * @return CountDownView
     */
    public CountDownView setHourTvSize(int width, int height) {
        ViewGroup.LayoutParams hourParams = hourTextView.getLayoutParams();
        if (hourParams != null) {
            if (width > 0)
                hourParams.width = width;
            if (height > 0)
                hourParams.height = height;
            hourTextView.setLayoutParams(hourParams);
        }
        return this;
    }

    /**
     * 设置小时控件背景
     *
     * @param res 背景资源ID
     * @return CountDownView
     */
    public CountDownView setHourTvBackgroundRes(int res) {
        hourTextView.setBackgroundResource(res);
        return this;
    }

    /**
     * 设置小时控件背景
     *
     * @param drawable 背景资源Drawable
     * @return CountDownView
     */
    public CountDownView setHourTvBackground(Drawable drawable) {
        if (drawable != null) {
            hourTextView.setBackground(drawable);
        }
        return this;
    }

    /**
     * 设置小时控件背景
     *
     * @param colorHex 背景颜色16进制“#FFFFFF”
     * @return CountDownView
     */
    public CountDownView setHourTvBackgroundColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        hourTextView.setBackgroundColor(color);
        return this;
    }

    /**
     * 设置小时控件字体大小
     *
     * @param size 字体大小
     * @return CountDownView
     */
    public CountDownView setHourTvTextSize(float size) {
        hourTextView.setTextSize(size);
        return this;
    }

    /**
     * 设置小时控件字体颜色
     *
     * @param colorHex 字体颜色十六进制 “#FFFFFF”
     * @return CountDownView
     */
    public CountDownView setHourTvTextColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        hourTextView.setTextColor(color);
        return this;
    }

    /**
     * 设置小时控件内部字体位置 - 默认居中
     *
     * @param countDownViewGravity 左上右下中
     */
    public CountDownView setHourTvGravity(CountDownViewGravity countDownViewGravity) {
        int gravity = Gravity.CENTER;
        if (countDownViewGravity == CountDownViewGravity.GRAVITY_BOTTOM) {
            gravity = Gravity.BOTTOM;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_CENTER) {
            gravity = Gravity.CENTER;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_LEFT) {
            gravity = Gravity.START;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_RIGHT) {
            gravity = Gravity.END;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_TOP) {
            gravity = Gravity.TOP;
        }
        hourTextView.setGravity(gravity);
        return this;
    }

    /**
     * 设置小时控件内边距
     *
     * @param left   左边距 px
     * @param top    上边距 px
     * @param right  右边距 px
     * @param bottom 下边距 px
     * @return CountDownView
     */
    public CountDownView setHourTvPadding(int left, int top, int right, int bottom) {
        hourTextView.setPadding(left, top, right, bottom);
        return this;
    }

    /**
     * 设置小时控件外边距
     *
     * @param left   左边距 px
     * @param top    上边距 px
     * @param right  右边距 px
     * @param bottom 下边距 px
     * @return CountDownView
     */
    public CountDownView setHourTvMargins(int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(left, top, right, bottom);
        minuteTextView.setLayoutParams(params);
        return this;
    }

    /**
     * 设置小时控件是否为粗体
     *
     * @param bool true/false
     * @return CountDownView
     */
    public CountDownView setHourTvBold(boolean bool) {
        hourTextView.getPaint().setFakeBoldText(bool);
        return this;
    }


    /**
     * 设置分钟控件尺寸
     *
     * @param width  宽 0取默认值
     * @param height 高 0取默认值
     * @return CountDownView
     */
    public CountDownView setMinuteTvSize(int width, int height) {
        ViewGroup.LayoutParams minuteParams = minuteTextView.getLayoutParams();
        if (minuteParams != null) {
            if (width > 0)
                minuteParams.width = width;
            if (height > 0)
                minuteParams.height = height;
            minuteTextView.setLayoutParams(minuteParams);
        }
        return this;
    }

    /**
     * 设置分钟控件背景
     *
     * @param res 背景资源ID
     * @return CountDownView
     */
    public CountDownView setMinuteTvBackgroundRes(int res) {
        minuteTextView.setBackgroundResource(res);
        return this;
    }

    /**
     * 设置分钟控件背景
     *
     * @param drawable 背景资源Drawable
     * @return CountDownView
     */
    public CountDownView setMinuteTvBackground(Drawable drawable) {
        if (drawable != null) {
            minuteTextView.setBackground(drawable);
        }
        return this;
    }

    /**
     * 设置分钟控件背景
     *
     * @param colorHex 背景颜色16进制“#FFFFFF”
     * @return CountDownView
     */
    public CountDownView setMinuteTvBackgroundColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        minuteTextView.setBackgroundColor(color);
        return this;
    }

    /**
     * 设置分钟控件字体大小
     *
     * @param size 字体大小
     * @return CountDownView
     */
    public CountDownView setMinuteTvTextSize(float size) {
        minuteTextView.setTextSize(size);
        return this;
    }

    /**
     * 设置分钟控件字体颜色
     *
     * @param colorHex 字体颜色十六进制 “#FFFFFF”
     * @return CountDownView
     */
    public CountDownView setMinuteTvTextColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        minuteTextView.setTextColor(color);
        return this;
    }

    /**
     * 设置分钟控件内部字体位置 - 默认居中
     *
     * @param countDownViewGravity 左上右下中
     */
    public CountDownView setMinuteTvGravity(CountDownViewGravity countDownViewGravity) {
        int gravity = Gravity.CENTER;
        if (countDownViewGravity == CountDownViewGravity.GRAVITY_BOTTOM) {
            gravity = Gravity.BOTTOM;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_CENTER) {
            gravity = Gravity.CENTER;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_LEFT) {
            gravity = Gravity.START;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_RIGHT) {
            gravity = Gravity.END;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_TOP) {
            gravity = Gravity.TOP;
        }
        minuteTextView.setGravity(gravity);
        return this;
    }

    /**
     * 设置分钟控件内边距
     *
     * @param left   左边距 px
     * @param top    上边距 px
     * @param right  右边距 px
     * @param bottom 下边距 px
     * @return CountDownView
     */
    public CountDownView setMinuteTvPadding(int left, int top, int right, int bottom) {
        minuteTextView.setPadding(left, top, right, bottom);
        return this;
    }

    /**
     * 设置分钟控件外边距
     *
     * @param left   左边距 px
     * @param top    上边距 px
     * @param right  右边距 px
     * @param bottom 下边距 px
     * @return CountDownView
     */
    public CountDownView setMinuteTvMargins(int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(left, top, right, bottom);
        minuteTextView.setLayoutParams(params);
        return this;
    }

    /**
     * 设置分钟控件是否为粗体
     *
     * @param bool true/false
     * @return CountDownView
     */
    public CountDownView setMinuteTvBold(boolean bool) {
        minuteTextView.getPaint().setFakeBoldText(bool);
        return this;
    }


    /**
     * 设置秒控件尺寸
     *
     * @param width  宽 0取默认值
     * @param height 高 0取默认值
     * @return CountDownView
     */
    public CountDownView setSecondTvSize(int width, int height) {
        ViewGroup.LayoutParams secondParams = secondTextView.getLayoutParams();
        if (secondParams != null) {
            if (width > 0)
                secondParams.width = width;
            if (height > 0)
                secondParams.height = height;
            secondTextView.setLayoutParams(secondParams);
        }
        return this;
    }

    /**
     * 设置秒控件背景
     *
     * @param res 背景资源ID
     * @return CountDownView
     */
    public CountDownView setSecondTvBackgroundRes(int res) {
        secondTextView.setBackgroundResource(res);
        return this;
    }

    /**
     * 设置秒控件背景
     *
     * @param drawable 背景资源Drawable
     * @return CountDownView
     */
    public CountDownView setSecondTvBackground(Drawable drawable) {
        if (drawable != null) {
            secondTextView.setBackground(drawable);
        }
        return this;
    }

    /**
     * 设置秒控件背景
     *
     * @param colorHex 背景颜色16进制“#FFFFFF”
     * @return CountDownView
     */
    public CountDownView setSecondTvBackgroundColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        secondTextView.setBackgroundColor(color);
        return this;
    }

    /**
     * 设置秒控件字体大小
     *
     * @param size 字体大小
     * @return CountDownView
     */
    public CountDownView setSecondTvTextSize(float size) {
        secondTextView.setTextSize(size);
        return this;
    }

    /**
     * 设置秒控件字体颜色
     *
     * @param colorHex 字体颜色十六进制 “#FFFFFF”
     * @return CountDownView
     */
    public CountDownView setSecondTvTextColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        secondTextView.setTextColor(color);
        return this;
    }

    /**
     * 设置秒控件内部字体位置
     *
     * @param countDownViewGravity 左上右下中
     */
    public CountDownView setSecondTvGravity(CountDownViewGravity countDownViewGravity) {
        int gravity = Gravity.CENTER;
        if (countDownViewGravity == CountDownViewGravity.GRAVITY_BOTTOM) {
            gravity = Gravity.BOTTOM;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_CENTER) {
            gravity = Gravity.CENTER;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_LEFT) {
            gravity = Gravity.START;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_RIGHT) {
            gravity = Gravity.END;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_TOP) {
            gravity = Gravity.TOP;
        }
        secondTextView.setGravity(gravity);
        return this;
    }

    /**
     * 设置秒控件内边距
     *
     * @param left   左边距 px
     * @param top    上边距 px
     * @param right  右边距 px
     * @param bottom 下边距 px
     * @return CountDownView
     */
    public CountDownView setSecondTvPadding(int left, int top, int right, int bottom) {
        secondTextView.setPadding(left, top, right, bottom);
        return this;
    }

    /**
     * 设置秒控件外边距
     *
     * @param left   左边距 px
     * @param top    上边距 px
     * @param right  右边距 px
     * @param bottom 下边距 px
     * @return CountDownView
     */
    public CountDownView setSecondTvMargins(int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(left, top, right, bottom);
        secondTextView.setLayoutParams(params);
        return this;
    }

    /**
     * 设置秒控件是否为粗体
     *
     * @param bool true/false
     * @return CountDownView
     */
    public CountDownView setSecondTvBold(boolean bool) {
        secondTextView.getPaint().setFakeBoldText(bool);
        return this;
    }


    /**
     * 设置小时冒号控件尺寸
     *
     * @param width  宽 0取默认值
     * @param height 高 0取默认值
     * @return CountDownView
     */
    public CountDownView setHourColonTvSize(int width, int height) {
        ViewGroup.LayoutParams hourColonParams = hourColonTextView.getLayoutParams();
        if (hourColonParams != null) {
            if (width > 0)
                hourColonParams.width = width;
            if (height > 0)
                hourColonParams.height = height;
            hourColonTextView.setLayoutParams(hourColonParams);
        }
        return this;
    }

    /**
     * 设置小时冒号控件背景
     *
     * @param res 背景资源ID
     * @return CountDownView
     */
    public CountDownView setHourColonTvBackgroundRes(int res) {
        hourColonTextView.setBackgroundResource(res);
        return this;
    }

    /**
     * 设置小时冒号控件背景
     *
     * @param drawable 背景资源Drawable
     * @return CountDownView
     */
    public CountDownView setHourColonTvBackground(Drawable drawable) {
        if (drawable != null) {
            hourColonTextView.setBackground(drawable);
        }
        return this;
    }

    /**
     * 设置小时冒号控件背景
     *
     * @param colorHex 背景颜色16进制“#FFFFFF”
     * @return CountDownView
     */
    public CountDownView setHourColonTvBackgroundColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        hourColonTextView.setBackgroundColor(color);
        return this;
    }

    /**
     * 设置小时冒号控件字体大小
     *
     * @param size 字体大小
     * @return CountDownView
     */
    public CountDownView setHourColonTvTextSize(float size) {
        hourColonTextView.setTextSize(size);
        return this;
    }

    /**
     * 修改小时冒号为文字
     *
     * @param hourColonText
     * @return
     */
    public CountDownView setHourColonTvText(String hourColonText) {
        hourColonTextView.setText(hourColonText);
        return this;
    }

    /**
     * 设置小时冒号控件字体颜色
     *
     * @param colorHex 字体颜色十六进制 “#FFFFFF”
     * @return CountDownView
     */
    public CountDownView setHourColonTvTextColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        hourColonTextView.setTextColor(color);
        return this;
    }

    /**
     * 设置小时冒号控件内部字体位置
     *
     * @param countDownViewGravity 左上右下中
     */
    public CountDownView setHourColonTvGravity(CountDownViewGravity countDownViewGravity) {
        int gravity = Gravity.CENTER;
        if (countDownViewGravity == CountDownViewGravity.GRAVITY_BOTTOM) {
            gravity = Gravity.BOTTOM;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_CENTER) {
            gravity = Gravity.CENTER;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_LEFT) {
            gravity = Gravity.START;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_RIGHT) {
            gravity = Gravity.END;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_TOP) {
            gravity = Gravity.TOP;
        }
        hourColonTextView.setGravity(gravity);
        return this;
    }

    /**
     * 设置小时冒号控件内边距
     *
     * @param left   左边距 px
     * @param top    上边距 px
     * @param right  右边距 px
     * @param bottom 下边距 px
     * @return CountDownView
     */
    public CountDownView setHourColonTvPadding(int left, int top, int right, int bottom) {
        hourColonTextView.setPadding(left, top, right, bottom);
        return this;
    }

    /**
     * 设置小时冒号控件外边距
     *
     * @param left   左边距 px
     * @param top    上边距 px
     * @param right  右边距 px
     * @param bottom 下边距 px
     * @return CountDownView
     */
    public CountDownView setHourColonTvMargins(int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(left, top, right, bottom);
        hourColonTextView.setLayoutParams(params);
        return this;
    }

    /**
     * 设置小时冒号控件是否为粗体
     *
     * @param bool true/false
     * @return CountDownView
     */
    public CountDownView setHourColonTvBold(boolean bool) {
        hourColonTextView.getPaint().setFakeBoldText(bool);
        return this;
    }


    /**
     * 设置分钟冒号控件尺寸
     *
     * @param width  宽 0取默认值
     * @param height 高 0取默认值
     * @return CountDownView
     */
    public CountDownView setMinuteColonTvSize(int width, int height) {
        ViewGroup.LayoutParams minuteColonParams = minuteColonTextView.getLayoutParams();
        if (minuteColonParams != null) {
            if (width > 0)
                minuteColonParams.width = width;
            if (height > 0)
                minuteColonParams.height = height;
            minuteColonTextView.setLayoutParams(minuteColonParams);
        }
        return this;
    }

    /**
     * 设置分钟冒号控件背景
     *
     * @param res 背景资源ID
     * @return CountDownView
     */
    public CountDownView setMinuteColonTvBackgroundRes(int res) {
        minuteColonTextView.setBackgroundResource(res);
        return this;
    }

    /**
     * 设置分钟冒号控件背景
     *
     * @param drawable 背景资源Drawable
     * @return CountDownView
     */
    public CountDownView setMinuteColonTvBackground(Drawable drawable) {
        if (drawable != null) {
            minuteColonTextView.setBackground(drawable);
        }
        return this;
    }

    /**
     * 设置分钟冒号控件背景
     *
     * @param colorHex 背景颜色16进制“#FFFFFF”
     * @return CountDownView
     */
    public CountDownView setMinuteColonTvBackgroundColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        minuteColonTextView.setBackgroundColor(color);
        return this;
    }

    /**
     * 设置分钟冒号控件字体大小
     *
     * @param size 字体大小
     * @return CountDownView
     */
    public CountDownView setMinuteColonTvTextSize(float size) {
        minuteColonTextView.setTextSize(size);
        return this;
    }

    /**
     * 修改冒号为文字
     *
     * @param minuteColonText
     * @return
     */
    public CountDownView setMinuteColonTvText(String minuteColonText) {
        minuteColonTextView.setText(minuteColonText);
        return this;
    }

    /**
     * 设置分钟冒号控件字体颜色
     *
     * @param colorHex 字体颜色十六进制 “#FFFFFF”
     * @return CountDownView
     */
    public CountDownView setMinuteColonTvTextColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        minuteColonTextView.setTextColor(color);
        return this;
    }

    /**
     * 设置分钟冒号控件内部字体位置
     *
     * @param countDownViewGravity 左上右下中
     */
    public CountDownView setMinuteColonTvGravity(CountDownViewGravity countDownViewGravity) {
        int gravity = Gravity.CENTER;
        if (countDownViewGravity == CountDownViewGravity.GRAVITY_BOTTOM) {
            gravity = Gravity.BOTTOM;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_CENTER) {
            gravity = Gravity.CENTER;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_LEFT) {
            gravity = Gravity.START;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_RIGHT) {
            gravity = Gravity.END;
        } else if (countDownViewGravity == CountDownViewGravity.GRAVITY_TOP) {
            gravity = Gravity.TOP;
        }
        minuteColonTextView.setGravity(gravity);
        return this;
    }

    /**
     * 设置分钟冒号控件内边距
     *
     * @param left   左边距 px
     * @param top    上边距 px
     * @param right  右边距 px
     * @param bottom 下边距 px
     * @return CountDownView
     */
    public CountDownView setMinuteColonTvPadding(int left, int top, int right, int bottom) {
        minuteColonTextView.setPadding(left, top, right, bottom);
        return this;
    }

    /**
     * 设置分钟冒号控件外边距
     *
     * @param left   左边距 px
     * @param top    上边距 px
     * @param right  右边距 px
     * @param bottom 下边距 px
     * @return CountDownView
     */
    public CountDownView setMinuteColonTvMargins(int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(left, top, right, bottom);
        minuteColonTextView.setLayoutParams(params);
        return this;
    }

    /**
     * 设置分钟冒号控件是否为粗体
     *
     * @param bool true/false
     * @return CountDownView
     */
    public CountDownView setMinuteColonTvBold(boolean bool) {
        minuteColonTextView.getPaint().setFakeBoldText(bool);
        return this;
    }


    /**
     * 设置倒计时-时间戳
     *
     * @param timeStamp 倒计时时间戳，不能大于99小时
     * @return CountDownView
     */
    public CountDownView setCountTime(long timeStamp) {
        this.timeStamp = (timeStamp - System.currentTimeMillis() / 1000);
        return this;
    }

    /**
     * 设置倒计时-时间戳
     *
     * @param timeStamp 倒计时时间
     * @return CountDownView
     */
    public CountDownView setCountAllTime(long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    /**
     * 开启倒计时
     *
     * @return CountDownView
     */
    public CountDownView startCountDown() {
        if (timeStamp <= 1) {
            this.isContinue = false;
            Log.e(TAG, "时间戳错误");
        } else {
            this.isContinue = true;
            countDown();
        }
        return this;
    }

    /**
     * 暂停倒计时
     *
     * @return CountDownView
     */
    public CountDownView pauseCountDown() {
        this.isContinue = false;
        return this;
    }

    /**
     * 关闭倒计时
     *
     * @return CountDownView
     */
    public CountDownView stopCountDown() {
        this.timeStamp = 0;
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

    /**
     * 实现倒计时的功能
     */
    private void countDown() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isContinue) {
                        isContinue = timeStamp-- > 1;
                        String[] times = CountDownUtil.secToTimes(timeStamp);
                        Message message = new Message();
                        message.obj = times;
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
     * 更新UI
     *
     * @param text     显示文本内容
     * @param textView 待显示的控件
     */
    private void updateTvText(String text, TextView textView) {
        textView.setText(text);
    }

    private Handler myHandler = new MyHandler(this);

    static class MyHandler extends Handler {
        // 定义一个对象用来引用Activity中的方法
        private final WeakReference<CountDownView> mCountDownView;

        MyHandler(CountDownView countDownView) {
            mCountDownView = new WeakReference<>(countDownView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CountDownView currentCountDownView = mCountDownView.get();
            switch (msg.what) {
                case UPDATE_UI_CODE:// 刷新UI
                    if (msg.obj != null) {
                        String[] times = (String[]) msg.obj;
                        for (int i = 0; i < times.length; i++) {
                            switch (i) {
                                case 0:// 时
                                    currentCountDownView.updateTvText(times[0], currentCountDownView.hourTextView);
                                    break;
                                case 1:// 分
                                    currentCountDownView.updateTvText(times[1], currentCountDownView.minuteTextView);
                                    break;
                                case 2:// 秒
                                    currentCountDownView.updateTvText(times[2], currentCountDownView.secondTextView);
                                    break;
                            }
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
     * 定义倒计时结束接口
     */
    public interface CountDownEndListener {
        void onCountDownEnd();
    }

    private CountDownEndListener countDownEndListener;

    public void setCountDownEndListener(CountDownEndListener countDownEndListener) {
        this.countDownEndListener = countDownEndListener;
    }

}
