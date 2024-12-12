package com.example.admin.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.admin.Class.Ticket;
import com.example.admin.R;
import com.example.admin.databinding.ActivityTicketBinding;
import com.example.admin.databinding.ActivityTicketRemovedBinding;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TicketRemovedActivity extends AppCompatActivity {

    FirebaseDatabase database;
    ActivityTicketRemovedBinding binding;
    private Ticket ticket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityTicketRemovedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.reinstateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reinstateTicket();
            }
        });

        database = FirebaseDatabase.getInstance();
        ticket = (Ticket) getIntent().getSerializableExtra("ticket");

        setVariable();
    }



    private void reinstateTicket() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TicketRemovedActivity.this);
        builder.setTitle("Reinstate confirmation");
        builder.setMessage("Are you sure you want to reinstate this booking?");
        builder.setPositiveButton("Reinstate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference myRef = database.getReference("Ticket");

                myRef.child(String.valueOf(ticket.getId())).child("status").setValue(0, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        startActivity(new Intent(TicketRemovedActivity.this, BookingsActivity.class));
                        finish();
                    }
                });
            }
        });
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

    private void setVariable() {
        binding.emailTxt.setText(ticket.getEmail());
        binding.titleTxt.setText(ticket.getTitle());
        binding.visitDateTxt.setText(ticket.getVisitDate());
        binding.durationTxt.setText(ticket.getDuration());
        binding.personsTxt.setText(""+ticket.getPersons());
        binding.idTxt.setText("Order Id: "+ticket.getId());
    }
}