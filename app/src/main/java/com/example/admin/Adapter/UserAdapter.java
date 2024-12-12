package com.example.admin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.Activity.DetailActivity;
import com.example.admin.Activity.TicketActivity;
import com.example.admin.Activity.UsersBookingsActivity;
import com.example.admin.Class.User;
import com.example.admin.databinding.ViewholderUserBinding;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.Viewholder> {
    private final ArrayList<User> users;
    private Context context;
    ViewholderUserBinding binding;

    public UserAdapter(ArrayList<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = ViewholderUserBinding.inflate(inflater,parent,false);

        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        User user = users.get(position);
        holder.binding.nameTxt.setText(user.getName());
        holder.binding.emailTxt.setText(user.getEmail());
        holder.binding.NoBTxt.setText("Number of bookings:"+user.getNumberOfBookings());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UsersBookingsActivity.class);
                intent.putExtra("id", user.getId());
                intent.putExtra("email", user.getEmail());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        public final ViewholderUserBinding binding;
        public Viewholder(ViewholderUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
