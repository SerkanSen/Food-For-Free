package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
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
    String userId, userName, offeringUserID, offeringUserName, interestedUserName;

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

        //Intent und msgID+interestUserName holen
        Intent intent = getIntent();
        String messageID = intent.getStringExtra(Messages.EXTRA_MSGID);

        //aktuellen Nutzernamen holen
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                userName = documentSnapshot.getString("name");
            }
        });

        //richtigen Nutzernamen in der Leiste anzeigen
        DocumentReference chatRef = db.collection("chats").document(messageID);
        chatRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                offeringUserID = documentSnapshot.getString("offeringUserID");
                //interestedUserID = documentSnapshot.getString("interestedUserID");
                offeringUserName = documentSnapshot.getString("offeringUser");
                interestedUserName = documentSnapshot.getString("interestedUser");

                if (userId.equals(offeringUserID)){
                    otherUserName.setText("    "+interestedUserName);
                } else {
                    otherUserName.setText("    "+offeringUserName);
                }
            }
        });

        setUpRecyclerView(messageID);

        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newMessage = message.getText().toString().trim();
                String senderName = userName;

                if (!newMessage.equals("")) {
                    sendMessage(messageID, senderName, newMessage);
                    message.setText("");
                    adapter.startListening();
                }
            }
        });
    }

    private void sendMessage (String msgId, String senderName, String message) {

        DocumentReference documentReference = db.collection("chats").document();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String timestamp = simpleDateFormat.format(calendar.getTime());

        Map<String, Object> chat = new HashMap<>();
        chat.put("msgID", msgId);
        chat.put("sender", userId);
        chat.put("senderName", senderName);
        chat.put("message", message);
        chat.put("timestamp", timestamp);

        documentReference.set(chat).addOnSuccessListener((OnSuccessListener) (aVoid) -> {

            Log.d(TAG, "onSuccess: Nachricht erfolgreich gespeichert!");
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });

        //1.Kontakt aktualisieren für MessageAdapter
        DocumentReference documentReference1 = db.collection("chats").document(msgId);
        Map<String, Object> chat1 = new HashMap<>();

        documentReference1.update(chat1);
        chat1.put("lastMessage", message);
        chat1.put("lastTimestamp", timestamp);

        documentReference1.update(chat1).addOnSuccessListener((OnSuccessListener) (aVoid) -> {
            Log.d(TAG, "onSuccess: erste Nachricht erfolgreich aktualisiert für MessageAdapter!");
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });

    }

    private void setUpRecyclerView(String msgId) {
        Query query = chatRef.whereEqualTo("msgID", msgId).orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message.class).build();

        adapter = new ChatAdapter1(options);

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

    public void ClickBack(View view) {
        startActivity(new Intent(getApplicationContext(), Messages.class));
    }

}
