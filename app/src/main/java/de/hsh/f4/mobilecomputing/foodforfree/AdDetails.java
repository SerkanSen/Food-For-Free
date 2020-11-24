package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.List;

public class AdDetails extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    TextView title, pickupLocation, description, amount, ingredients, filterOptions;
    ImageView image;
    //List <String> list;

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

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //get Intent and adId from clicked itemview
        Intent intent = getIntent();
        String adId = intent.getStringExtra(MainActivity.EXTRA_ADID);

        //get Image from storage/ads
        StorageReference adsRef = storageReference.child("ads/"+adId+"/adPhoto.jpg");
        adsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).resize(500,500).onlyScaleDown().into(image);
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
                //(documentSnapshot.getString("filterOptions"));
                filterOptions.setText(documentSnapshot.getString("filterOptions")); //List zu String
            }
        });

    }
}