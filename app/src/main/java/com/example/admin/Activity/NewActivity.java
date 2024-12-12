package com.example.admin.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.admin.Class.Item;
import com.example.admin.R;
import com.example.admin.databinding.ActivityDetailBinding;
import com.example.admin.databinding.ActivityNewBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class NewActivity extends AppCompatActivity {

    FirebaseDatabase database;
    ActivityNewBinding binding;
    private ProgressDialog processDialog;

    private Uri uri;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK){
                        Intent intent = result.getData();
                        if (intent == null){
                            return;
                        }
                        uri = intent.getData();
                        Glide.with(NewActivity.this).load(uri).into(binding.pic);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityNewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        binding.pic.setImageResource(R.drawable.intro);
        processDialog = new ProgressDialog(this);
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRequestPermission();
            }
        });
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitAdd();
            }
        });
    }



    private void submitAdd() {
        String title = binding.titleTxt.getText().toString();
        String address = binding.addressTxt.getText().toString();
        String description = binding.descriptionTxt.getText().toString();
        String duration = binding.durationTxt.getText().toString();
        String priceStr = binding.priceTxt.getText().toString();
        String bedStr = binding.bedTxt.getText().toString();
        // Kiểm tra nếu bất kỳ trường nào bị bỏ trống
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(address) || TextUtils.isEmpty(description)
                || TextUtils.isEmpty(duration) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(bedStr)) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }
        int price = Integer.parseInt(priceStr);
        int bed = Integer.parseInt(bedStr);
        if (uri == null){
            Toast.makeText(NewActivity.this,"Please choose your picture",Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");  // Tạo tên ảnh duy nhất
        // Tải ảnh lên Firebase Storage
        processDialog.show();
        imageRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Sau khi tải lên thành công, lấy URL của ảnh từ Firebase Storage
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {

                        // Nhận được URL ảnh từ Firebase Storage
                        String imageUrl = uri.toString();

                        // tục lưu item và URL ảnh vào Firebase Database
                        DatabaseReference myRef = database.getReference("Item");
                        myRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int id = 0;  // Nếu chưa có phần tử nào, bắt đầu từ 0
                                for (DataSnapshot data:snapshot.getChildren()) {
                                    // Lấy khóa cuối cùng
                                    String lastKey = data.getKey();
                                    if (lastKey != null) {
                                        // Tạo khóa mới bằng cách tăng giá trị của khóa cuối cùng
                                        id = Integer.parseInt(lastKey)+1;
                                    }
                                    Item newItem = new Item(id, title, address, description, imageUrl, duration, price, bed, "300km", 4.5, 1, 0, 0,0);

                                    myRef.child(String.valueOf(id)).setValue(newItem, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            processDialog.dismiss();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(NewActivity.this);
                                            builder.setTitle("Add new sucessfull!");
                                            builder.setMessage("New tour has been added");
                                            builder.setPositiveButton("Finish", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    startActivity(new Intent(NewActivity.this, MainActivity.class));
                                                    finishAffinity();
                                                }
                                            });
                                            builder.setCancelable(false);
                                            AlertDialog alertDialog = builder.create();
                                            alertDialog.show();
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NewActivity.this,"Error uploading image",Toast.LENGTH_SHORT).show();
                    Log.e("Firebase", "Error uploading image", e);
                });
    }


    private void onClickRequestPermission(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){ //Kiểm tra phiên bản android
            openGallery();
            return;
        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){ //Kiểm tra quyền ứng dụng
            openGallery();
        }else {
            String [] permission = {Manifest.permission.READ_EXTERNAL_STORAGE}; //Yêu cầu quyền đọc dữ liệu
            requestPermissions(permission, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){ //Người dùng đồng ý cho quyền ứng dụng
                openGallery();
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Permission not granted");
                builder.setMessage("Please enable the storage permission in the app settings");
                builder.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uriSetting = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uriSetting);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("Finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }
}