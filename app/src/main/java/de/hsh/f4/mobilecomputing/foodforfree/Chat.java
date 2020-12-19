package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Chat extends AppCompatActivity {

    //Firestore for recyclerView
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference chatRef = db.collection("chats");

    private ChatAdapter adapter;

    TextView otherUserName;
    ImageButton sendMsgBtn, locationBtn;
    EditText message;
    FirebaseAuth fAuth;
    String userId, userName, offeringUserID, offeringUserName, interestedUserName;

    public static final String TAG = "TAG";

    public LocationManager locationManager;
    public LocationListener locationListener = new Chat.MyLocationListener();
    String lat, lon;     //speichert grade in on locationchanged

    private boolean gps_enable = false;
    private boolean network_enable = false;

    Geocoder geocoder;
    List<Address> myaddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendMsgBtn = findViewById(R.id.send);
        locationBtn = findViewById(R.id.location);
        message = findViewById(R.id.msg);
        otherUserName = findViewById(R.id.otherUserName);

        fAuth = FirebaseAuth.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

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

                if (userId.equals(offeringUserID)) {
                    otherUserName.setText("    " + interestedUserName);
                } else {
                    otherUserName.setText("    " + offeringUserName);
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

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Chat.this, "Um Standort einzufügen Button länger drücken", Toast.LENGTH_SHORT).show();
            }
        });

        locationBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Initialze Alert Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
                //Set title
                builder.setTitle("Standort einfügen");
                //Set message
                builder.setMessage("Willst du deine Standortadresse schicken? \nDu kannst es nach dem Einfügen noch bearbeiten.");
                //positive button
                builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                     getMyLocation();
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
                //startActivity(new Intent(getApplicationContext(),MainActivity.class));
                return false;
            }
        });
    }


    private void sendMessage(String msgId, String senderName, String message) {

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

        adapter = new ChatAdapter(options);

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

    class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            if (location != null) {
                locationManager.removeUpdates(locationListener);
                lat = "" + location.getLatitude();
                lon = "" + location.getLongitude();

                // latitude.setText(lat);
                //longitude.setText(lon);

                geocoder = new Geocoder(Chat.this, Locale.getDefault());
                try {
                    myaddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String address = myaddress.get(0).getAddressLine(0);
                message.setText(address);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }
    }


       /* //berechtnet längen/breitengrad aus angegebenen ort(steil)
        private class GeoHandler extends Handler {
            @Override
            public void handleMessage(@NonNull Message msg) {
                String address;
                switch (msg.what) {
                    case 1:
                        Bundle bundle = msg.getData();
                        address = bundle.getString("address");
                        break;
                    default:
                        address = null;
                }
                message.setText(address);
            }
        }*/


        public void getMyLocation() {
            try {
                gps_enable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {

            }

            try {
                network_enable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {

            }
            if (!gps_enable && !network_enable) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Chat.this);
                builder.setTitle("Achtung");
                builder.setMessage("Die Standortbestimmung ist ausgeschaltet, bitte einschalten...");

                builder.create().show();
            }

            if (gps_enable) {
                if (ActivityCompat.checkSelfPermission(Chat.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Chat.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
            if (network_enable) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        }

        private boolean checkLocationPermission() {
            int location = ContextCompat.checkSelfPermission(Chat.this, Manifest.permission.ACCESS_FINE_LOCATION);
            int location2 = ContextCompat.checkSelfPermission(Chat.this, Manifest.permission.ACCESS_COARSE_LOCATION);

            List<String> listPermission = new ArrayList<>();

            if (location != PackageManager.PERMISSION_GRANTED) {
                listPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (location != PackageManager.PERMISSION_GRANTED) {
                listPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (!listPermission.isEmpty()) {
                ActivityCompat.requestPermissions(Chat.this, listPermission.toArray(new String[listPermission.size()]), 1);
            }
            return true;


        }


};
