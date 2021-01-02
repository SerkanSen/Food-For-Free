//Dieser Code wurde erstellt von Laura Nguyen und Serkan Şen
package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.google.android.material.textfield.TextInputEditText;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlacingAd extends AppCompatActivity  {
    EditText pTitle, pDescription, pIngredients;
    EditText pAmount;
    TextView pPickupLocation;
    CheckBox chBoxVeggie, chBoxVegan, chBoxFruitsVegs, chBoxCans, chBoxMeal, chBoxSweets, chBoxSnacks;
    ArrayList <String> sFilterOptions = new ArrayList<>();
    String [] categories = new String[7];
    Button pPlaceAdBtn, pUploadAdPhotoBtn, pTakePicBtn;
    Calendar calendar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId, adId, currentPhotoPath;
    ImageView pAdPhoto;
    Uri imageUri;
    StorageReference storageReference;
    public static final String TAG = "TAG";
    public static final String DEFAULT_URL = "https://firebasestorage.googleapis.com/v0/b/food-for-free-9663f.appspot.com/o/ads%2FDefault%20Bild.jpg?alt=media&token=57a564e3-006c-4146-b793-cf4346a8f07a";


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
        chBoxSnacks = findViewById(R.id.chBoxSnacks);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        userId = fAuth.getCurrentUser().getUid();

        //Voreinstellung von Abholort = Stadtteil
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
        pTakePicBtn = findViewById(R.id.makePic);

        pUploadAdPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(openGalleryIntent, 1000);
            }
        });
        pTakePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        pPlaceAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //eingegeben Werte einlesen
                String title = pTitle.getText().toString().trim();
                String description = pDescription.getText().toString().trim();
                String ingredients = pIngredients.getText().toString().trim();
                String amount = pAmount.getText().toString().trim();
                String pickupLocation = pPickupLocation.getText().toString().trim();
                String filterOptions;
                List<String> filterCat = Arrays.asList(categories);

                //Zeitstempel erstellen für Sortierung
                calendar = Calendar.getInstance();
                //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timestamp = simpleDateFormat.format(calendar.getTime());

                //Fehlerüberprüfung
                if(TextUtils.isEmpty(title)){
                    pTitle.setError("Titel wird benötigt.");
                    return;
                }
                if(TextUtils.isEmpty(description)){
                    pDescription.setError("Bitte gib eine kurze Beschreibung ein.");
                    return;
                }

                if(TextUtils.isEmpty(ingredients)){
                    pIngredients.setError("Min. eine Zutat wird benötigt. Bitte gib für Allergiker relevante Zutaten unbedingt an!");
                    return;
                }
                if(TextUtils.isEmpty(amount)){
                    pAmount.setError("Bitte gib die Portionen an!");
                    return;
                }

                if(amount.equals("0")){
                    pAmount.setError("Min. eine Portion ist nötig!");
                    return;
                }

                //ArrayList mit den angeklickten Checkboxen als einen String speichern
                StringBuilder stringBuilder = new StringBuilder();
                for (String s : sFilterOptions)
                    stringBuilder.append("- ").append(s).append("\n");
                filterOptions = stringBuilder.toString();

                //userId = fAuth.getCurrentUser().getUid();

                //neues Dokument in Firestore anlegen in der Sammlung "ads/"
                DocumentReference documentReference = fStore.collection("ads").document();
                //Id des Dokumentes adId zuweisen
                adId = documentReference.getId();

                Map<String, Object> ad = new HashMap<>();
                //Angaben in Dokument speichern
                ad.put("title", title);
                ad.put("description", description);
                ad.put("ingredients", ingredients);
                ad.put("amount", amount);
                ad.put("userID", userId);
                ad.put("adID", adId);
                ad.put("timestamp", timestamp);
                ad.put("pickupLocation", pickupLocation);
                ad.put("filterOptions", filterOptions);
                //doppelte Speicherung der Filteroptionen für Query, da es dort nur whereArrayContains gibt
                ad.put("categories", filterCat);
                //Url des Default Bildes wird erst gespeichert
                ad.put("imageUrl", DEFAULT_URL);
                //falls Bild vorhanden: Bild hochladen, imageUrl updaten
                if(imageUri!=null){
                    uploadImageToFirebase(imageUri);
                }
                documentReference.set(ad).addOnSuccessListener((OnSuccessListener) (aVoid) -> {
                    Toast.makeText(PlacingAd.this,"Anzeige erfolgreich erstellt", Toast.LENGTH_SHORT).show();
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
        super.onActivityResult(requestCode, resultCode, data);                      //Bild aus Galerie
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                imageUri = data.getData();
                Picasso.get()
                        .load(imageUri)
                        .fit()
                        .centerCrop()
                        .into(pAdPhoto);
            }
        } else if (requestCode == 61 && resultCode == Activity.RESULT_OK) {         //Bild aus Kameraufnahme
            Picasso.get()
                    .load(imageUri)
                    .fit()
                    .rotate(90)
                    .centerCrop()
                    .into(pAdPhoto);
        }
    }

    //capture Pic and create image File as Uri
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d(TAG, "onSuccess: Fehler bei Pfaderstellung!");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this,
                        "de.hsh.f4.mobilecomputing.foodforfree/files/Pictures",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent,61);
            }
        }
    }


    //hochladen des Bildes in Firebase Storage unter "ads/adId/adPhoto"
    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference fileRef = storageReference.child("ads/"+ adId +"/adPhoto.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .fit()
                                .centerCrop()
                                .into(pAdPhoto);
                        //imageUrl im Dokument speichern
                        DocumentReference documentReference = fStore.collection("ads/").document(adId);
                        documentReference
                                .update("imageUrl", uri.toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "imageUrl successfully saved");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error saving imageUrl", e);
                                    }
                                });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PlacingAd.this, "Bild hochladen fehlgeschlagen.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  //prefix
                ".jpg",   //suffix
                storageDir      //directory
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //Checkboxen: wenn angeklickt, dann füge der ArrayList die entsprechende Kategorie hinzu
    public void selectItem(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()){
            case R.id.chBoxVeggie:
                if(checked){
                    sFilterOptions.add("Vegetarisch");
                    categories[0] = "Vegetarisch";
                }
                else {
                    sFilterOptions.remove("Vegetarisch");
                    categories[0] = "";
                }
                break;
            case R.id.chBoxVegan:
                if(checked){
                    sFilterOptions.add("Vegan");
                    categories[1] = "Vegan";
                }
                else {
                    sFilterOptions.remove("Vegan");
                    categories[1] = "";
                }
                break;
            case R.id.chBoxFruitsVegs:
                if(checked){
                    sFilterOptions.add("Obst/Gemüse");
                    categories[2] = "Obst/Gemüse";
                }
                else {
                    sFilterOptions.remove("Obst/Gemüse");
                    categories[2] = "";
                }
                break;
            case R.id.chBoxCans:
                if(checked){
                    sFilterOptions.add("Konserven");
                    categories[3] = "Konserven";
                }
                else {
                    sFilterOptions.remove("Konserven");
                    categories[3] = "";
                }
                break;
            case R.id.chBoxMeal:
                if(checked){
                    sFilterOptions.add("Gericht");
                    categories[4] = "Gericht";
                }
                else {
                    sFilterOptions.remove("Gericht");
                    categories[4] = "";
                }
                break;
            case R.id.chBoxSweets:
                if(checked){
                    sFilterOptions.add("Süßes");
                    categories[5] = "Süßes";
                }
                else {
                    sFilterOptions.remove("Süßes");
                    categories[5] = "";
                }
                break;
            case R.id.chBoxSnacks:
                if(checked){
                    sFilterOptions.add("Snacks");
                    categories[6] = "Snacks";
                }
                else {
                    sFilterOptions.remove("Snacks");
                    categories[6] = "";
                }
                break;
        }
    }

    public void ClickClose(View view){
        //Initialze Alert Dialog
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        //Set title
        builder.setTitle("Entwurf verwerfen");
        //Set message
        builder.setMessage("Wenn du die Seite verlässt, wird der Entwurf verworfen. Willst du fortfahren?");
        //positive button
        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
}