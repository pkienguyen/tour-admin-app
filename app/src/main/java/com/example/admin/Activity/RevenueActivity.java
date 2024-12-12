package com.example.admin.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.Adapter.ItemAdapter;
import com.example.admin.Adapter.PopularAdapter;
import com.example.admin.Class.Item;
import com.example.admin.R;
import com.example.admin.databinding.ActivityPendingBinding;
import com.example.admin.databinding.ActivityRevenueBinding;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RevenueActivity extends AppCompatActivity {

    ActivityRevenueBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRevenueBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        revenueMonths();
        revenueYears();
        initPopular();
    }

    private void revenueMonths(){
        DatabaseReference paymentRef = FirebaseDatabase.getInstance().getReference("Payment");

        ArrayList<BarEntry> entries = new ArrayList<>();
        List<String> months = new ArrayList<>(Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"));
        float[] revenue = new float[6]; // Mảng lưu doanh thu của 6 tháng gần nhất

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH); // Lấy tháng hiện tại (0-11)
        int currentYear = calendar.get(Calendar.YEAR);

        paymentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot paymentSnapshot : snapshot.getChildren()) {
                    String date = paymentSnapshot.child("date").getValue(String.class);
                    double amount = paymentSnapshot.child("totalPrice").getValue(Double.class);

                    // Kiểm tra nếu ngày và số tiền hợp lệ
                    if (date != null && amount > 0) {
                        try {
                            // Tách lấy năm và tháng từ chuỗi date (yyyy-MM-dd)
                            String[] dateParts = date.split("/");
                            int year = Integer.parseInt(dateParts[0]);
                            int month = Integer.parseInt(dateParts[1]) - 1; // Chuyển về index (0-11)

                            // Nếu là trong 6 tháng gần nhất
                            if (year == currentYear && month <= currentMonth && month >= currentMonth - 5) {
                                int index = currentMonth - month;
                                revenue[5 - index] += amount;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                // Cập nhật dữ liệu vào BarEntry
                for (int i = 0; i < 6; i++) {
                    entries.add(new BarEntry(i, revenue[i]));
                }

                // Cập nhật biểu đồ
                List<String> months = Arrays.asList("Jul", "Aug", "Sep", "Oct", "Nov", "Dec");

                binding.revenueMonths.getAxisRight().setEnabled(false);
                YAxis yAxis = binding.revenueMonths.getAxisLeft();
                yAxis.setAxisMinimum(0f);
                yAxis.setAxisMaximum(4000f); // Tùy chỉnh trục Y dựa trên doanh thu
                yAxis.setAxisLineWidth(2f);
                yAxis.setAxisLineColor(Color.GRAY);
                yAxis.setLabelCount(5);

                BarDataSet barDataSet = new BarDataSet(entries, "2024");
                barDataSet.setValueTextSize(10f);
                barDataSet.setColor(Color.LTGRAY);

                BarData barData = new BarData(barDataSet);
                binding.revenueMonths.setData(barData);

                binding.revenueMonths.getDescription().setEnabled(false);
                binding.revenueMonths.invalidate();

                XAxis xAxis = binding.revenueMonths.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to read data", error.toException());
            }
        });
    }


    private void revenueYears(){
        DatabaseReference paymentRef = FirebaseDatabase.getInstance().getReference("Payment");

        ArrayList<BarEntry> entries = new ArrayList<>();
        List<String> years = new ArrayList<>();
        float[] revenue = new float[3]; // Mảng lưu doanh thu cho 3 năm gần nhất

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

// Tạo danh sách các năm gần nhất
        for (int i = 2; i >= 0; i--) {
            years.add(String.valueOf(currentYear - i));
        }

        paymentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot paymentSnapshot : snapshot.getChildren()) {
                    String date = paymentSnapshot.child("date").getValue(String.class);
                    double amount = paymentSnapshot.child("totalPrice").getValue(Double.class);

                    // Kiểm tra nếu ngày và số tiền hợp lệ
                    if (date != null && amount > 0) {
                        try {
                            // Tách lấy năm từ chuỗi date (yyyy-MM-dd)
                            String[] dateParts = date.split("/");
                            int year = Integer.parseInt(dateParts[0]);

                            // Nếu thuộc 3 năm gần nhất
                            if (year >= currentYear - 2 && year <= currentYear) {
                                int index = currentYear - year; // Tính chỉ số cho mảng revenue
                                revenue[2 - index] += amount; // Tính tổng doanh thu cho từng năm
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                // Cập nhật dữ liệu vào BarEntry
                for (int i = 0; i < 3; i++) {
                    entries.add(new BarEntry(i, revenue[i]));
                }

                // Cập nhật biểu đồ
                binding.revenueYears.getAxisRight().setEnabled(false);
                YAxis yAxis = binding.revenueYears.getAxisLeft();
                yAxis.setAxisMinimum(0f);
                yAxis.setAxisMaximum(8000f); // Tùy chỉnh trục Y dựa trên doanh thu
                yAxis.setAxisLineWidth(2f);
                yAxis.setAxisLineColor(Color.GRAY);
                yAxis.setLabelCount(5);

                BarDataSet barDataSet = new BarDataSet(entries, "Revenue");
                barDataSet.setValueTextSize(10f);
                barDataSet.setColor(Color.LTGRAY);

                BarData barData = new BarData(barDataSet);
                binding.revenueYears.setData(barData);

                binding.revenueYears.getDescription().setEnabled(false);
                binding.revenueYears.invalidate();

                XAxis xAxis = binding.revenueYears.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(years));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to read data", error.toException());
            }
        });
    }

    private void initPopular() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Item");

        binding.progressBarItem.setVisibility(View.VISIBLE);
        ArrayList<Item> list = new ArrayList<>();

        myRef.orderByChild("numberOfBookings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot i:snapshot.getChildren()){
                        Item item = i.getValue(Item.class);
                        if (item.getNumberOfBookings() > 0){
                            list.add(item);
                        }
                    }
                    if (!list.isEmpty()){
                        // Đảo ngược danh sách để có thứ tự giảm dần
                        Collections.reverse(list);
                        binding.recyclerViewItem.setLayoutManager(new LinearLayoutManager(RevenueActivity.this,LinearLayoutManager.VERTICAL,false));
                        binding.recyclerViewItem.setNestedScrollingEnabled(false);
                        RecyclerView.Adapter<PopularAdapter.Viewholder> adapter = new PopularAdapter(list);
                        binding.recyclerViewItem.setAdapter(adapter);
                    }
                    binding.progressBarItem.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}