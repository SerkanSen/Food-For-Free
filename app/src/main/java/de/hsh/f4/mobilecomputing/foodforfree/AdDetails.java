//Dieser Code wurde erstellt von Laura Nguyen und Serkan Şen
package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class AdDetails extends AppCompatActivity {

    protected static final String EXTRA_ADID = "de.hsh.mobilecomputing.foodforfree.ADID";
    protected static final String EXTRA_IMAGEURL = "de.hsh.mobilecomputing.foodforfree.IMAGEURL";
    protected static final String EXTRA_OFF_USERID = "de.hsh.mobilecomputing.foodforfree.OFF_USERID";
    protected static final String EXTRA_LOCATION = "de.hsh.mobilecomputing.foodforfree.LOCATION";

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    TextView title, pickupLocation, description, amount, ingredients, filterOptions;
    String adUserId, userId ;
    String offeringUserID, location;;
    ImageView image;
    ImageButton backBtn, locationBtn;
    Button contactBtn;
    ProgressBar progressBarAdPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_details);

        title = findViewById(R.id.adDetails_title);
        pickupLocation = findViewById(R.id.adDetails_pickupLocation);
        description = findViewById(R.id.adDetails_description);
        amount = findViewById(R.id.adDetails_amount);
        ingredients = findViewById(R.id.adDetails_ingredients);
        filterOptions = findViewById(R.id.adDetails_filterOptions);
        image = findViewById(R.id.adDetails_image);
        progressBarAdPhoto = findViewById(R.id.progressBarAdPhoto);
        contactBtn = findViewById(R.id.contactBtn);
        backBtn = findViewById(R.id.backBtn);
        locationBtn = findViewById(R.id.location);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //get Intent and adId, imageUrl, offeringUserID from clicked itemview
        Intent intent = getIntent();
        String adId = intent.getStringExtra(EXTRA_ADID);
        String imageUrl = intent.getStringExtra(EXTRA_IMAGEURL);
        offeringUserID = intent.getStringExtra(EXTRA_OFF_USERID);

        userId = fAuth.getCurrentUser().getUid();

        DocumentReference documentRef = fStore.collection("ads").document(adId);
        documentRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                adUserId = documentSnapshot.getString("userID");
                if (adUserId.equals(userId)) {
                    Toast.makeText(AdDetails.this, "Deine Anzeige!", Toast.LENGTH_SHORT).show();
                    //contactBtn nur sichtbar, wenn es nicht die eigene Anzeige ist
                    contactBtn.setVisibility(View.INVISIBLE);
                    locationBtn.setVisibility(View.INVISIBLE);
                }
            }
        });

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
                        Toast.makeText(AdDetails.this, "Bild konnte nicht geladen werden.", Toast.LENGTH_SHORT).show();
                    }
                });

        //get document information
        DocumentReference documentReference = fStore.collection("ads").document(adId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                title.setText(documentSnapshot.getString("title"));
                pickupLocation.setText(documentSnapshot.getString("pickupLocation"));
                description.setText(documentSnapshot.getString("description"));
                amount.setText(documentSnapshot.getString("amount"));
                ingredients.setText(documentSnapshot.getString("ingredients"));
                filterOptions.setText(documentSnapshot.getString("filterOptions")); //List zu String
            }
        });

        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdDetails.this, Contact.class);
                intent.putExtra(EXTRA_ADID, adId);
                intent.putExtra(EXTRA_IMAGEURL, imageUrl);
                intent.putExtra(EXTRA_OFF_USERID, offeringUserID);
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdDetails.this, MainActivity.class);
                startActivity(intent);
            }
        });

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdDetails.this, MapsActivity.class);
                location=pickupLocation.getText().toString();
                intent.putExtra(EXTRA_LOCATION, location);
                startActivity(intent)
                ;
            }
        });

    }
}