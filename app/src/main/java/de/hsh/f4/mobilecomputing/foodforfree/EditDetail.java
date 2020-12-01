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
import android.widget.TextView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import static de.hsh.f4.mobilecomputing.foodforfree.MainActivity.EXTRA_ADID;


public class EditDetail extends AppCompatActivity {
    public static final String EXTRA_ADID = "de.hsh.mobilecomputing.foodforfree.ADID" ;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    TextView title, pickupLocation, description, amount, ingredients, filterOptions;
    ImageView image;
    Button editAd, deleteAd;
    String userId, adId;
    //List <String> list;
    DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_detail);

        title = findViewById(R.id.adDetails_title);
        pickupLocation = findViewById(R.id.adDetails_pickupLocation);
        description = findViewById(R.id.adDetails_description);
        amount = findViewById(R.id.adDetails_amount);
        ingredients = findViewById(R.id.adDetails_ingredients);
        filterOptions = findViewById(R.id.adDetails_filterOptions);
        image = findViewById(R.id.adDetails_image);
        deleteAd =findViewById(R.id.deleteAd);
        editAd =findViewById(R.id.editAd);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        editAd.setOnClickListener(new View.OnClickListener() {
            @Override
              public void onClick(View v) {


                Intent intent = getIntent();

                String adId = intent.getStringExtra(MainActivity.EXTRA_ADID);


                intent = new Intent(EditDetail.this, EditAd.class);
                intent.putExtra(EXTRA_ADID, adId);
                startActivity(intent);


            }

              });

        deleteAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditDetail.this,"Für Löschung Button länger drücken", Toast.LENGTH_SHORT).show();
            }
        });
        deleteAd.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Initialze Alert Dialog
                AlertDialog.Builder builder =new AlertDialog.Builder(EditDetail.this);
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
                            String adId = intent.getStringExtra(EditDetail.EXTRA_ADID);
                            documentReference= fStore.collection("ads").document(adId);
                            StorageReference fileRef = storageReference.child("ads/"+adId+"/adPhoto.jpg");
                            documentReference.delete();
                            fileRef.delete();

                        }catch (Exception e) {
                            Toast.makeText(EditDetail.this, "Anzeige nicht vorhanden", Toast.LENGTH_SHORT).show();


                        }
                        if (documentReference != null) {

                            Toast.makeText(EditDetail.this, "Anzeige wurde gelöscht", Toast.LENGTH_SHORT).show();
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