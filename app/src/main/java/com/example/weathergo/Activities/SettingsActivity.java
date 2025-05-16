package com.example.weathergo.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.weathergo.widget.WeatherWidgetProvider;
import com.example.weathergo.R;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String selectedUnit = "Celsius"; // Đơn vị mặc định là Celsius
    private String selectedLocation = "Hanoi"; // Địa điểm mặc định

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        EditText locationEdt = findViewById(R.id.locationEdt);
        Button saveButton = findViewById(R.id.saveButton);

        // Lấy dữ liệu từ SharedPreferences
        selectedUnit = sharedPreferences.getString("unit", "Celsius");
        selectedLocation = sharedPreferences.getString("location", "Hanoi");

        // Hiển thị giá trị hiện tại trên giao diện
        locationEdt.setText(selectedLocation);
        if (selectedUnit.equals("Celsius")) {
            RadioButton celsiusRadioButton = findViewById(R.id.celsiusRadioButton);
            celsiusRadioButton.setChecked(true);
        } else if (selectedUnit.equals("Fahrenheit")) {
            RadioButton fahrenheitRadioButton = findViewById(R.id.fahrenheitRadioButton);
            fahrenheitRadioButton.setChecked(true);
        }

        // Xử lý thay đổi  đơn vị nhiệt độ trong RadioGroup
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.celsiusRadioButton) {
                selectedUnit = "Celsius";
            } else if (checkedId == R.id.fahrenheitRadioButton) {
                selectedUnit = "Fahrenheit";
            }
        });

        // Xử lý nút Lưu
        saveButton.setOnClickListener(view -> {
            // Lưu cài đặt vào SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            selectedLocation = locationEdt.getText().toString().trim();
            editor.putString("unit", selectedUnit);
            editor.putString("location", selectedLocation);
            editor.apply();

            // Cập nhật widget thời tiết
            updateWeatherWidgets();

            // Quay lại MainActivity
            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
        });
    }

    // Cập nhật widget khi cài đặt thay đổi
    private void updateWeatherWidgets() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName componentName = new ComponentName(this, WeatherWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);

        if (appWidgetIds != null && appWidgetIds.length > 0) {
            for (int appWidgetId : appWidgetIds) {
                WeatherWidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetId);
            }
        }
    }
}
