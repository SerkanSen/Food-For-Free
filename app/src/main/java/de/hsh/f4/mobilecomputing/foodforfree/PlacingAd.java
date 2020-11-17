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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class PlacingAd extends AppCompatActivity {
    EditText pTitle, pDescription, pIngredient1, pIngredient2, pIngredient3, pIngredient4, pAmount;
    CheckBox veggie, vegan, fruitsveggie, cans, meal, sweets;
    Button pPlaceAdBtn, pUploadAdPhotoBtn;
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

        pTitle = findViewById(R.id.editTitle);
        pDescription = findViewById(R.id.editDescription);
        pIngredient1 = findViewById(R.id.editIngredient1);
        pIngredient2 = findViewById(R.id.editIngredient2);
        pIngredient3 = findViewById(R.id.editIngredient3);
        pIngredient4 = findViewById(R.id.editIngredient4);
        pAmount = findViewById(R.id.editAmount);
        veggie = findViewById(R.id.chBoxVeggie);
        vegan = findViewById(R.id.chBoxVegan);
        fruitsveggie = findViewById(R.id.chBoxFruitsVegs);
        cans = findViewById(R.id.chBoxCans);
        meal = findViewById(R.id.chBoxMeal);
        sweets = findViewById(R.id.chBoxSweets);

        pPlaceAdBtn = findViewById(R.id.placeAdBtn);
        pAdPhoto = findViewById(R.id.adPhoto);
        pUploadAdPhotoBtn = findViewById(R.id.uploadAdPhotoBtn);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

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
                String description = pDescription.getText().toString();
                String ingredient1 = pIngredient1.getText().toString();
                String ingredient2 = pIngredient2.getText().toString();
                String ingredient3 = pIngredient3.getText().toString();
                String ingredient4 = pIngredient4.getText().toString();
                String amount = pAmount.getText().toString();


                if(TextUtils.isEmpty(title)){
                    pTitle.setError("Titel wird benötigt.");
                    return;
                }
                if(TextUtils.isEmpty(ingredient1)){
                    pIngredient1.setError("Min. eine Zutat wird benötigt.");
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
                userId = fAuth.getCurrentUser().getUid();

                DocumentReference documentReference = fStore.collection("ads").document();
                adId = documentReference.getId();
                Map<String, Object> ad = new HashMap<>();
                ad.put("title", title);
                ad.put("description", description);
                ad.put("ingredient1", ingredient1);
                if(ingredient2!= null)
                    ad.put("ingredient2", ingredient2);
                if(ingredient3!= null)
                    ad.put("ingredient3", ingredient3);
                if(ingredient4!= null)
                    ad.put("ingredient4", ingredient4);
                ad.put("amount", amount);
                ad.put("userID", userId);
                ad.put("adID", adId);
                if(imageUri!=null){
                    uploadImageToFirebase(imageUri);
                }
                documentReference.set(ad).addOnSuccessListener((OnSuccessListener) (aVoid) -> {
                    Log.d(TAG, "onSuccess: Anzeige erfolgreich erstellt!");
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

    private void uploadImageToFirebase(Uri imageUri) {
        //upload image to firebase storage
        StorageReference fileRef = storageReference.child("ads/"+ adId +"/adPhoto.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(PlacingAd.this, "Foto erfolgreich hochgeladen.", Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(pAdPhoto);
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

    public void selectItem(View view) {
        boolean checked = ((CheckBox) view).isChecked();
            switch (view.getId()){
                case R.id.chBoxVeggie:
                    if(checked){

                    }
            }
    }


}