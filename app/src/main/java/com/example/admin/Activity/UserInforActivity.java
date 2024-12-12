package com.example.admin.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.admin.Class.User;
import com.example.admin.R;
import com.example.admin.databinding.ActivityUserInforBinding;
import com.example.admin.databinding.ActivityUsersBookingsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserInforActivity extends AppCompatActivity {

    FirebaseDatabase database;
    ActivityUserInforBinding binding;
    private ProgressDialog processDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityUserInforBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        processDialog = new ProgressDialog(this);

        String id = getIntent().getStringExtra("id");

        showInfor(id);

        binding.deleteTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickDelete(id);
            }
        });
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitChanges(id);
            }
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

    private void onClickDelete(String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove confirmation");
        builder.setMessage("Do you want to remove this customer?");
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference myRef = database.getReference("Users");
                processDialog.show();
                myRef.child(id).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            // Xóa thành công
                            processDialog.dismiss();
                            Toast.makeText(UserInforActivity.this, "Removed successfully", Toast.LENGTH_SHORT).show();
                            finish();

                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firebase", "Error deleting item", e);
                        });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // Đảm bảo người dùng không thể đóng dialog khi bấm ngoài
        builder.setCancelable(false);
        // Tạo và hiển thị dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void submitChanges(String id){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update confirmation");
        builder.setMessage("Do you want to update this informations");
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = binding.nameTxt.getText().toString().trim();
                String phone = binding.phoneTxt.getText().toString().trim();

                DatabaseReference myRef = database.getReference("Users");
                myRef.child(id).child("name").setValue(name);
                myRef.child(id).child("phone").setValue(phone);

                Toast.makeText(UserInforActivity.this,"Informations updated", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // Đảm bảo người dùng không thể đóng dialog khi bấm ngoài
        builder.setCancelable(false);
        // Tạo và hiển thị dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}