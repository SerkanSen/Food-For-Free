package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Profile extends AppCompatActivity {
    //Initialize variable
    DrawerLayout drawerLayout;
    TextView name, email, tName, tEmail, tAdresse;
    ImageView profilePhoto;
    Button editProfileBtn;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    Button bestätigen;
    EditText standort;
    TextView profileAdress;

    public LocationManager locationManager;
    public LocationListener locationListener = new MyLocationListener();
    String lat, lon;     //speichert grade in on locationchanged

    private boolean gps_enable = false;
    private boolean network_enable = false;

    Geocoder geocoder;
    List<Address> myaddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Assign variable
        drawerLayout = findViewById(R.id.drawer_layout);
        tName = findViewById(R.id.tName);
        tEmail = findViewById(R.id.tEmail);
        tAdresse = findViewById(R.id.tStadtteil);
        name = findViewById(R.id.profileName);
        email = findViewById(R.id.profileMail);

        profilePhoto = findViewById(R.id.profilePhoto);
        editProfileBtn = findViewById(R.id.updateProfileBtn);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //bestätigen = findViewById(R.id.bestätigen);

        final Profile profile = this;

       // standort = (EditText) findViewById(R.id.standort);
        profileAdress = (TextView) findViewById(R.id.profileAdress);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        userId = fAuth.getCurrentUser().getUid();

        StorageReference profileRef = storageReference.child("users/"+userId+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).resize(100,100).into(profilePhoto);
            }
        });

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                name.setText(documentSnapshot.getString("name"));
                email.setText(documentSnapshot.getString("email"));
                profileAdress.setText(documentSnapshot.getString("stadtteil"));
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profile, EditProfile.class);
                startActivity(intent);
                //startActivity(new Intent(getApplicationContext(),EditProfile.class));
            }
        });

/*  //Button bestätigen der schaut ob eine Ort eingegeben wurde, wenn ja wird das Textfeld gefüll
    //sonst wird die exakte Adresse via GPS ermittelt->wird aber noch nicht gespeichert
        bestätigen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String address = standort.getText().toString();
                if (address.equals("")) {
                    getMyLocation();

                } else {
                    Standort geoLocation = new Standort();
                    geoLocation.getAddress(address, getApplicationContext(), new GeoHandler());
                }

            //    Intent intent = new Intent(profile, MainActivity.class);
              //  startActivity(intent);
            }

            ;


        });
        checkLocationPermission();
*/
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
        //Recreate activity
        recreate();
    }

    public void ClickAds(View view) {
        //Redirect activity to Ads
        MainActivity.redirectActivity(this, MyAds.class);
    }

    public void ClickMessages(View view) {
        //Redirect activity to Messages
        MainActivity.redirectActivity(this, Messages.class);
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
                //activity.finishAffinity();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                //finish();
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

    class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            if (location != null) {
                locationManager.removeUpdates(locationListener);
                lat = "" + location.getLatitude();
                lon = "" + location.getLongitude();

               // latitude.setText(lat);
                //longitude.setText(lon);

                geocoder = new Geocoder(Profile.this, Locale.getDefault());
                try {
                    myaddress=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String address = myaddress.get(0).getAddressLine(0);
                profileAdress.setText(address);
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
/*
//berechtnet längen/breitengrad aus angegebenen ort(steil)
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
            profileAdress.setText(address);
        }
    }
//standortbestimmung via gps und längenbreitengrad angabe
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
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Profile.this);
            builder.setTitle("Achtung");
            builder.setMessage("Die Standortbestimmung ist ausgeschaltet, bitte einschalten...");

            builder.create().show();
        }

        if (gps_enable) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
        if (network_enable) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }
    private boolean checkLocationPermission(){
        int location = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        int location2 = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);

        List<String> listPermission =new ArrayList<>();

        if(location != PackageManager.PERMISSION_GRANTED){
            listPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(location != PackageManager.PERMISSION_GRANTED){
            listPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }if (!listPermission.isEmpty()){
            ActivityCompat.requestPermissions(this, listPermission.toArray(new String  [listPermission.size()]), 1);
        }
        return true;


    }*/


}