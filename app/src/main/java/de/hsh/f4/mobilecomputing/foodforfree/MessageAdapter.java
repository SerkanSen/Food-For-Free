package de.hsh.f4.mobilecomputing.foodforfree;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MessageAdapter extends FirestoreRecyclerAdapter<Message, MessageAdapter.MessageHolder> {

    private OnItemClickListener listener;
    private OnItemLongClickListener listener1;
    private ProgressBar progressBarAdItemPhoto;
    FirebaseAuth fAuth;
    String currentUserId;

    public MessageAdapter(@NonNull FirestoreRecyclerOptions<Message> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageHolder holder, int position, @NonNull Message model) {

        fAuth = FirebaseAuth.getInstance();
        currentUserId = fAuth.getCurrentUser().getUid();
        if (model.getOfferingUserID().equals(currentUserId)) {
            holder.textViewOtherUserName.setText(model.getInterestedUser());
        } else {
            holder.textViewOtherUserName.setText(model.getOfferingUser());
        }

        holder.textViewTime.setText(model.getLastTimestamp());
        holder.textViewAdTitle.setText(model.getAdTitle());
        holder.textViewLastMessage.setText(model.getLastMessage());

        progressBarAdItemPhoto.setVisibility(View.VISIBLE);
        Picasso.get()
                .load(model.getImageUrl())
                .fit()
                .centerCrop()
                .into(holder.imageViewAdPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBarAdItemPhoto.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageHolder(view);
    }

    public void deleteItem (int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class MessageHolder extends RecyclerView.ViewHolder {
        TextView textViewOtherUserName, textViewTime, textViewAdTitle, textViewLastMessage;
        ImageView imageViewAdPhoto;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            textViewOtherUserName = itemView.findViewById(R.id.message_item_other_user);
            textViewTime = itemView.findViewById(R.id.message_item_time);
            textViewAdTitle = itemView.findViewById(R.id.message_item_ad_title);
            textViewLastMessage = itemView.findViewById(R.id.message_item_message);
            imageViewAdPhoto = itemView.findViewById(R.id.message_item_ad_image);
            progressBarAdItemPhoto = itemView.findViewById(R.id.message_item_progressBar);

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

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener1 != null) {
                        listener1.onItemLongClick(getSnapshots().getSnapshot(position), position);
                    }
                    return false;
                }
            });

        }
    }

    //helps sending information from adapter to underlying activity
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(DocumentSnapshot snapshot, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener1) { this.listener1 = listener1; }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
