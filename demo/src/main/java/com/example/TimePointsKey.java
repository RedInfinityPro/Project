package com.example;

import java.time.LocalTime;
import java.util.Objects;

public class TimePointsKey {
    private final LocalTime timeStamp;
    private final Double points;

    TimePointsKey(LocalTime timeStamp, Double points) {
        this.timeStamp = timeStamp;
        this.points = points;
    }

    public LocalTime getTime() {
        return timeStamp;
    }

    public Double getPoints() {
        return points;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TimePointsKey)) {
            return false;
        }
        TimePointsKey other = (TimePointsKey) obj;
        return Objects.equals(timeStamp, other.timeStamp) && Double.compare(points, other.points) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeStamp, points);
    }

    @Override
    public String toString() {
        return String.format("Key[%s, %.2f]", timeStamp, points);
    }
}