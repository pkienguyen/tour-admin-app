package com.example.admin.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.Adapter.PopularAdapter;
import com.example.admin.Adapter.UserAdapter;
import com.example.admin.Class.Item;
import com.example.admin.Class.User;
import com.example.admin.R;
import com.example.admin.databinding.ActivityMainBinding;
import com.example.admin.databinding.ActivityUserBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {

    ActivityUserBinding binding;
    FirebaseDatabase database;
    String keyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        database = FirebaseDatabase.getInstance();
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserActivity.this,ProfileActivity.class));
                finishAffinity();
            }
        });
        onClickSearch();
        initUser(keyword);
    }

    private void onClickSearch() {
        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyword = binding.searchTxt.getText().toString();
                initUser(keyword);
            }
        });
    }

    private void initUser(String keyword) {
        DatabaseReference myRef = database.getReference("Users");
        binding.progressBarItem.setVisibility(View.VISIBLE);
        ArrayList<User> list = new ArrayList<>();

        myRef.orderByChild("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot i:snapshot.getChildren()){
                        User user = i.getValue(User.class);
                        if(user.getEmail().toLowerCase().contains(keyword.toLowerCase()) && user.getRole().equals("user")){
                            list.add(user);
                        }
                    }
                    if (!list.isEmpty()){
                        binding.recyclerViewItem.setLayoutManager(new LinearLayoutManager(UserActivity.this,LinearLayoutManager.VERTICAL,false));
                        RecyclerView.Adapter<UserAdapter.Viewholder> adapter = new UserAdapter(list);
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