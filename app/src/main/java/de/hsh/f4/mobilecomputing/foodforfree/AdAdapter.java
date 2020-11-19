package de.hsh.f4.mobilecomputing.foodforfree;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AdAdapter extends FirestoreRecyclerAdapter<Ad, AdAdapter.AdHolder> {

    public AdAdapter(@NonNull FirestoreRecyclerOptions<Ad> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdHolder holder, int position, @NonNull Ad model) {
        holder.textViewTitle.setText(model.getTitle());
        holder.textViewAmount.setText(model.getAmount());
        holder.textViewPickupLocation.setText(model.getPickupLocation());
    }

    @NonNull
    @Override
    public AdHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_item, parent, false);
        return new AdHolder(view);
    }

    class AdHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDescription, textViewIngredients, textViewPickupLocation, textViewAmount;

        public AdHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.ad_item_title);
            //textViewDescription = itemView.findViewById(R.id.ad_item_des);
            textViewPickupLocation = itemView.findViewById(R.id.ad_item_pickup_location);
            textViewAmount = itemView.findViewById(R.id.ad_item_portion);
            //textViewTitle = itemView.findViewById(R.id.ad_item_title);

        }
    }
}
