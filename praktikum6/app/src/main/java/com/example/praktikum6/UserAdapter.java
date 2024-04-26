package com.example.praktikum6;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    DetailsViewInterface detailsViewInterface;
    private List<User> users;
    public UserAdapter(List<User> users, DetailsViewInterface detailsViewInterface) {
        this.users = users;
        this.detailsViewInterface = detailsViewInterface;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.person_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User user = users.get(position);

        Picasso.get().load(user.getAvatar()).into(holder.pfp);
        holder.name.setText(user.getFirst_name()+" "+user.getLast_name());
        holder.email.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pfp;
        ConstraintLayout rectangle;
        TextView name, email;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pfp = itemView.findViewById(R.id.pfp);
            name = itemView.findViewById(R.id.nama);
            email = itemView.findViewById(R.id.email);
            rectangle = itemView.findViewById(R.id.userItem);
            rectangle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (detailsViewInterface!= null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            detailsViewInterface.onItemClickToProfile(pos);
                        }
                    }
                }
            });
        }
    }
}
