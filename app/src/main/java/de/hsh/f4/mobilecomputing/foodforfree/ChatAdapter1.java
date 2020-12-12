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
import com.google.firebase.auth.FirebaseUser;

import static de.hsh.f4.mobilecomputing.foodforfree.R.color.weiss;
import static de.hsh.f4.mobilecomputing.foodforfree.R.color.white;

public class ChatAdapter1 extends FirestoreRecyclerAdapter<Message, ChatAdapter1.ChatHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    int type;

    String currentUserId;
    FirebaseUser fUser;
    FirebaseAuth fAuth;

    public ChatAdapter1(@NonNull FirestoreRecyclerOptions<Message> options) {
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
            holder.textViewMessage.setTextColor(Color.parseColor("white"));
            holder.textViewUserName.setTextColor(Color.parseColor("white"));
            holder.textViewTimestamp.setTextColor(Color.parseColor("white"));
        }


            /*fUser= FirebaseAuth.getInstance().getCurrentUser();
            if (model.getSender().equals(fUser.getUid())){
                type =  MSG_TYPE_RIGHT;
            } else{
                type =  MSG_TYPE_LEFT;
            }*/

    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatHolder(view);

        /*if ((viewType == MSG_TYPE_RIGHT)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
            return new ChatHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
            return new ChatHolder(view);
        }*/
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

    /*@Override
    public int getItemViewType(int position) {

        fUser= FirebaseAuth.getInstance().getCurrentUser();
        if (model.getSender().equals(fUser.getUid())){
            type =  MSG_TYPE_RIGHT;
        } else{
            type =  MSG_TYPE_LEFT;
        }

        if ( type == 1) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
     fUser= FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        } else{
            return MSG_TYPE_LEFT;
        }
    }*/
}