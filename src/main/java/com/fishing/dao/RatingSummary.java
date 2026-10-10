package com.fishing.dao;

/** Điểm trung bình và số lượt đánh giá của một sản phẩm. */
public class RatingSummary {
    private final double average;
    private final long count;

    public RatingSummary(double average, long count) {
        this.average = average;
        this.count = count;
    }

    public double getAverage() { return average; }
    public long getCount() { return count; }
}
