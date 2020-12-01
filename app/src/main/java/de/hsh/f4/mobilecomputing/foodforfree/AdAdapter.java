package de.hsh.f4.mobilecomputing.foodforfree;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdAdapter extends FirestoreRecyclerAdapter<Ad, AdAdapter.AdHolder> {

    private OnItemClickListener listener;
    StorageReference storageReference;

    public AdAdapter(@NonNull FirestoreRecyclerOptions<Ad> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdHolder holder, int position, @NonNull Ad model) {
        holder.textViewTitle.setText(model.getTitle());
        holder.textViewAmount.setText(model.getAmount());
        holder.textViewPickupLocation.setText(model.getPickupLocation());
        holder.textViewFilter.setText(model.getFilterOptions());

        //Picasso.get().load(model.getImageUrl()).into(holder.imageViewAdPhoto);
        //holder.imageViewAdPhoto.(model.getImageUrl());

        //StorageReference fileRef = storageReference.child("ads/"+ model.getAdID() +"/adPhoto.jpg");

        /*fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.imageViewAdPhoto);
            }
        });*/
        //Picasso.get().load(model.getImageUrl()).into(holder.imageViewAdPhoto);
    }

    @NonNull
    @Override
    public AdHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_item, parent, false);
        return new AdHolder(view);
    }

    class AdHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDescription, textViewIngredients, textViewPickupLocation, textViewAmount, textViewFilter;
        ImageView imageViewAdPhoto;

        public AdHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.ad_item_title);
            //textViewDescription = itemView.findViewById(R.id.ad_item_des);
            textViewPickupLocation = itemView.findViewById(R.id.ad_item_pickup_location);
            textViewAmount = itemView.findViewById(R.id.ad_item_portion);
            textViewFilter = itemView.findViewById(R.id.ad_item_filter);
            imageViewAdPhoto = itemView.findViewById(R.id.adDetails_image);

            //OnClick for each item(card)
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

        }
    }

    //helps sending information from adapter to underlying activity
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
