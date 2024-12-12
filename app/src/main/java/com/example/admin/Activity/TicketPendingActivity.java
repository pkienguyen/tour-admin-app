package com.example.admin.Activity;

import android.animation.ObjectAnimator;
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
import com.example.admin.databinding.ActivityTicketPendingBinding;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TicketPendingActivity extends AppCompatActivity {

    FirebaseDatabase database;
    ActivityTicketPendingBinding binding;
    private Ticket ticket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityTicketPendingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmTicket();
            }
        });

        database = FirebaseDatabase.getInstance();
        ticket = (Ticket) getIntent().getSerializableExtra("ticket");

        setVariable();
    }


    private void confirmTicket() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TicketPendingActivity.this);
        builder.setTitle("Booking confirmation");
        builder.setMessage("Are you sure you want to confirm this booking?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference myRef = database.getReference("Ticket");

                myRef.child(String.valueOf(ticket.getId())).child("status").setValue(2, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        startActivity(new Intent(TicketPendingActivity.this, PendingActivity.class));
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

    private void removeTicket() {
        String note = binding.noteTxt.getText().toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(TicketPendingActivity.this);
        builder.setTitle("Remove confirmation");
        builder.setMessage("Are you sure you want to remove this booking?");
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference myRef = database.getReference("Ticket");

                myRef.child(String.valueOf(ticket.getId())).child("status").setValue(1);
                myRef.child(String.valueOf(ticket.getId())).child("note").setValue(note, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        startActivity(new Intent(TicketPendingActivity.this, PendingActivity.class));
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

        binding.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.bookingCardView.setVisibility(View.VISIBLE);
                binding.overlayView.setVisibility(View.VISIBLE);
                binding.scrollView.setOnTouchListener((v, event) -> true); //Chặn thao tác vuốt màn hình
                binding.backBtn.setClickable(false);
                ObjectAnimator animation = ObjectAnimator.ofFloat(binding.bookingCardView, "translationY", 1000f, 0f);
                animation.setDuration(300);  // Thời gian trượt lên (300ms)
                animation.start();
            }
        });
        binding.confRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeTicket();
            }
        });
        binding.hideCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.bookingCardView.setVisibility(View.GONE);
                binding.overlayView.setVisibility(View.GONE);
                binding.scrollView.setOnTouchListener(null); //Bỏ chặn thao tác vuốt màn hình
                binding.backBtn.setClickable(true);
            }
        });
    }
}