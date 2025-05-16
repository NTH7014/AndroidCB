package com.example.weathergo.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.weathergo.Adapters.ViewPagerAdapter;
import com.example.weathergo.R;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    // Khai báo biến cho TabLayout và ViewPager2
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ViewPagerAdapter viewPagerAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
    // Cấu hình ActionBar (thanh tiêu đề trên cùng)
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setBackgroundDrawable(new ColorDrawable(Color.rgb(252, 242, 244))); // Đổi màu nền ActionBar
            toolbar.setElevation(0); // Bỏ đổ bóng (elevation)
            toolbar.setTitle(""); // Xóa tiêu đề mặc định
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Gắn layout cho MainActivity
        // Ánh xạ các view từ layout XML
        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager);
        // Khởi tạo ViewPagerAdapter và gắn vào ViewPager2
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Khi chọn tab, chuyển trang ViewPager2 tương ứng
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Không làm gì khi bỏ chọn tab
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Không làm gì khi re-select tab
            }
        });
        // Bắt sự kiện khi vuốt ViewPager2 để chuyển tab trên TabLayout tương ứng
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Tạo menu trên ActionBar
        getMenuInflater().inflate(R.menu.options_menu, menu); // options_menu.xml
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        // Nếu chọn menuItem1 thì chuyển sang màn hình SettingsActivity
        if (itemId == R.id.menuItem1){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}