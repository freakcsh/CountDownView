package com.freak.countdownview;

public class TestEntity {
    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public TestEntity(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "TestEntity{" +
                "time=" + time +
                '}';
    }
}
