package yellow.jogging.beans;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Statistic {

    @JsonProperty
    private int weekNumber;

    @JsonProperty
    private String weekRange;

    @JsonProperty(value = "Av. Speed")
    private double avSpeed;
    @JsonProperty(value = "Av. Time")
    private double avTime;
    @JsonProperty(value = "Total Distance")
    private long totalDistance;

    public long getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(long totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getAvTime() {
        return avTime;
    }

    public void setAvTime(double avTime) {
        this.avTime = avTime;
    }

    public double getAvSpeed() {
        return avSpeed;
    }

    public void setAvSpeed(double avSpeed) {
        this.avSpeed = avSpeed;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public String getWeekRange() {
        return weekRange;
    }

    public void setWeekRange(String weekRange) {
        this.weekRange = weekRange;
    }

}
