package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import static de.hsh.f4.mobilecomputing.foodforfree.MyAds.EXTRA_AD_ID;
import static de.hsh.f4.mobilecomputing.foodforfree.MyAds.EXTRA_IMAGE_URL;


public class MyAdsDetails extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    TextView title, pickupLocation, description, amount, ingredients, filterOptions;
    ImageView image;
    ProgressBar progressBarAdPhoto;
    Button editAd, deleteAd;
    DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads_details);

        title = findViewById(R.id.adDetails_title);
        pickupLocation = findViewById(R.id.adDetails_pickupLocation);
        description = findViewById(R.id.adDetails_description);
        amount = findViewById(R.id.adDetails_amount);
        ingredients = findViewById(R.id.adDetails_ingredients);
        filterOptions = findViewById(R.id.adDetails_filterOptions);
        image = findViewById(R.id.adDetails_image);
        deleteAd =findViewById(R.id.deleteAd);
        editAd =findViewById(R.id.editAd);
        progressBarAdPhoto = findViewById(R.id.progressBarAdPhoto);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //get Intent and adId from clicked itemview
        Intent intent = getIntent();
        String adId = intent.getStringExtra(EXTRA_AD_ID);
        String imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL);

        //load adPhoto from Storage with imageUrl, meanwhile progressBar
        progressBarAdPhoto.setVisibility(View.VISIBLE);
        Picasso.get()
                .load(imageUrl)
                .fit()
                .centerCrop()
                .into(image, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBarAdPhoto.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(MyAdsDetails.this, "Bild konnte nicht geladen werden.", Toast.LENGTH_SHORT).show();
                    }
                });

        //get document information
        documentReference = fStore.collection("ads").document(adId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                title.setText(documentSnapshot.getString("title"));
                pickupLocation.setText(documentSnapshot.getString("pickupLocation"));
                description.setText(documentSnapshot.getString("description"));
                amount.setText(documentSnapshot.getString("amount"));
                ingredients.setText(documentSnapshot.getString("ingredients"));
                //(documentSnapshot.getString("filterOptions"));
                filterOptions.setText(documentSnapshot.getString("filterOptions"));
            }
        });

        editAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getIntent();

                String adId = intent.getStringExtra(MainActivity.EXTRA_ADID);
                String imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL);

                intent = new Intent(MyAdsDetails.this, EditAd.class);
                intent.putExtra(EXTRA_AD_ID, adId);
                intent.putExtra(EXTRA_IMAGE_URL, imageUrl);
                startActivity(intent);
            }

        });

        deleteAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyAdsDetails.this,"Für Löschung Button länger drücken", Toast.LENGTH_SHORT).show();
            }
        });
        deleteAd.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Initialze Alert Dialog
                AlertDialog.Builder builder =new AlertDialog.Builder(MyAdsDetails.this);
                //Set title
                builder.setTitle("Löschung");
                //Set message
                builder.setMessage("Bist Du sicher, dass Du die Anzeige löschen willst?");
                //positive button
                builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //activity.finishAffinity();

                        //userId = fAuth.getCurrentUser().getUid();

                        try {Intent intent = getIntent();
                            //Löschen des Dokuments und des Fotos
                            documentReference = fStore.collection("ads").document(adId);
                            StorageReference fileRef = storageReference.child("ads/"+adId+"/adPhoto.jpg");
                            documentReference.delete();
                            fileRef.delete();
                        }catch (Exception e) {
                            Toast.makeText(MyAdsDetails.this, "Anzeige nicht vorhanden", Toast.LENGTH_SHORT).show();
                        }
                        if (documentReference != null) {
                            Toast.makeText(MyAdsDetails.this, "Anzeige wurde gelöscht", Toast.LENGTH_SHORT).show();
                        }

                        startActivity(new Intent(getApplicationContext(), MyAds.class));
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
}