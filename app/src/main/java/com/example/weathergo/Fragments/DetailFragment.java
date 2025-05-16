package com.example.weathergo.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.weathergo.Adapters.HourlyAdapters;
import com.example.weathergo.Domains.Hourly;
import com.example.weathergo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class DetailFragment extends Fragment {
    TextView windDirectionTxt, visualTxt, uvTxt, gustTxt, precipTxt, pressureTxt, airQualityTxt;
    private RecyclerView hourlyView;
    private ArrayList<Hourly> items;
    private HourlyAdapters adapterHourly;

    private String selectedLocation = "Hanoi"; // Giá trị mặc định
    private String selectedUnit = "Celsius"; // Giá trị mặc định

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        // Khởi tạo các thành phần UI
        windDirectionTxt = view.findViewById(R.id.windDirectTxt);
        visualTxt = view.findViewById(R.id.visTxt);
        uvTxt = view.findViewById(R.id.uvTxt);
        gustTxt = view.findViewById(R.id.gustTxt);
        precipTxt = view.findViewById(R.id.precipTxt);
        pressureTxt = view.findViewById(R.id.pressureTxt);
        airQualityTxt = view.findViewById(R.id.airQualityTxt);

        hourlyView = view.findViewById(R.id.hourlyView);
        hourlyView.setBackgroundColor(getResources().getColor(R.color.primary));

        items = new ArrayList<>();

        // Đọc giá trị từ SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPreferences", requireContext().MODE_PRIVATE);
        selectedLocation = sharedPreferences.getString("location", "Hanoi");
        selectedUnit = sharedPreferences.getString("unit", "Celsius");

        // Khởi tạo RecyclerView và lấy dữ liệu
        initRecyclerView(); //load danh sách thời tiết theo giờ.
        getWeatherData(); //load các chỉ số thời tiết tổng quan.

        return view;
    }

    private void initRecyclerView() {
        String unitQueryParam = selectedUnit.equals("Celsius") ? "temp_c" : "temp_f";
        String url = "https://api.weatherapi.com/v1/forecast.json?key=3e863d90628d41b2a6e72023232709&q=" + selectedLocation + "&days=1&aqi=no&alerts=no";

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                Date date = new Date();
                String currentHour = String.valueOf(date).substring(11, 13); // Giờ hiện tại

                // Lấy mảng "hour" từ JSON
                JSONArray hourArray = response.getJSONObject("forecast")
                        .getJSONArray("forecastday")
                        .getJSONObject(0)
                        .getJSONArray("hour");

                for (int i = 0; i < hourArray.length(); i++) {
                    JSONObject hourObject = hourArray.getJSONObject(i);

                    // Lấy nhiệt độ theo đơn vị đã chọn
                    double temperature = hourObject.getDouble(unitQueryParam);

                    String icon = hourObject.getJSONObject("condition").getString("icon");
                    String time = hourObject.getString("time").substring(11, 16); // Lấy giờ phút

                    String hourOnly = time.substring(0, 2); // Chỉ lấy giờ
                    if (hourOnly.equals(currentHour)) {
                        items.add(new Hourly("Bây giờ", (int) temperature, icon));
                    } else if (Integer.parseInt(hourOnly) > Integer.parseInt(currentHour)) { //// Nếu sau giờ hiện tại ➔ ghi giờ bình thường
                        items.add(new Hourly(time, (int) temperature, icon));
                    }
                }

                // Cập nhật RecyclerView
                adapterHourly = new HourlyAdapters(items, requireContext());
                hourlyView.setAdapter(adapterHourly);
                hourlyView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
            } catch (JSONException e) {
                Toast.makeText(requireContext(), "Lỗi xử lý JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(requireContext(), "Lỗi khi gọi API: " + error.toString(), Toast.LENGTH_SHORT).show();
            error.printStackTrace();
        });

        queue.add(request);
    }

    private void getWeatherData() {
        String url = "https://api.weatherapi.com/v1/current.json?key=3e863d90628d41b2a6e72023232709&q=" + selectedLocation + "&aqi=yes";

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                //Lấy đối tượng "current" trong JSON response
                JSONObject current = response.getJSONObject("current");

                // Hướng gió
                String windDirection = current.getString("wind_dir");
                HashMap<String, String> windMapping = new HashMap<>();
                windMapping.put("N", "Bắc");
                windMapping.put("S", "Nam");
                windMapping.put("E", "Đông");
                windMapping.put("W", "Tây");
                windMapping.put("NE", "Đông Bắc");
                windMapping.put("NW", "Tây Bắc");
                windMapping.put("SE", "Đông Nam");
                windMapping.put("SW", "Tây Nam");
                windMapping.put("ESE", "Đông Đông Nam");
                windMapping.put("NNE", "Bắc Đông Bắc");
                windMapping.put("SSE", "Nam Đông Nam");
                windMapping.put("SSW", "Nam Tây Nam");
                windMapping.put("WNW", "Tây Tây Bắc");
                windMapping.put("WSW", "Tây Tây Nam");
                windMapping.put("NNW", "Bắc Tây Bắc");

                windDirectionTxt.setText(windMapping.getOrDefault(windDirection, windDirection));

                //Tầm nhìn (vis_km), UV, gió giật (gust_kph), lượng mưa (precip_mm), áp suất (pressure_mb)
                visualTxt.setText(String.format("%.1f km", current.getDouble("vis_km")));
                uvTxt.setText(String.valueOf(current.getDouble("uv")));
                gustTxt.setText(String.format("%.1f km/h", current.getDouble("gust_kph")));
                precipTxt.setText(String.format("%.1f mm", current.getDouble("precip_mm")));
                pressureTxt.setText(String.format("%.1f mb", current.getDouble("pressure_mb")));

                // Thông tin chất lượng không khí
                if (current.has("air_quality")) {
                    JSONObject airQuality = current.getJSONObject("air_quality");
                    double pm2_5 = airQuality.getDouble("pm2_5");
                    double pm10 = airQuality.getDouble("pm10");

                    String airQualityLevel;
                    int textColor;

                    if (pm2_5 <= 12) {
                        airQualityLevel = "Tốt";
                        textColor = getResources().getColor(R.color.good); // Xanh lá
                    } else if (pm2_5 <= 35.4) {
                        airQualityLevel = "Trung bình";
                        textColor = getResources().getColor(R.color.moderate); // Vàng nhạt
                    } else if (pm2_5 <= 55.4) {
                        airQualityLevel = "Không tốt cho nhạy cảm";
                        textColor = getResources().getColor(R.color.unhealthy_sensitive); // Cam nhạt
                    } else if (pm2_5 <= 150.4) {
                        airQualityLevel = "Xấu";
                        textColor = getResources().getColor(R.color.unhealthy); // Cam đậm
                    } else if (pm2_5 <= 250.4) {
                        airQualityLevel = "Rất xấu";
                        textColor = getResources().getColor(R.color.very_unhealthy); // Tím
                    } else {
                        airQualityLevel = "Nguy hại";
                        textColor = getResources().getColor(R.color.hazardous); // Đỏ đậm
                    }

                    airQualityTxt.setText(String.format(
                            "Chất lượng không khí: %.0f - %s (PM2.5: %.1f µg/m³, PM10: %.1f µg/m³)",
                            pm2_5, airQualityLevel, pm2_5, pm10
                    ));
                    airQualityTxt.setTextColor(textColor);
                } else {
                    airQualityTxt.setText("Không có dữ liệu chất lượng không khí");
                    airQualityTxt.setTextColor(getResources().getColor(R.color.gray)); // Màu xám khi không có data
                }

            } catch (JSONException e) {
                Toast.makeText(requireContext(), "Lỗi xử lý JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(requireContext(), "Lỗi khi gọi API: " + error.toString(), Toast.LENGTH_SHORT).show();
            error.printStackTrace();
        });

        queue.add(request);
    }
}
