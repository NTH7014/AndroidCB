package com.example.weathergo.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weathergo.Domains.Hourly;
import com.example.weathergo.R;

import java.util.ArrayList;

public class HourlyAdapters extends RecyclerView.Adapter<HourlyAdapters.viewHolder> {
    ArrayList<Hourly> items; //  danh sách các đối tượng Hourly
    Context context;

    public HourlyAdapters(ArrayList<Hourly> items, Context context) {
        this.context = context;
        this.items = items;
        //Nhận danh sách dữ liệu và context từ bên ngoài khi khởi tạo Adapter.
    }

    @NonNull
    @Override
    public HourlyAdapters.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_hourly, parent, false); //Inflate (nạp) layout viewholder_hourly.xml cho từng item.
        return new viewHolder(view); //Tạo và trả về một viewHolder để RecyclerView dùng.
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyAdapters.viewHolder holder, int position) {
        // Lấy giờ theo vị trí
        holder.hourTxt.setText(items.get(position).getHour());

        // Lấy nhiệt độ theo đơn vị chọn từ SharedPreferences
        int temp = items.get(position).getTemp();
        SharedPreferences sharedPreferences = holder.itemView.getContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String selectedUnit = sharedPreferences.getString("unit", "Celsius"); // Mặc định là Celsius

        // Kiểm tra đơn vị và hiển thị nhiệt độ phù hợp
        if (selectedUnit.equals("Celsius")) {
            holder.tempTxt.setText(temp + "°C");
        } else if (selectedUnit.equals("Fahrenheit")) {
            holder.tempTxt.setText(temp + "°F");
        }

        // Lấy đường dẫn ảnh và tải ảnh bằng Glide
        String picPath = items.get(position).getPicPath();
        Log.d("PicPathDebug", "PicPath: " + picPath); // In đường dẫn vào Logcat
        Glide.with(holder.itemView.getContext()).load("https:" + picPath).into(holder.pic);
        Log.d("Test", "Load ảnh thành công");
    }


    @Override
    public int getItemCount() {
        return items.size();
    } // Đếm sl và Trả về số lượng item có trong danh sách items

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView hourTxt, tempTxt;
        ImageView pic;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            hourTxt = itemView.findViewById(R.id.hourTxt);
            tempTxt = itemView.findViewById(R.id.tempTxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}
