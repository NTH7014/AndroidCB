package com.example.weathergo.Domains;

public class Daily {
    private String day;
    private String dailyPicPath;
    private String condition;
    private int minTemp;
    private int maxTemp;

    public Daily(String day, String dailyPicPath, String condition, int minTemp, int maxTemp) {
        this.day = day;
        this.dailyPicPath = dailyPicPath; //Đường dẫn ảnh minh họa thời tiết
        this.condition = condition; //Mô tả thời tiết
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDailyPicPath() {
        return dailyPicPath;
    }

    public void setDailyPicPath(String dailyPicPath) {
        this.dailyPicPath = dailyPicPath;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(int minTemp) {
        this.minTemp = minTemp;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(int maxTemp) {
        this.maxTemp = maxTemp;
    }
}
