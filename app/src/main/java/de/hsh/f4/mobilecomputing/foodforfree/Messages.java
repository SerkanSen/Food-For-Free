package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;

public class Messages extends AppCompatActivity {

    DrawerLayout drawerLayout;
    String userId;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    public static final String EXTRA_MSGID = "de.hsh.mobilecomputing.foodforfree.EXTRA_MSGID";

    //Firestore for recyclerView
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference chatRef = db.collection("chats");

    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        drawerLayout = findViewById(R.id.drawer_layout);

        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        fStore = FirebaseFirestore.getInstance();

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        //in participants stehen Sender und Empfänger drin
        Query query = chatRef.whereArrayContains("participants", userId).orderBy("lastTimestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message.class).build();

        adapter = new MessageAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_messages);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MessageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Intent intent = new Intent(Messages.this, Chat.class);
                //übergeben der msgID
                String msgId = documentSnapshot.getString("msgID");
                intent.putExtra(EXTRA_MSGID, msgId);
                startActivity(intent);
            }
        });

        adapter.setOnItemLongClickListener(new MessageAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(DocumentSnapshot snapshot, int position) {
                //Initialze Alert Dialog
                AlertDialog.Builder builder =new AlertDialog.Builder(Messages.this);
                //Set title
                builder.setTitle("Konversation beenden");
                //Set message
                builder.setMessage("Bist Du sicher, dass Du die Konservation beenden und löschen willst?");
                //positive button
                builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            //Löschen der Startnachricht und damit Zugriff auf CHat
                            String msgId = snapshot.getString("msgID");
                            DocumentReference documentReference = fStore.collection("chats").document(msgId);
                            documentReference.delete();
                        }catch (Exception e) {
                            Toast.makeText(Messages.this, "Konservation nicht vorhanden", Toast.LENGTH_SHORT).show();
                        }
                        adapter.startListening();
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
        });

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

 
    //Navigationsmenü
    public void ClickMenu(View view) {
        //Open Drawer
        MainActivity.openDrawer(drawerLayout);
    }

    public void ClickLogo(View view) {
        //Close Drawer
        MainActivity.closeDrawer(drawerLayout);
    }

    public void ClickHome(View view) {
        //Redirect activity to home
        MainActivity.redirectActivity(this, MainActivity.class);
    }

    public void ClickProfile(View view) {
        //Redirect activity to Profile
        MainActivity.redirectActivity(this, Profile.class);
    }

    public void ClickAds(View view) {
        //Redirect activity to MyAds
        MainActivity.redirectActivity(this, MyAds.class);
    }

    public void ClickMessages(View view) {
        //Recreate activity
        recreate();
    }

    public void ClickInfo(View view) {
        //Redirect activity to Information
        MainActivity.redirectActivity(this, Information.class);
    }

    public void ClickLogout(View view) {
        //logout
        logout(this);
    }

    public void logout(Activity activity) {
        //Initialze Alert Dialog
        AlertDialog.Builder builder =new AlertDialog.Builder(activity);
        //Set title
        builder.setTitle("Abmelden");
        //Set message
        builder.setMessage("Bist Du sicher, dass Du Dich abmelden willst?");
        //positive button
        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
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

    @Override
    protected void onPause(){
        super.onPause();
        //close drawer
        MainActivity.closeDrawer(drawerLayout);
    }
}