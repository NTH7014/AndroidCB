package com.example.weathergo.Domains;

public class Hourly {
    private String hour;
    private int temp;
    private String picPath;

    public Hourly(String hour, int temp, String picPath) {
        this.hour = hour;
        this.temp = temp; // Nhiệt độ cụ thể tại giờ đó
        this.picPath = picPath; //Đường dẫn đến ảnh biểu tượng thời tiết
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }
}
