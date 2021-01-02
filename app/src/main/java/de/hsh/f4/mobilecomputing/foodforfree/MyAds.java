package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MyAds extends AppCompatActivity {
    //Initialize variable
    protected static final String EXTRA_AD_ID = "de.hsh.mobilecomputing.foodforfree.ADID";
    protected static final String EXTRA_IMAGE_URL = "de.hsh.mobilecomputing.foodforfree.IMAGEURL";
    DrawerLayout drawerLayout;
    FirebaseAuth fAuth;
    String userId;

    //Firestore for recyclerView
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference adRef = db.collection("ads");

    private AdAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);

        //assign variable
        drawerLayout = findViewById(R.id.drawer_layout);

        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = adRef.whereEqualTo("userID", userId).orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Ad> options = new FirestoreRecyclerOptions.Builder<Ad>().setQuery(query, Ad.class).build();

        adapter = new AdAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_myAds);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AdAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Intent intent = new Intent(MyAds.this, MyAdsDetails.class);
                String adId = documentSnapshot.getId();
                String imageUrl = documentSnapshot.getString("imageUrl");
                //übergeben der adId+imageUrl
                intent.putExtra(EXTRA_AD_ID, adId);
                intent.putExtra(EXTRA_IMAGE_URL, imageUrl);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
        //Recreate activity
        recreate();
    }

    public void ClickMessages(View view) {
        //Redirect activity to MyAds
        MainActivity.redirectActivity(this, Messages.class);
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