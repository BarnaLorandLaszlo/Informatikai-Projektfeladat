package com.example.filmajnlalkalmazs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileImageAdapter extends RecyclerView.Adapter<ProfileImageAdapter.ViewHolder> {

    private final String[] imageNames;
    private final Context context;
    private final OnImageSelectListener listener;
    private int selectedPosition = -1;

    public interface OnImageSelectListener {
        void onImageSelected(String imageName);
    }

    public ProfileImageAdapter(Context context, String[] imageNames, OnImageSelectListener listener) {
        this.context = context;
        this.imageNames = imageNames;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageItem);
        }
    }

    @NonNull
    @Override
    public ProfileImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageName = imageNames[position];
        int resId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());

        holder.imageView.setImageResource(resId);

        // Kiemelés kiválasztáskor
        holder.imageView.setAlpha(position == selectedPosition ? 1.0f : 0.5f);

        holder.imageView.setOnClickListener(v -> {
            int previous = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previous);
            notifyItemChanged(selectedPosition);
            listener.onImageSelected(imageName);
        });
    }
    public void setSelectedImage(String imageName) {
        for (int i = 0; i < imageNames.length; i++) {
            if (imageNames[i].equals(imageName)) {
                selectedPosition = i;
                notifyDataSetChanged();
                break;
            }
        }
    }


    @Override
    public int getItemCount() {
        return imageNames.length;
    }
}

