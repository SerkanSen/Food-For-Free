package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlacingAd extends AppCompatActivity {
    EditText pTitle, pDescription, pIngredients, pAmount;
    TextView pPickupLocation;
    CheckBox chBoxVeggie, chBoxVegan, chBoxFruitsVegs, chBoxCans, chBoxMeal, chBoxSweets;
    //Boolean veggie, vegan, fruitsvegs, cans, meal, sweets;
    //ArrayList<String> pFilterOptions = new ArrayList<>();
    String [] pFilterOptions;
    //int n;
    Button pPlaceAdBtn, pUploadAdPhotoBtn;
    Calendar calendar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId, adId;
    ImageView pAdPhoto;
    Uri imageUri;
    StorageReference storageReference;
    public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placing_ad);

        //n = 0;
        pTitle = findViewById(R.id.editTitle);
        pDescription = findViewById(R.id.editDescription);
        pIngredients = findViewById(R.id.editIngredients);
        pAmount = findViewById(R.id.editAmount);
        pPickupLocation = findViewById(R.id.pickupLocation);
        chBoxVeggie = findViewById(R.id.chBoxVeggie);
        chBoxVegan = findViewById(R.id.chBoxVegan);
        chBoxFruitsVegs = findViewById(R.id.chBoxFruitsVegs);
        chBoxCans = findViewById(R.id.chBoxCans);
        chBoxMeal = findViewById(R.id.chBoxMeal);
        chBoxSweets = findViewById(R.id.chBoxSweets);
        pFilterOptions = new String [6];

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        userId = fAuth.getCurrentUser().getUid();

        DocumentReference userRef = fStore.collection("users").document(userId);
        userRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                pPickupLocation.setText(documentSnapshot.getString("stadtteil"));
            }
        });

        pPlaceAdBtn = findViewById(R.id.placeAdBtn);
        pAdPhoto = findViewById(R.id.adPhoto);
        pUploadAdPhotoBtn = findViewById(R.id.uploadAdPhotoBtn);

        pUploadAdPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        pPlaceAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = pTitle.getText().toString();
                String description = pDescription.getText().toString().trim();
                String ingredients = pIngredients.getText().toString().trim();
                String amount = pAmount.getText().toString();
                String pickupLocation = pPickupLocation.getText().toString();
                //n = pFilterOptions.length;
                List<String> filterOptions = Arrays.asList(pFilterOptions);

                calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
                String timestamp = simpleDateFormat.format(calendar.getTime());

                if(TextUtils.isEmpty(title)){
                    pTitle.setError("Titel wird benötigt.");
                    return;
                }
                if(TextUtils.isEmpty(description)){
                    pDescription.setError("Bitte gib eine kurze Beschreibung ein.");
                    return;
                }
                if(TextUtils.isEmpty(amount)){
                    pAmount.setError("Bitte gib die Portion/en an.");
                    return;
                }
                if(amount.equals("0")){
                    pAmount.setError("Min. eine Portion (1)");
                    return;
                }
                if(TextUtils.isEmpty(ingredients)){
                    pIngredients.setError("Min. eine Zutat wird benötigt. Bitte gib für Allergiker relevante Zutaten unbedingt an!");
                    return;
                }

                userId = fAuth.getCurrentUser().getUid();

                DocumentReference documentReference = fStore.collection("ads").document();
                adId = documentReference.getId();
                Map<String, Object> ad = new HashMap<>();
                ad.put("title", title);
                ad.put("description", description);
                ad.put("ingredients", ingredients);
                ad.put("amount", amount);
                ad.put("userID", userId);
                ad.put("adID", adId);
                ad.put("timestamp", timestamp);
                ad.put("pickupLocation", pickupLocation);
                ad.put("filterOptions", filterOptions);
                //ad.put("n", n);
                if(imageUri!=null){
                    uploadImageToFirebase(imageUri);
                }
                documentReference.set(ad).addOnSuccessListener((OnSuccessListener) (aVoid) -> {
                    Toast.makeText(PlacingAd.this,"Anzeige erfolgreich erstellt", Toast.LENGTH_SHORT).show();
                    //Log.d(TAG, "onSuccess: Anzeige erfolgreich erstellt!");
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000) {
            if(resultCode == Activity.RESULT_OK){
                imageUri = data.getData();
                pAdPhoto.setImageURI(imageUri);

                //uploadImageToFirebase(imageUri);
            }
        }
    }

    //uploads image to Firebase Storage "ads/adId/adPhoto"
    private void uploadImageToFirebase(Uri imageUri) {
        //upload image to firebase storage
        StorageReference fileRef = storageReference.child("ads/"+ adId +"/adPhoto.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(PlacingAd.this, "Foto erfolgreich hochgeladen.", Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).resize(200,200).into(pAdPhoto);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PlacingAd.this, "Fehlgeschlagen.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //include checkboxes
    public void selectItem(View view) {
        boolean checked = ((CheckBox) view).isChecked();
            switch (view.getId()){
                case R.id.chBoxVeggie:
                    if(checked){
                        pFilterOptions[0] = ("Vegetarisch");
                    }
                    else {
                        pFilterOptions[0] = ("");
                    }
                    break;
                case R.id.chBoxVegan:
                    if(checked){
                        pFilterOptions[1] = ("Vegan");
                    }
                    else {
                        pFilterOptions[1] = ("");
                    }
                    break;
                case R.id.chBoxFruitsVegs:
                    if(checked){
                        pFilterOptions[2] = ("Obst/Gemüse");
                    }
                    else {
                        pFilterOptions[2] = ("");
                    }
                    break;
                case R.id.chBoxCans:
                    if(checked){
                        pFilterOptions[3] = ("Konserven");
                    }
                    else {
                        pFilterOptions[3] = ("");
                    }
                    break;
                case R.id.chBoxMeal:
                    if(checked){
                        pFilterOptions[4] = ("Gericht");
                    }
                    else {
                        pFilterOptions[4] = ("");
                    }
                    break;
                case R.id.chBoxSweets:
                    if(checked){
                        pFilterOptions[5] = ("Knabberzeug");
                    }
                    else {
                        pFilterOptions[5] = ("");
                    }
                    break;
            }
    }
}