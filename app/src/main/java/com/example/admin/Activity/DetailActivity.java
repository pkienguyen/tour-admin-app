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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DetailActivity extends AppCompatActivity {

    FirebaseDatabase database;
    ActivityDetailBinding binding;
    private Item item;
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
                        Glide.with(DetailActivity.this).load(uri).into(binding.pic);
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        processDialog = new ProgressDialog(this);
        database = FirebaseDatabase.getInstance();
        item = (Item) getIntent().getSerializableExtra("item");
        setVariable();

    }



    private void setVariable() {
        binding.titleTxt.setText(item.getTitle());
        binding.addressTxt.setText(item.getAddress());
        binding.ratingBar.setRating((float) item.getScore());
        binding.rateTxt.setText("" + item.getScore());
        binding.durationTxt.setText(item.getDuration());
        binding.bedTxt.setText("" + item.getBed());
        binding.descriptionTxt.setText(item.getDescription());
        binding.priceTxt.setText("" + item.getPrice());

        Glide.with(DetailActivity.this)
                .load(item.getPic())
                .into(binding.pic);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitChanges();
            }
        });
        binding.pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRequestPermission();
            }
        });
        binding.deleteTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickDelete();
            }
        });
    }

    private void onClickDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete confirmation");
        builder.setMessage("Do you want to delete this item?");
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Item");
                processDialog.show();
                myRef.child(String.valueOf(item.getId())).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            // Xóa thành công
                            processDialog.dismiss();
                            Toast.makeText(DetailActivity.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(DetailActivity.this, MainActivity.class));
                            finishAffinity();

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

    private void submitChanges() {
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update confirmation");
        builder.setMessage("Do you want to update this informations");
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Nếu ko chọn ảnh mới
                if (uri == null){
                    // lưu item và URL ảnh vào Firebase Database
                    DatabaseReference myRef = database.getReference("Item");
                    Item newItem = new Item(item.getId(), title, address, description, item.getPic(), duration, price, bed, "300km", 4.5, 1, 0, 0, item.getNumberOfBookings());
                    processDialog.show();
                    myRef.child(String.valueOf(item.getId())).setValue(newItem, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            processDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                            builder.setTitle("Update sucessfull!");
                            builder.setMessage("New tour has been updated");
                            builder.setPositiveButton("Finish", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(DetailActivity.this, MainActivity.class));
                                    finishAffinity();
                                }
                            });
                            builder.setCancelable(false);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    });
                } else {
                    // Nếu chọn ảnh mới thì tải ảnh mới lên
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

                                    // lưu item và URL ảnh vào Firebase Database
                                    DatabaseReference myRef = database.getReference("Item");
                                    Item newItem = new Item(item.getId(), title, address, description, imageUrl, duration, price, bed, "300km", 4.5, 1, 0, 0, item.getNumberOfBookings());

                                    myRef.child(String.valueOf(item.getId())).setValue(newItem, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            processDialog.dismiss();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                                            builder.setTitle("Update sucessfull!");
                                            builder.setMessage("New tour has been updated");
                                            builder.setPositiveButton("Finish", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    startActivity(new Intent(DetailActivity.this, MainActivity.class));
                                                    finishAffinity();
                                                }
                                            });
                                            builder.setCancelable(false);
                                            AlertDialog alertDialog = builder.create();
                                            alertDialog.show();
                                        }
                                    });
                                });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(DetailActivity.this, "Error uploading image", Toast.LENGTH_SHORT).show();
                                Log.e("Firebase", "Error uploading image", e);
                            });
                }
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