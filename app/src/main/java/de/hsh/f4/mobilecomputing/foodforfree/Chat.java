package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Chat extends AppCompatActivity {

    TextView title;
    Button send;
    EditText msg;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId, adId, msgId, imageAdPhotoUrl, timestamp;
    Uri imageUri;
    Calendar calendar;
    public static final String TAG = "TAG";
    public static final String EXTRA_ADID = "de.hsh.mobilecomputing.foodforfree.ADID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        title = findViewById(R.id.title);
        msg = findViewById(R.id.msg);
        send = findViewById(R.id.send);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid(); //sender

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = msg.getText().toString(); //message
                if (!message.equals("")) {
                    sendMessage(userId, adId, message);
                    Toast.makeText(Chat.this, "Lass uns das Essen retten!",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Chat.this, "Wir sollten niemanden mit leeren Nachrichten belästigen.", Toast.LENGTH_SHORT).show();
                }
                msg.setText("");
            }
        });

        //get Intent and adId from clicked itemview
        Intent intent = getIntent();
        adId = intent.getStringExtra(AdDetails.EXTRA_ADID); //reciever


    }


    private void sendMessage(String sender, String reciever, String message) {
        // StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        //DocumentReference documentReference = fStore.collection("ads").document(adId).collection("Chat").document();
        DocumentReference documentReference = fStore.collection("ads/"+ adId +"/Chat").document();

        calendar = Calendar.getInstance();
        //String timestamp = DateFormat.getDateInstance().format(calendar.getTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String timestamp = simpleDateFormat.format(calendar.getTime());

        //StorageReference fileRef = storageReference.child("ads/"+ adId +"/adPhoto.jpg");
        msgId = documentReference.getId();
        Map<String, Object> chat = new HashMap<>();
        chat.put("sender", sender);
        chat.put("reciever", reciever);
        chat.put("message", message);
        chat.put("timestamp", timestamp);
        //storageReference.child("Chat").push().setValue(hashMap);
        //documentReference.child("Chat");
        documentReference.set(chat).addOnSuccessListener((OnSuccessListener) (aVoid) -> {

            //Log.d(TAG, "onSuccess: Anzeige erfolgreich erstellt!");
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
