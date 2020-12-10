package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

import static androidx.core.view.GravityCompat.*;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //Initialize variable
    protected static final String EXTRA_ADID = "de.hsh.mobilecomputing.foodforfree.ADID";
    protected static final String EXTRA_IMAGEURL = "de.hsh.mobilecomputing.foodforfree.IMAGEURL";
    protected static final String EXTRA_OFF_USERID = "de.hsh.mobilecomputing.foodforfree.OFF_USERID";
    DrawerLayout drawerLayout;
    EditText inputSearch;
    FloatingActionButton newAdBtn;
    FirebaseAuth fAuth;
    String userId;

    //Firestore for recyclerView
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference adRef = db.collection("ads");

    private AdAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign variabe
        drawerLayout = findViewById(R.id.drawer_layout);
        newAdBtn = findViewById(R.id.newAdBtn);
        inputSearch = findViewById(R.id.inputSearch);
        final Button standort= (Button) findViewById(R.id.standort);

        Spinner spinner = findViewById(R.id.spinnerFilter);
        ArrayAdapter<CharSequence> adapterFilter = ArrayAdapter.createFromResource(this, R.array.filterOptions, android.R.layout.simple_spinner_item);
        adapterFilter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterFilter);
        spinner.setOnItemSelectedListener(this);

        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();


        final MainActivity mainActivity = this;

        //newAdBtn -> new activity PlacingAd
        newAdBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, PlacingAd.class);
                startActivity(intent);
            }
        }));

        standort.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, MapsActivity.class);
                startActivity(intent);
            }
        }));

        Query queryAll = adRef.orderBy("timestamp", Query.Direction.DESCENDING);
        setUpRecyclerView(queryAll);

        //Suchleiste für Titel
        /*inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString() != null) {
                    setUpRecyclerView(s.toString());
                } else {
                    setUpRecyclerView("");
                }
            }
        });*/
    }

    private void setUpRecyclerView(Query query) {
        //Query query = adRef.orderBy("timestamp", Query.Direction.DESCENDING);

        //für Filter:
        //Query query = adRef.orderBy("title").orderBy("timestamp", Query.Direction.DESCENDING).startAt(data).endAt(data+"\uf8ff");
        //Query query = adRef.whereEqualTo("title", data).orderBy("timestamp", Query.Direction.DESCENDING).startAt(data).endAt(data+"\uf8ff");

        FirestoreRecyclerOptions<Ad> options = new FirestoreRecyclerOptions.Builder<Ad>().setQuery(query, Ad.class).build();

        adapter = new AdAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AdAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Intent intent = new Intent(MainActivity.this, AdDetails.class);
                String adId = documentSnapshot.getId();
                String imageUrl = documentSnapshot.getString("imageUrl");
                String offeringAdUserID = documentSnapshot.getString("userID");
                //übergeben der adId und imageUrl
                intent.putExtra(EXTRA_ADID, adId);
                intent.putExtra(EXTRA_IMAGEURL, imageUrl);
                intent.putExtra(EXTRA_OFF_USERID, offeringAdUserID);
                startActivity(intent);
            }
        });
    }

    public void onBackPressed(){
        return;
    }

    public void ClickRefresh(View view) {
        adapter.startListening();
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
        //Open drawer
        openDrawer(drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        //Open drawer layout
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void ClickLogo(View view) {
        //close Drawer
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        //close drawer layout
        //Check condition
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            //When drawer is open
            //Close drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void ClickHome(View view){
        //recreate activity
        recreate();
    }

    public void ClickProfile(View view){
        //redirect activity to Profile
        redirectActivity(this,Profile.class);
    }

    public void ClickAds(View view){
        //redirect activity to Ads
        redirectActivity(this,MyAds.class);
    }

    public void ClickMessages(View view) {
        //Redirect activity to MyAds
        redirectActivity(this, Messages.class);
    }

    public void ClickLogout(View view){
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

    public static void redirectActivity(Activity activity, Class aClass) {
        //Initialize intent
        Intent intent = new Intent(activity, aClass);
        //Set Flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //Start activity
        activity.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //close drawer
        closeDrawer(drawerLayout);
    }

    //für Filter
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Query queryCat;
        String catSelected = parent.getItemAtPosition(position).toString();
        //Toast.makeText(MainActivity.this, "Startseite aktualisieren...", Toast.LENGTH_SHORT).show();
        if(catSelected.equals("Alles")) {
            queryCat = adRef.orderBy("timestamp", Query.Direction.DESCENDING);
            //do nothing
        } else {
            queryCat = adRef.whereArrayContains("categories", catSelected).orderBy("timestamp", Query.Direction.DESCENDING);
        }
        setUpRecyclerView(queryCat);
        adapter.startListening();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //query = adRef.orderBy("timestamp", Query.Direction.DESCENDING);
        Query queryAll = adRef.orderBy("timestamp", Query.Direction.DESCENDING);
        setUpRecyclerView(queryAll);
    }
}