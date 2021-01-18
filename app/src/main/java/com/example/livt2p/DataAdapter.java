package com.example.livt2p;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<User> users;

    DataAdapter(Context context, List<User> users) {
        this.users = users;
        this.inflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        holder.emailView.setText(user.getEmail());
        holder.nameView.setText(user.getName());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView emailView;
        final TextView nameView;
        ViewHolder(View view){
            super(view);
            emailView = view.findViewById(R.id.emailTextView);
            nameView = (TextView) view.findViewById(R.id.nameTextView);
        }
    }
}
