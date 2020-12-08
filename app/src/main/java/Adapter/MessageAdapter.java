package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.User;

import java.util.List;

import de.hsh.f4.mobilecomputing.foodforfree.Chat;
import de.hsh.f4.mobilecomputing.foodforfree.R;


//alle Chat referenzen => Chatting?


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageUrl;
    FirebaseUser fuser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageUrl) {
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if ((viewType == MSG_TYPE_RIGHT)) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat =mChat.get(position);
        holder.show_message.setText(chat.getMessage());
        if(imageUrl.equals("default")){
            holder.ad_item_image.setImageResource((R.mipmap.ic_launcher));
        }else{
            Glide.with(mContext).load(imageUrl).into(holder.ad_item_image);
        }
    }


        @Override
        public int getItemCount () {
            return mChat.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView ad_item_title, show_message;
            public ImageView ad_item_image;

            public ViewHolder(View itemView) {
                super(itemView);

                show_message = itemView.findViewById(R.id.show_message);
                ad_item_title = itemView.findViewById(R.id.ad_item_title);
                ad_item_image = itemView.findViewById(R.id.ad_item_image);

            }
        }

    @Override
    public int getItemViewType(int position) {
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }
}

