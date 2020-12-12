package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Chat extends AppCompatActivity {

    //Firestore for recyclerView
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference chatRef = db.collection("chats");

    private ChatAdapter1 adapter;

    TextView otherUserName;
    ImageButton sendMsgBtn;
    EditText message;
    FirebaseAuth fAuth;
    String userId, userName;

    public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendMsgBtn = findViewById(R.id.send);
        message = findViewById(R.id.msg);
        otherUserName = findViewById(R.id.otherUserName);


        fAuth = FirebaseAuth.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        DocumentReference userRef = db.collection("users").document(userId);
        userRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                userName = documentSnapshot.getString("name");
            }
        });

        //Intent und msgID holen
        Intent intent = getIntent();
        String messageID = intent.getStringExtra(Messages.EXTRA_MSGID);



        setUpRecyclerView(messageID);
        //adapter.startListening();
        //Toast.makeText(Chat.this, messageID, Toast.LENGTH_SHORT).show();

        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newMessage = message.getText().toString().trim();
                String senderName = userName;

                DocumentReference documentReference = db.collection("chats").document();

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String timestamp = simpleDateFormat.format(calendar.getTime());

                Map<String, Object> chat = new HashMap<>();
                //chat.put("adID", adId);
                chat.put("msgID", messageID);
                chat.put("sender", userId);
                chat.put("senderName", senderName);
                chat.put("message", newMessage);
                chat.put("timestamp", timestamp);
                //chat.put("participants", Arrays.asList(participants));

                documentReference.set(chat).addOnSuccessListener((OnSuccessListener) (aVoid) -> {

                    Log.d(TAG, "onSuccess: Anzeige erfolgreich erstellt!");
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
                message.setText("");
                adapter.startListening();
            }
        });


    }

    private void setUpRecyclerView(String msgId) {
        Query query = chatRef.whereEqualTo("msgId", msgId).orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message.class).build();

        adapter = new ChatAdapter1(options);

        //Toast.makeText(Chat.this, "in setUp", Toast.LENGTH_SHORT).show();
        RecyclerView recyclerView = findViewById(R.id.recycler_view_chats);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    //when app updates new data from firestore
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    //does not update, when app is in the background, saves ressources
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void ClickBack() {
        startActivity(new Intent(getApplicationContext(), Messages.class));
    }

}
