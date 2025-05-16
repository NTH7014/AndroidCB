package com.example.weathergo.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.weathergo.Activities.MainActivity;
import com.example.weathergo.R;

import org.json.JSONObject;

public class WeatherWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Lấy layout của widget
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_weather);

        // Mở HomeFragment khi nhấn vào widget
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("openFragment", "HomeFragment");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

        // Đặt dữ liệu mặc định
        views.setTextViewText(R.id.widget_city_name, "Hà Nội");
        views.setTextViewText(R.id.widget_temperature, "--°C");
        views.setImageViewResource(R.id.widget_icon, R.drawable.df); // Icon mặc định

        // Đặt layout vào widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

        // Lấy và cập nhật dữ liệu thời tiết
        fetchWeatherData(context, appWidgetManager, appWidgetId);
    }

    private static void fetchWeatherData(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String location = sharedPreferences.getString("location", "Hanoi");
        String unit = sharedPreferences.getString("unit", "Celsius");

        // URL API WeatherAPI
        String url = "https://api.weatherapi.com/v1/forecast.json?key=3e863d90628d41b2a6e72023232709&q=" + location + "&days=1&aqi=no&alerts=no";

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        // Lấy dữ liệu thời tiết
                        JSONObject locationObj = response.getJSONObject("location");
                        JSONObject forecastDayObj = response.getJSONObject("forecast")
                                .getJSONArray("forecastday")
                                .getJSONObject(0)
                                .getJSONObject("day");

                        //String cityName = locationObj.getString("name");
                        String cityName = location;

                        // Nhiệt độ trung bình
                        double temperature = forecastDayObj.getDouble("avgtemp_c");
                        if (unit.equals("Fahrenheit")) {
                            temperature = (temperature * 9 / 5) + 32;
                        }

                        // URL icon thời tiết
                        String iconCode = forecastDayObj.getJSONObject("condition").getString("icon");
                        String iconUrl = "https:" + iconCode;

                        // Cập nhật giao diện widget
                        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_weather);
                        views.setTextViewText(R.id.widget_city_name, cityName);
                        views.setTextViewText(R.id.widget_temperature, String.format("%.0f°%s", temperature, unit.equals("Celsius") ? "C" : "F"));

                        // Tải icon thời tiết
                        loadImage(context, iconUrl, views, appWidgetManager, appWidgetId);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        queue.add(request);
    }

    // Tải hình ảnh icon thời tiết vào widget
    private static void loadImage(Context context, String iconUrl, RemoteViews views, AppWidgetManager appWidgetManager, int appWidgetId) {
        RequestQueue queue = Volley.newRequestQueue(context);

        ImageRequest imageRequest = new ImageRequest(iconUrl,
                bitmap -> {
                    views.setImageViewBitmap(R.id.widget_icon, bitmap);
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                },
                0, 0,
                ImageView.ScaleType.CENTER_INSIDE,
                Bitmap.Config.ARGB_8888,
                error -> error.printStackTrace());

        queue.add(imageRequest);
    }

    // Cập nhật tất cả widget khi dữ liệu cài đặt thay đổi
    public static void updateAllWidgets(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context, WeatherWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}
