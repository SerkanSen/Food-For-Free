package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PlacingAd extends AppCompatActivity  {
    //implements AdapterView.OnItemSelectedListener
    EditText pTitle, pDescription, pIngredients;
    EditText pAmount;
    TextInputEditText inputEditBeschreibung;
    //Spinner pAmount;
    //int selectedAmount;
    TextView pPickupLocation;
    CheckBox chBoxVeggie, chBoxVegan, chBoxFruitsVegs, chBoxCans, chBoxMeal, chBoxSweets, chBoxSnacks;
    ArrayList <String> sFilterOptions = new ArrayList<>();
    Button pPlaceAdBtn, pUploadAdPhotoBtn, pMakePic;
    Calendar calendar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId, adId;
    ImageView pAdPhoto;
    Uri imageUri;
    StorageReference storageReference;
    public static final String EXTRA_AMOUNT = "";
    public static final String TAG = "TAG";
    public static final String DEFAULT_URL = "https://firebasestorage.googleapis.com/v0/b/food-for-free-9663f.appspot.com/o/ads%2FDefault%20Bild.jpg?alt=media&token=57a564e3-006c-4146-b793-cf4346a8f07a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placing_ad);

        pTitle = findViewById(R.id.editTitle);
        //inputEditBeschreibung = findViewById(R.id.text_input_des);
        pDescription = findViewById(R.id.editDescription);
        pIngredients = findViewById(R.id.editIngredients);

        pAmount = findViewById(R.id.editAmount);
        /*pAmount = findViewById(R.id.spinnerAmount);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pAmount.setAdapter(adapter);
        pAmount.setOnItemSelectedListener(this);*/

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
        pMakePic = findViewById(R.id.makePic);

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



        pPlaceAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //eingegeben Werte einlesen
                String title = pTitle.getText().toString().trim();
                String description = pDescription.getText().toString().trim();
                String ingredients = pIngredients.getText().toString().trim();
                String amount = pAmount.getText().toString().trim();
                //Intent intent1 = getIntent();
                //String amount = intent1.getStringExtra(PlacingAd.EXTRA_AMOUNT);
                //int amount = selectedAmount;
                String pickupLocation = pPickupLocation.getText().toString().trim();
                String filterOptions;

                //Zeitstempel erstellen
                calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
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
                    pAmount.setError("Bitte die Portionen an!");
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


                userId = fAuth.getCurrentUser().getUid();

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
                ad.put("imageUrl", DEFAULT_URL);
                //falls Bild vorhanden: Bild hochladen
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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                imageUri = data.getData();
                //pAdPhoto.setImageURI(imageUri);
                Picasso.get()
                        .load(imageUri)
                        .fit()
                        .centerCrop()
                        .into(pAdPhoto);
            }
        } else if (requestCode == 61 && resultCode == Activity.RESULT_OK) {          //Übergabe Foto an pAdPhoto
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");                   //habe viele Variationen mit dieser und in kombbination deiner variante drüber probiert, Casting in Uri,direkt als Uri

            pAdPhoto.setImageBitmap(bitmap);
            //imageUri = data.getExtras().get("data");
            //pAdPhoto.setImageURI(imageUri);
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

    //Checkboxen: wenn angeklickt, dann füge der ArrayList die entsprechende Kategorie hinzu
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
                    sFilterOptions.add("Süßes");
                }
                else {
                    sFilterOptions.remove("Süßes");
                }
                break;
            case R.id.chBoxSnacks:
                if(checked){
                    sFilterOptions.add("Snacks");
                }
                else {
                    sFilterOptions.remove("Snacks");
                }
                break;
        }
    }

    /*@Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedAmount = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), selectedAmount, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent (PlacingAd.this, PlacingAd.class);
        intent.putExtra(EXTRA_AMOUNT, selectedAmount);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }*/

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