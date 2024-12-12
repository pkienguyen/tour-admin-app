package com.example.admin.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.admin.Class.Item;
import com.example.admin.Class.Ticket;
import com.example.admin.Class.User;
import com.example.admin.R;
import com.example.admin.databinding.ActivityProfileBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    FirebaseDatabase database;
    ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });;

        database = FirebaseDatabase.getInstance();

        setVariable();
        bottomMenu();
        showInfor();


        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
        binding.account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, AccountStActivity.class));
            }
        });
        binding.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, AccountStActivity.class));
            }
        });
        binding.security.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, SecurityActivity.class));
            }
        });
        binding.customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, UserActivity.class));
            }
        });
        binding.bookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, BookingsActivity.class));
            }
        });
        binding.pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, PendingActivity.class));
            }
        });
        binding.revenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, RevenueActivity.class));
            }
        });
    }



    private void setVariable() {
        ArrayList<User> userlist = new ArrayList<>();
        ArrayList<Item> tourlist = new ArrayList<>();
        ArrayList<Ticket> bookinglist = new ArrayList<>();

        database.getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot i:snapshot.getChildren()){
                        User user = i.getValue(User.class);
                        if(user.getRole().equals("user")){
                            userlist.add(user);
                        }
                    }
                }
                binding.usersTxt.setText(""+userlist.size());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        database.getReference("Item").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot i:snapshot.getChildren()){
                        Item item = i.getValue(Item.class);
                        tourlist.add(item);
                    }
                }
                binding.tourTxt.setText(""+tourlist.size());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        database.getReference("Ticket").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot i:snapshot.getChildren()){
                        Ticket ticket = i.getValue(Ticket.class);
                        bookinglist.add(ticket);
                    }
                }
                binding.bookingsTxt.setText(""+bookinglist.size());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void logOut(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout confirmation");
        builder.setMessage("Do you want to log out?");
        // Nút "Có" để xác nhận đăng xuất
        builder.setPositiveButton("LogOut", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Xử lý đăng xuất tại đây
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                finishAffinity();
            }
        });
        // Nút "Không" để hủy
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Đóng dialog
                dialog.dismiss();
            }
        });
        // Đảm bảo người dùng không thể đóng dialog khi bấm ngoài
        builder.setCancelable(false);
        // Tạo và hiển thị dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showInfor() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            if (name != null){
                binding.tvEmail.setVisibility(View.VISIBLE);
                binding.tvUsername.setText(name);
            }else {
                binding.tvEmail.setVisibility(View.GONE);
            }
            binding.tvEmail.setText(email);
            Glide.with(this)
                    .load(photoUrl)
                    .error(R.drawable.ic_avatar_default)
                    .into(binding.avatar);
        }
    }

    private void bottomMenu(){
        binding.bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.profile) {
                    return true;
                } else if (itemId == R.id.home) {
                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.newtour) {
                    startActivity(new Intent(ProfileActivity.this, NewActivity.class));
                    return true;
                } else {
                    return false;
                }
            }
        });
        binding.bottomMenu.setSelectedItemId(R.id.profile);
    }
}