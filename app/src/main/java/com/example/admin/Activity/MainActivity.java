package com.example.admin.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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
import com.example.admin.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseDatabase database;
    String keyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
        }
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,MainActivity.class));
                finish();
            }
        });
        onClickSearch();
        initItem(keyword);
        bottomMenu();
    }



    private void onClickSearch() {
        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyword = binding.searchTxt.getText().toString();
                initItem(keyword);
            }
        });
    }

    private void initItem(String keyword) {
        DatabaseReference myRef = database.getReference("Item");
        binding.progressBarItem.setVisibility(View.VISIBLE);
        ArrayList<Item> list = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot i:snapshot.getChildren()){
                        Item item = i.getValue(Item.class);
                        if(item.getTitle().toLowerCase().contains(keyword.toLowerCase())){
                            list.add(item);
                        }
                    }
                    if (!list.isEmpty()){
                        binding.recyclerViewItem.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false));
                        RecyclerView.Adapter<ItemAdapter.Viewholder> adapter = new ItemAdapter(list);
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

    private void bottomMenu(){
        binding.bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.home) {
                    return true;
                } else if (itemId == R.id.newtour) {
                    startActivity(new Intent(MainActivity.this, NewActivity.class));
                    return true;
                } else if (itemId == R.id.profile) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    finish();
                    return true;
                } else {
                    return false;
                }
            }
        });
        binding.bottomMenu.setSelectedItemId(R.id.home);
    }
}