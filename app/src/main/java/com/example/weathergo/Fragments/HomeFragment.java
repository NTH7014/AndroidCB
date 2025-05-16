package com.example.weathergo.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.weathergo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class HomeFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    TextView cityTxt, realFeelTxt, cloudTxt, windSpeedTxt, humidityTxt, mainTempTxt, conditionTxt;
    ImageView conditionGif;
    private String selectedLocation;
    private String selectedUnit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        realFeelTxt = view.findViewById(R.id.realFeelTxt); // cảm nhận
        mainTempTxt = view.findViewById(R.id.mainTempTxt); // nhiệt độ chính
        humidityTxt = view.findViewById(R.id.humidityTxt); // độ ẩm
        windSpeedTxt = view.findViewById(R.id.windSpeedTxt); // tốc độ gió
        cloudTxt = view.findViewById(R.id.cloudTxt); // mây
        conditionTxt = view.findViewById(R.id.conditionTxt); // Điều kiện thời tiết
        cityTxt = view.findViewById(R.id.cityTxt); // tên tp
        conditionGif = view.findViewById(R.id.conditionView); //Hình ảnh động thể hiện điều kiện thời tiết

        sharedPreferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        selectedLocation = sharedPreferences.getString("location", "Hanoi"); // Lấy location từ SharedPreferences
        selectedUnit = sharedPreferences.getString("unit", "Celsius"); // Lấy unit từ SharedPreferences

        getWeatherData();
        return view;
    }

    public void getWeatherData() {
        String url = "https://api.weatherapi.com/v1/forecast.json?key=3e863d90628d41b2a6e72023232709&q=" + selectedLocation + "&days=1&aqi=no&alerts=no";
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        //Tạo JsonObjectRequest để gửi GET và nhận về JSONObject
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                cityTxt.setText(selectedLocation);

                // Nhiệt độ chính
                JSONObject currentObject = response.getJSONObject("current");
                double temperature = currentObject.getDouble("temp_c");
                String tempCelText = String.valueOf((int) temperature);
                double temperatureF = temperature * 9 / 5 + 32;
                String tempFahText = String.valueOf((int) temperatureF);

                if (selectedUnit.equals("Celsius")) {
                    mainTempTxt.setText(tempCelText + "°C");
                } else if (selectedUnit.equals("Fahrenheit")) {
                    mainTempTxt.setText(tempFahText + "°F");
                }

                // Điều kiện thời tiết
                JSONObject conditionObject = currentObject.getJSONObject("condition");
                String textCondition = conditionObject.getString("text");
                String normalizedCondition = textCondition.trim().toLowerCase();

                // Mã hóa điều kiện thời tiết thành các giá trị hiển thị
                HashMap<String, String> weatherMapping = new HashMap<>();
                weatherMapping.put("clear", "Trời quang");
                weatherMapping.put("rainy", "Mưa");
                weatherMapping.put("cloudy", "Nhiều mây");
                weatherMapping.put("partly cloudy", "Mây rải rác");
                weatherMapping.put("sunny", "Nắng");
                weatherMapping.put("moderate rain", "Mưa vừa");
                weatherMapping.put("light rain", "Mưa nhỏ");
                weatherMapping.put("light drizzle", "Mưa bụi");
                weatherMapping.put("overcast", "Âm u");
                weatherMapping.put("patchy rain nearby", "Thất thường");
                weatherMapping.put("mist", "Sương mù");
                weatherMapping.put("fog", "Nhiều sương");
                weatherMapping.put("light rain shower", "Mưa rào nhẹ");
                weatherMapping.put("light sleet", "Tuyết rơi nhẹ");

                String displayValue = weatherMapping.getOrDefault(normalizedCondition, textCondition);
                conditionTxt.setText(displayValue);

                // Cập nhật ảnh động cho điều kiện thời tiết
                switch (displayValue) {
                    case "Thất thường":
                        Glide.with(this).load(R.drawable.patchy_rain_nearby).into(conditionGif);
                        break;
                    case "Mưa nặng hạt có sấm sét":
                        Glide.with(this).load(R.drawable.rain_thunder).into(conditionGif);
                        break;
                    case "Mưa có sấm sét":
                        Glide.with(this).load(R.drawable.rain_thunder).into(conditionGif);
                        break;
                    case "Có thể mưa":
                        Glide.with(this).load(R.drawable.moderate_rain).into(conditionGif);
                        break;
                    case "Âm u":
                        Glide.with(this).load(R.drawable.overcast).into(conditionGif);
                        break;
                    case "Mưa nhỏ":
                        Glide.with(this).load(R.drawable.moderate_rain).into(conditionGif);
                        break;
                    case "Mưa vừa":
                        Glide.with(this).load(R.drawable.moderate_rain).into(conditionGif);
                        break;
                    case "Mưa rào nhẹ":
                        Glide.with(this).load(R.drawable.moderate_rain).into(conditionGif);
                        break;
                    case "Mây rải rác":
                        Glide.with(this).load(R.drawable.cloudy_main_frame).into(conditionGif);
                        break;
                    case "Nhiều mây":
                        Glide.with(this).load(R.drawable.cloudy_main_frame).into(conditionGif);
                        break;
                    case "Trời quang":
                        Glide.with(this).load(R.drawable.clear_mainframe).into(conditionGif);
                        break;
                    case "Nắng":
                        Glide.with(this).load(R.drawable.sunnygif_mainframe).into(conditionGif);
                        break;
                    case "Mưa":
                        Glide.with(this).load(R.drawable.rain_condition_mainframe).into(conditionGif);
                        break;
                    case "Mưa bụi":
                        Glide.with(this).load(R.drawable.mb).into(conditionGif);
                        break;
                    case "Sương mù":
                        Glide.with(this).load(R.drawable.mist_mainframe).into(conditionGif);
                        break;
                    case "Nhiều sương":
                        Glide.with(this).load(R.drawable.mist_mainframe).into(conditionGif);
                        break;
                    case "Tuyết rơi nhẹ":
                        Glide.with(this).load(R.drawable.tr).into(conditionGif);
                        break;
                }

                // Nhiệt độ cảm nhận
                int realFeelTemp = currentObject.getInt("feelslike_c");
                if (selectedUnit.equals("Celsius")) {
                    realFeelTxt.setText(realFeelTemp + "°C");
                } else if (selectedUnit.equals("Fahrenheit")) {
                    int realFeelTempF = realFeelTemp * 9 / 5 + 32;
                    realFeelTxt.setText(realFeelTempF + "°F");
                }

                // Độ ẩm
                int humidity = currentObject.getInt("humidity");
                humidityTxt.setText(humidity + "%");

                // Tốc độ gió
                double windSpeed = currentObject.getDouble("wind_kph");
                windSpeedTxt.setText(windSpeed + " km/h");

                // Mây
                int cloud = currentObject.getInt("cloud");
                cloudTxt.setText(cloud + "%");

            } catch (JSONException e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }, error -> Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show());

        queue.add(request);
    }
}
