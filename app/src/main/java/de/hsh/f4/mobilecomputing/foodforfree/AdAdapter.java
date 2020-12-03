package de.hsh.f4.mobilecomputing.foodforfree;

import android.content.Context;
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
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdAdapter extends FirestoreRecyclerAdapter<Ad, AdAdapter.AdHolder> {

    private OnItemClickListener listener;
    private Context mContext;

    public AdAdapter(@NonNull FirestoreRecyclerOptions<Ad> options) {
        super(options);
    }

    public AdAdapter(Context context, FirestoreRecyclerOptions<Ad> options) {
        super(options);
        mContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull AdHolder holder, int position, @NonNull Ad model) {
        holder.textViewTitle.setText(model.getTitle());
        holder.textViewAmount.setText(model.getAmount());
        holder.textViewPickupLocation.setText(model.getPickupLocation());
        holder.textViewFilter.setText(model.getFilterOptions());

        //erster Versuch: abgespeicherte URL mit Picasso in imageViewAdPhoto zu laden -> App öffnet nicht
        //Picasso.get().load(model.getImageUrl()).into(holder.imageViewAdPhoto);
        //Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/food-for-free-9663f.appspot.com/o/ads%2FXpSL9noJl9LbFfcqorBn%2FadPhoto.jpg?alt=media&token=43fe765a-a6a4-41f5-9547-2a467da4cb69")
        //       .into(holder.imageViewAdPhoto);


        //Picasso.get().load().into(holder.imageViewAdPhoto);

        //Glide.with(this).using(new FirebaseImageLoader()).load(storageReference).into(holder.imageViewAdPhoto);
        /*Glide.with(this  //context )
                .load(adRef)
                .into(holder.imageViewAdPhoto);*/
        //Glide.with(mContext).load(model.getImageUrl()).into(holder.imageViewAdPhoto);
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
