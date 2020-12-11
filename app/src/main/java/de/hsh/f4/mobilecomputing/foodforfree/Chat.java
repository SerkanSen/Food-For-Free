package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Chat extends AppCompatActivity {

    //Firestore for recyclerView
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference chatRef = db.collection("chats");

    private ChatAdapter1 adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Intent und msgID holen
        Intent intent = getIntent();
        String messageID = intent.getStringExtra(Messages.EXTRA_MSGID);

        setUpRecyclerView(messageID);
    }

    private void setUpRecyclerView(String msgId) {
        Query query = chatRef.whereEqualTo("msgID", msgId).orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message.class).build();

        adapter = new ChatAdapter1(options);

        Toast.makeText(Chat.this, "in setUp", Toast.LENGTH_SHORT).show();
        RecyclerView recyclerView = findViewById(R.id.recycler_view_chats);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}