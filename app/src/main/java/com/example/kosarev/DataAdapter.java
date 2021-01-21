package com.example.kosarev;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    public void setSelectElementListener(SelectElementListener selectElementListener) {
        this.selectElementListener = selectElementListener;
    }

    SelectElementListener selectElementListener;
    public abstract static class SelectElementListener{
        public abstract void selectElement(User user);
    }
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
        final User user = users.get(position);
        holder.emailView.setText(user.getEmail());
        holder.nameView.setText(user.getName());
        holder.itenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectElementListener.selectElement(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView emailView;
        final TextView nameView;
        final View itenView;
        ViewHolder(View view){
            super(view);
            itenView = view;
            emailView = view.findViewById(R.id.emailTextView);
            nameView = (TextView) view.findViewById(R.id.nameTextView);
        }
    }
}
