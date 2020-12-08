package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
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

import java.util.List;

import static de.hsh.f4.mobilecomputing.foodforfree.MainActivity.EXTRA_ADID;
import static de.hsh.f4.mobilecomputing.foodforfree.MainActivity.EXTRA_IMAGEURL;

public class AdDetails extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    TextView title, pickupLocation, description, amount, ingredients, filterOptions;
    ImageView image;
    ImageButton contact, backBtn;
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
        contact =findViewById(R.id.contact);
        backBtn = findViewById(R.id.backBtn);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //get Intent and adId+imageUrl from clicked itemview
        Intent intent = getIntent();
        String adId = intent.getStringExtra(EXTRA_ADID);
        String imageUrl = intent.getStringExtra(EXTRA_IMAGEURL);

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
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();

                String adId = intent.getStringExtra(MainActivity.EXTRA_ADID);

                intent = new Intent(AdDetails.this, Chat.class);
                intent.putExtra(EXTRA_ADID, adId);
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

    }
}