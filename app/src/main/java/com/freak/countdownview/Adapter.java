package com.freak.countdownview;

import android.os.CountDownTimer;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.freak.countdown.CountDownView;

import java.util.List;

public class Adapter extends BaseQuickAdapter<TestEntity, BaseViewHolder> {
    //用于退出activity,避免countdown，造成资源浪费。
    private SparseArray<CountDownView> countDownMap;

    public Adapter(int layoutResId, @Nullable List<TestEntity> data) {
        super(layoutResId, data);
        countDownMap = new SparseArray<>();
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, TestEntity item) {
        CountDownView countDownView = helper.getView(R.id.count_down_view_item);
        countDownView.setCountDownTime(item.getTime()).startCountDown();
        countDownMap.put(countDownView.hashCode(), countDownView);
    }

    public void stopAllTime() {
        Log.e("TAG", "停止倒计时");
        if (countDownMap == null) {
            return;
        }
        Log.e("TAG", "size :  " + countDownMap.size());
        for (int i = 0, length = countDownMap.size(); i < length; i++) {

            CountDownView countDownView = countDownMap.get(countDownMap.keyAt(i));
            if (countDownView != null) {
                Log.e("TAG", "停止倒计时  " + i);
                countDownView.stopCountDown();
            }
        }
        countDownMap.clear();
        countDownMap = null;
    }
}
