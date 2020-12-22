package de.hsh.f4.mobilecomputing.foodforfree;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;

public class ChatAdapter extends FirestoreRecyclerAdapter<Message, ChatAdapter.ChatHolder> {

    String currentUserId;
    FirebaseAuth fAuth;

    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Message> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatHolder holder, int position, @NonNull Message model) {
        holder.textViewMessage.setText(model.getMessage());
        holder.textViewUserName.setText(model.getSenderName());
        holder.textViewTimestamp.setText(model.getTimestamp());

        fAuth = FirebaseAuth.getInstance();
        currentUserId = fAuth.getCurrentUser().getUid();
        if(model.getSender().equals(currentUserId)) {
            holder.cardView.setBackgroundResource(R.color.green);
            holder.textViewUserName.setText("");
            holder.textViewMessage.setTextColor(Color.parseColor("white"));
            holder.textViewTimestamp.setTextColor(Color.parseColor("white"));
        }
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatHolder(view);
    }

    class ChatHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage, textViewUserName, textViewTimestamp;
        CardView cardView;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewChat);
            textViewMessage = itemView.findViewById(R.id.show_message);
            textViewUserName = itemView.findViewById(R.id.userName);
            textViewTimestamp = itemView.findViewById(R.id.timestamp);
        }
    }
}