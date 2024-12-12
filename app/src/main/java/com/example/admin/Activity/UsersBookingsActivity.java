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

import com.example.admin.Adapter.TicketAdapter;
import com.example.admin.Adapter.UserAdapter;
import com.example.admin.Class.Ticket;
import com.example.admin.Class.User;
import com.example.admin.R;
import com.example.admin.databinding.ActivityBookingsBinding;
import com.example.admin.databinding.ActivityUsersBookingsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class UsersBookingsActivity extends AppCompatActivity {

    FirebaseDatabase database;
    ActivityUsersBookingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityUsersBookingsBinding.inflate(getLayoutInflater());
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
                finish();
            }
        });
        String id = getIntent().getStringExtra("id");
        String email = getIntent().getStringExtra("email");

        showInfor(id);
        initTicket(email);

        binding.userInfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UsersBookingsActivity.this, UserInforActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
    }



    private void showInfor(String id){
        DatabaseReference myRef = database.getReference("Users");
        myRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    binding.emailTxt.setText(user.getEmail());
                    binding.phoneTxt.setText(user.getPhone());
                    binding.nameTxt.setText(user.getName());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void initTicket(String email) {
        DatabaseReference myRef = database.getReference("Ticket");
        binding.progressBarTicket.setVisibility(View.VISIBLE);
        ArrayList<Ticket> list = new ArrayList<>();

        myRef.orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot i:snapshot.getChildren()){
                        Ticket ticket = i.getValue(Ticket.class);
                        if (String.valueOf(ticket.getEmail()).equals(email)){
                            list.add(ticket);
                        }
                    }
                    if (!list.isEmpty()){
                        // Đảo ngược danh sách để có thứ tự giảm dần
                        Collections.reverse(list);
                        binding.recyclerViewTicket.setLayoutManager(new LinearLayoutManager(UsersBookingsActivity.this,LinearLayoutManager.VERTICAL,false));
                        binding.recyclerViewTicket.setNestedScrollingEnabled(false);
                        RecyclerView.Adapter<TicketAdapter.Viewholder> adapter = new TicketAdapter(list);
                        binding.recyclerViewTicket.setAdapter(adapter);
                    }
                    binding.progressBarTicket.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}