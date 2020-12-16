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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
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
    ProgressBar progressBarProfileImage;



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
        progressBarProfileImage = findViewById(R.id.progressBarProfileImage);

        profilePhoto = findViewById(R.id.profilePhoto);
        editProfileBtn = findViewById(R.id.updateProfileBtn);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //bestätigen = findViewById(R.id.bestätigen);

        final Profile profile = this;

       // standort = (EditText) findViewById(R.id.standort);
        profileAdress = (TextView) findViewById(R.id.profileAdress);

        userId = fAuth.getCurrentUser().getUid();

        progressBarProfileImage.setVisibility(View.VISIBLE);
        StorageReference profileRef = storageReference.child("users/"+userId+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri)
                        .fit()
                        .centerCrop()
                        .into(profilePhoto, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBarProfileImage.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(Profile.this, "Profilbild konnte nicht geladen werden.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Picasso.get()
                        .load("https://firebasestorage.googleapis.com/v0/b/food-for-free-9663f.appspot.com/o/ads%2FDefault%20Bild.jpg?alt=media&token=57a564e3-006c-4146-b793-cf4346a8f07a")
                        .fit()
                        .centerCrop()
                        .into(profilePhoto, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBarProfileImage.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
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