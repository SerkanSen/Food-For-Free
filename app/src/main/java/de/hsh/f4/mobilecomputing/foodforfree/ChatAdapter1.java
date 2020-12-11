package de.hsh.f4.mobilecomputing.foodforfree;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChatAdapter1 extends FirestoreRecyclerAdapter<Message, ChatAdapter1.MessageHolder> {

    //push
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    int type;

    FirebaseUser fUser;

    public ChatAdapter1(@NonNull FirestoreRecyclerOptions<Message> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageHolder holder, int position, @NonNull Message model) {
        holder.textViewMessage.setText(model.getMessage());

            fUser= FirebaseAuth.getInstance().getCurrentUser();
            if (model.getSender().equals(fUser.getUid())){
                type =  MSG_TYPE_RIGHT;
            } else{
                type =  MSG_TYPE_LEFT;
            }

    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        //return new MessageHolder(view);

        if ((viewType == MSG_TYPE_RIGHT)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
            return new MessageHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
            return new MessageHolder(view);
        }
    }

    class MessageHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.show_message);

        }
    }

    @Override
    public int getItemViewType(int position) {

        if ( type == 1) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
        /*fUser= FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        } else{
            return MSG_TYPE_LEFT;
        }*/
    }
}