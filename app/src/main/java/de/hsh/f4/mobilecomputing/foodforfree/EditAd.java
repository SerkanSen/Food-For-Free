package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditAd extends AppCompatActivity {
    private static final String EXTRA_ADID = "de.hsh.mobilecomputing.foodforfree.ADID";
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    ArrayList<String> sFilterOptions = new ArrayList<>();
    Button pUpdateAdBtn, pUploadAdPhotoBtn, pMakePic;
    Calendar calendar;
    String userId, adId, imageAdPhotoUrl;
    ImageView pAdPhoto;
    Uri imageUri;
    public static final String TAG = "TAG";

    EditText pTitle, pDescription, pIngredients, pAmount;
    TextView pPickupLocation;
    CheckBox chBoxVeggie, chBoxVegan, chBoxFruitsVegs, chBoxCans, chBoxMeal, chBoxSweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placing_ad);

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
        pUpdateAdBtn = findViewById(R.id.placeAdBtn);
        pUpdateAdBtn.setText("Anzeige aktualisieren");
        pAdPhoto = findViewById(R.id.adPhoto);
        pUploadAdPhotoBtn = findViewById(R.id.uploadAdPhotoBtn);
        pMakePic = findViewById(R.id.makePic);
/*
        title = findViewById(R.id.adDetails_title);
        pickupLocation = findViewById(R.id.adDetails_pickupLocation);
        description = findViewById(R.id.adDetails_description);
        amount = findViewById(R.id.adDetails_amount);
        ingredients = findViewById(R.id.adDetails_ingredients);
        filterOptions = findViewById(R.id.adDetails_filterOptions);*/
        //image = findViewById(R.id.adDetails_image);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userId = fAuth.getCurrentUser().getUid();

        //get Intent and adId from clicked itemview
        Intent intent = getIntent();
        adId = intent.getStringExtra(MyAdsDetails.EXTRA_ADID);

        //Darstellung der bisherigen Informationen:
        //get Image from storage/ads
        StorageReference adsRef = storageReference.child("ads/"+adId+"/adPhoto.jpg");
        adsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
               Picasso.get().load(uri).resize(500,500).onlyScaleDown().into(pAdPhoto);
            }
        });
        //get document information
        DocumentReference documentReference = fStore.collection("ads").document(adId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                pTitle.setText(documentSnapshot.getString("title"));
                pPickupLocation.setText(documentSnapshot.getString("pickupLocation"));
                pDescription.setText(documentSnapshot.getString("description"));
                pAmount.setText(documentSnapshot.getString("amount"));
                pIngredients.setText(documentSnapshot.getString("ingredients"));
                //Checkboxen evtl. inkludieren
            }
        });

        pUploadAdPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });
        pMakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,61);
            }
        });

        pUpdateAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = pTitle.getText().toString();
                String description = pDescription.getText().toString().trim();
                String ingredients = pIngredients.getText().toString().trim();
                String amount = pAmount.getText().toString();
                String pickupLocation = pPickupLocation.getText().toString();
                String filterOptions;

                calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
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

                //filterOptions as String
                StringBuilder stringBuilder = new StringBuilder();
                for (String s : sFilterOptions)
                    stringBuilder.append("- ").append(s).append("\n");
                filterOptions = stringBuilder.toString();

                userId = fAuth.getCurrentUser().getUid();

                if(imageUri!=null){
                    uploadImageToFirebase(imageUri);
                }

                //überschreiben/updaten des bisherigen Dokumentes über die übergebene adId
                DocumentReference documentReference = fStore.collection("ads").document(adId);
                Map<String, Object> ad = new HashMap<>();

                documentReference.set(ad);
                ad.put("adID", adId);
                ad.put("userID", userId);
                ad.put("title", title);
                ad.put("description", description);
                ad.put("ingredients", ingredients);
                ad.put("amount", amount);
                ad.put("timestamp", timestamp);
                ad.put("pickupLocation", pickupLocation);
                ad.put("filterOptions", filterOptions);
                ad.put("imageUrl", imageAdPhotoUrl);
                documentReference.set(ad).addOnSuccessListener((OnSuccessListener) (aVoid) -> {
                    Toast.makeText(EditAd.this,"Anzeige aktualisiert", Toast.LENGTH_SHORT).show();
                    //Log.d(TAG, "onSuccess: Anzeige erfolgreich erstellt!");
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
                startActivity(new Intent(getApplicationContext(), MyAds.class));

            }

        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                imageUri = data.getData();
                pAdPhoto.setImageURI(imageUri);
            }
        } else if (requestCode == 61 && resultCode == Activity.RESULT_OK) {          //Übergabe Foto an pAdPhoto
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");                   //habe viele Variationen mit dieser und in kombbination deiner variante drüber probiert, Casting in Uri,direkt als Uri

            pAdPhoto.setImageBitmap(bitmap);
            //imageUri = data.getExtras().get("data");
            //pAdPhoto.setImageURI(imageUri);
        }
    }

    public void onBackPressed(){
        return;
    }



    //uploads image to Firebase Storage "ads/adId/adPhoto"
    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference fileRef = storageReference.child("ads/"+ adId +"/adPhoto.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).resize(200,200).into(pAdPhoto);
                        imageAdPhotoUrl = fileRef.getDownloadUrl().toString();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditAd.this, "Bild hochladen fehlgeschlagen.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //include checkboxes
    public void selectItem(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()){
            case R.id.chBoxVeggie:
                if(checked){
                    sFilterOptions.add("Vegetarisch");
                }
                else {
                    sFilterOptions.remove("Vegetarisch");
                }
                break;
            case R.id.chBoxVegan:
                if(checked){
                    sFilterOptions.add("Vegan");
                }
                else {
                    sFilterOptions.remove("Vegan");
                }
                break;
            case R.id.chBoxFruitsVegs:
                if(checked){
                    sFilterOptions.add("Obst/Gemüse");
                }
                else {
                    sFilterOptions.remove("Obst/Gemüse");
                }
                break;
            case R.id.chBoxCans:
                if(checked){
                    sFilterOptions.add("Konserven");
                }
                else {
                    sFilterOptions.remove("Konserven");
                }
                break;
            case R.id.chBoxMeal:
                if(checked){
                    sFilterOptions.add("Gericht");
                }
                else {
                    sFilterOptions.remove("Gericht");
                }
                break;
            case R.id.chBoxSweets:
                if(checked){
                    sFilterOptions.add("Knabberzeug");
                }
                else {
                    sFilterOptions.remove("Knabberzeug");
                }
                break;
        }
    }
}