//Dieser Code wurde erstellt von Laura Nguyen und Serkan Şen
package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Contact extends AppCompatActivity {

    TextView title;
    Button sendBtn;
    EditText message;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String adTitle, interestedUserID, interestedUserName, offeringUserName , msgId, adId, imageUrl, offeringUserID;
    String [] participants = new String[2];
    Calendar calendar;
    public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        title = findViewById(R.id.adTitle);
        message = findViewById(R.id.editMessage);
        sendBtn = findViewById(R.id.sendBtn);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        interestedUserID = fAuth.getCurrentUser().getUid(); //sender

        //get Intent and adId from clicked itemview
        Intent intent = getIntent();
        adId = intent.getStringExtra(AdDetails.EXTRA_ADID);
        imageUrl = intent.getStringExtra(AdDetails.EXTRA_IMAGEURL);
        offeringUserID = intent.getStringExtra(AdDetails.EXTRA_OFF_USERID);

        DocumentReference adRef = fStore.collection("ads").document(adId);
        adRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                adTitle = documentSnapshot.getString("title");
                title.setText(documentSnapshot.getString("title"));
            }
        });

        DocumentReference interestedUserRef = fStore.collection("users").document(interestedUserID);
        interestedUserRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                interestedUserName = documentSnapshot.getString("name");
            }
        });

        DocumentReference offeringUserRef = fStore.collection("users").document(offeringUserID);
        offeringUserRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                offeringUserName = documentSnapshot.getString("name");
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstMessage = message.getText().toString(); //message
                if (!firstMessage.equals("")) {
                    sendMessage(interestedUserID, interestedUserName, offeringUserID, offeringUserName, adId, firstMessage, imageUrl);
                    Toast.makeText(Contact.this, "Nachricht erfolgreich gesendet! \nLass uns das Essen retten!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Contact.this, "Wir sollten niemanden mit leeren Nachrichten belästigen.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendMessage(String interestedUserID, String interestedUserName, String offeringUserID, String offeringUserName,
                             String adId, String message, String imageUrl) {

        participants[0] = interestedUserID;
        participants[1] = offeringUserID;

        DocumentReference documentReference = fStore.collection("chats").document();

        calendar = Calendar.getInstance();
        //Für die Sortierung
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = simpleDateFormat.format(calendar.getTime());
        //Für die Anzeige der Zeit
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String messageTime = simpleDateFormat1.format(calendar.getTime());


        msgId = documentReference.getId();
        Map<String, Object> chat = new HashMap<>();
        chat.put("adID", adId);
        chat.put("adTitle", adTitle);
        chat.put("imageUrl", imageUrl);
        chat.put("interestedUserID", interestedUserID);
        chat.put("sender", interestedUserID);
        chat.put("senderName", interestedUserName);
        chat.put("interestedUser", interestedUserName);
        chat.put("message", message);
        chat.put("lastMessage", message);
        chat.put("msgID", msgId);
        chat.put("offeringUserID", offeringUserID);
        chat.put("offeringUser", offeringUserName);
        chat.put("timestamp", timestamp);
        chat.put("lastTimestamp", messageTime);
        chat.put("messageTime", messageTime);
        chat.put("participants", Arrays.asList(participants));

        documentReference.set(chat).addOnSuccessListener((OnSuccessListener) (aVoid) -> {
            Log.d(TAG, "onSuccess: Nachricht erfolgreich gesendet!");
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });
        Intent intent = new Intent(Contact.this, AdDetails.class);
        intent.putExtra(AdDetails.EXTRA_ADID, adId);
        intent.putExtra(AdDetails.EXTRA_IMAGEURL, imageUrl);
        intent.putExtra(AdDetails.EXTRA_OFF_USERID, offeringUserID);
        startActivity(intent);
    }

    public void ClickClose(View view){
        //Initialze Alert Dialog
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        //Set title
        builder.setTitle("Entwurf verwerfen");
        //Set message
        builder.setMessage("Wenn du die Seite verlässt, wird der Entwurf verworfen. Willst du fortfahren?");
        //positive button
        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //startActivity(new Intent(getApplicationContext(), AdDetails.class));
                Intent intent = new Intent(Contact.this, AdDetails.class);
                intent.putExtra(AdDetails.EXTRA_ADID, adId);
                intent.putExtra(AdDetails.EXTRA_IMAGEURL, imageUrl);
                intent.putExtra(AdDetails.EXTRA_OFF_USERID, offeringUserID);
                startActivity(intent);
            }
        });
        //negative button
        builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}