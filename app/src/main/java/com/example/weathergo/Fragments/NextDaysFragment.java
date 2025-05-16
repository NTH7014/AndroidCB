package com.example.weathergo.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.weathergo.Adapters.DailyAdapter;
import com.example.weathergo.Domains.Daily;
import com.example.weathergo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class NextDaysFragment extends Fragment {

    private RecyclerView dailyView;
    private ArrayList<Daily> items;
    private DailyAdapter adapterDaily;

    private String selectedLocation;
    private String selectedUnit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_next_days, container, false);

        dailyView = view.findViewById(R.id.dailyView);
        items = new ArrayList<>();

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPreferences", requireContext().MODE_PRIVATE);
        selectedLocation = sharedPreferences.getString("location", "Hanoi");
        selectedUnit = sharedPreferences.getString("unit", "Celsius");

        initRecyclerView();

        return view;
    }

    private void initRecyclerView() {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=d873c079dccf4b0d955123827240112&q=" + selectedLocation + "&days=7&aqi=no&alerts=no";
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONArray forecastdayArray = response.getJSONObject("forecast").getJSONArray("forecastday");

                for (int i = 1; i < forecastdayArray.length(); i++) {
                    JSONObject firstForecast = forecastdayArray.getJSONObject(i);

                    // Lấy nhiệt độ
                    int maxTempC = firstForecast.getJSONObject("day").getInt("maxtemp_c");
                    int minTempC = firstForecast.getJSONObject("day").getInt("mintemp_c");

                    if (selectedUnit.equals("Fahrenheit")) {
                        maxTempC = convertToFahrenheit(maxTempC);
                        minTempC = convertToFahrenheit(minTempC);
                    }

                    // Lấy điều kiện thời tiết
                    String condition = firstForecast.getJSONObject("day").getJSONObject("condition").getString("text").trim().toLowerCase(Locale.ROOT);
                    String icon = firstForecast.getJSONObject("day").getJSONObject("condition").getString("icon");

                    // Lấy ngày
                    String time = firstForecast.getString("date");
                    String subTime = time.substring(8, 10) + "-" + time.substring(5, 7);

                    // Lấy mô tả thời tiết bằng tiếng Việt
                    String displayValue = getWeatherDescription(condition);

                    // Thêm item vào danh sách
                    items.add(new Daily(subTime, icon, displayValue, minTempC, maxTempC));
                }

                adapterDaily = new DailyAdapter(items, requireContext());
                dailyView.setAdapter(adapterDaily);
                dailyView.setLayoutManager(new LinearLayoutManager(requireContext()));

            } catch (JSONException e) {
                Toast.makeText(requireContext(), "Lỗi xử lý dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }, error -> Toast.makeText(requireContext(), "Lỗi kết nối: " + error.toString(), Toast.LENGTH_SHORT).show());

        queue.add(request);
    }

    private int convertToFahrenheit(int tempC) {
        return (int) ((tempC * 9 / 5.0) + 32);
    }

    private String getWeatherDescription(String condition) {
        HashMap<String, String> weatherMapping = new HashMap<>();
        weatherMapping.put("clear", "Trời quang");
        weatherMapping.put("rainy", "Mưa");
        weatherMapping.put("cloudy", "Nhiều mây");
        weatherMapping.put("partly cloudy", "Mây rải rác");
        weatherMapping.put("sunny", "Nắng");
        weatherMapping.put("mist", "Sương mù");
        weatherMapping.put("moderate rain", "Mưa vừa");
        weatherMapping.put("light rain", "Mưa nhỏ");
        weatherMapping.put("patchy rain possible", "Có thể mưa");
        weatherMapping.put("heavy rain", "Mưa lớn");
        weatherMapping.put("thunderstorm", "Dông bão");
        weatherMapping.put("snow", "Tuyết");
        weatherMapping.put("light snow", "Tuyết nhẹ");
        weatherMapping.put("heavy snow", "Tuyết dày đặc");
        weatherMapping.put("fog", "Sương mù dày đặc");
        weatherMapping.put("drizzle", "Mưa phùn");
        weatherMapping.put("hail", "Mưa đá");
        weatherMapping.put("freezing rain", "Mưa đông đá");
        weatherMapping.put("patchy rain nearby", "Thất thường");
        weatherMapping.put("overcast", "Âm u");

        return weatherMapping.getOrDefault(condition, condition);
    }
}
