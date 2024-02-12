package com.example.pawadoptjava;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

// AnimalAdapter.java
// AnimalAdapter.java
public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder> {
    private static final String TAG = "AnimalAdapter";
    private Context context = null;
    private List<Animal> animalList;


    public AnimalAdapter(Context context,List<Animal> animalList) {
        this.animalList = animalList;
        this.context = context;
    }

    @NonNull
    @Override
    public AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_animal, parent, false);
        return new AnimalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalViewHolder holder, int position) {
        Animal animal = animalList.get(position);

        Picasso.get().load(animal.getPhotos().get(0)).into(holder.imageView);
        holder.animalName.setText(animal.getName());
        holder.animalGender.setText(animal.getGender());
        holder.animalBreed.setText(animal.getBreed());
        holder.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AnimalDetailsActivity.class);
            intent.putExtra("animal_id", animal.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {

        return animalList.size();

    }

    static class AnimalViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView animalName;
        TextView animalGender;
        TextView animalBreed;
        AnimalViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);

            animalName = itemView.findViewById(R.id.animalName);
            animalGender = itemView.findViewById(R.id.animalGender);
            animalBreed = itemView.findViewById(R.id.animalBreed);
        }
    }
}
