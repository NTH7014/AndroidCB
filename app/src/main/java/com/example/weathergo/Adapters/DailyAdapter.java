package com.example.weathergo.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weathergo.Domains.Daily;
import com.example.weathergo.R;

import java.util.ArrayList;

public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.viewHolder> {
    ArrayList<Daily> items;
    Context context;

    public DailyAdapter(ArrayList<Daily> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override

    public DailyAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_daily, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyAdapter.viewHolder holder, int position) {
        holder.dayTxt.setText(items.get(position).getDay());

        String picPath = items.get(position).getDailyPicPath();
        Glide.with(holder.itemView.getContext()).load("https:"+picPath).into(holder.dailyPic);

        holder.conditionTxt.setText(items.get(position).getCondition());
        // Lấy nhiệt độ theo đơn vị chọn từ SharedPreferences
        int Mintemp = items.get(position).getMinTemp();
        int Maxtemp = items.get(position).getMaxTemp();
        SharedPreferences sharedPreferences = holder.itemView.getContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String selectedUnit = sharedPreferences.getString("unit", "Celsius"); // Mặc định là Celsius

        // Kiểm tra đơn vị và hiển thị nhiệt độ phù hợp
        if (selectedUnit.equals("Celsius")) {
            holder.minTempTxt.setText(Mintemp + "°C");
            holder.maxTempTxt.setText(Maxtemp + "°C");
        } else if (selectedUnit.equals("Fahrenheit")) {
            holder.minTempTxt.setText(Mintemp + "°F");
            holder.maxTempTxt.setText(Maxtemp + "°F");
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        TextView dayTxt, conditionTxt, minTempTxt, maxTempTxt;
                ImageView dailyPic;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            dayTxt = itemView.findViewById(R.id.dayTxt);
            conditionTxt = itemView.findViewById(R.id.conditionTxt);
            minTempTxt = itemView.findViewById(R.id.minTempTxt);
            maxTempTxt = itemView.findViewById(R.id.maxTempTxt);
            dailyPic = itemView.findViewById(R.id.dailyPic);
        }
    }
}
