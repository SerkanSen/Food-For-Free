package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {
    TextView pName, pEmail, tStadtteil, tName, tEmail;
    EditText pStadtteil;
    ImageView profilePhoto;
    Button uploadProfilePhotoBtn, updateProfileBtn;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    Uri imageUri;
    public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        pName = findViewById(R.id.profileName);
        pEmail = findViewById(R.id.profileMail);
        pStadtteil = findViewById(R.id.profileStadtteil);
        //tName = findViewById(R.id.tName);
        //tEmail = findViewById(R.id.tEmail);
        //tStadtteil = findViewById(R.id.tStadtteil);
        profilePhoto = findViewById(R.id.profilePhoto);
        uploadProfilePhotoBtn = findViewById(R.id.uploadProfilePhotoBtn);
        updateProfileBtn = findViewById(R.id.updateProfileBtn);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        userId = fAuth.getCurrentUser().getUid();

        //bisheriges Profilbild darstellen
        StorageReference adRef = storageReference.child("users/"+userId+"/profile.jpg");
        adRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri)
                        .fit()
                        .centerCrop()
                        .into(profilePhoto);
            }
        });

        //bisherigen Stadtteil darstellen
        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                pName.setText(documentSnapshot.getString("name"));
                pEmail.setText(documentSnapshot.getString("email"));
                pStadtteil.setText(documentSnapshot.getString("stadtteil"));
            }
        });

        uploadProfilePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri != null) {
                    uploadImageToFirebase(imageUri);
                }

                String stadtteil = pStadtteil.getText().toString().trim();

                if(stadtteil.equals("")) {
                    pStadtteil.setError("Stadtteil wird benötigt.");
                    return;
                }else{
                    DocumentReference documentReference = fStore.collection("users").document(userId);
                    documentReference
                            .update("stadtteil", stadtteil)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(EditProfile.this, "Profil erfolgreich aktualisiert!", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                }
                            });
                    startActivity(new Intent(getApplicationContext(), Profile.class));
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000) {
            //wenn Bild ausgewählt
            if(resultCode == Activity.RESULT_OK){
                imageUri = data.getData();
                Picasso.get()
                        .load(imageUri)
                        .fit()
                        .centerCrop()
                        .into(profilePhoto);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        //upload image to firebase storage
        StorageReference fileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
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
                                .into(profilePhoto);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfile.this, "Fehlgeschlagen.", Toast.LENGTH_SHORT).show();
            }
        });
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
                startActivity(new Intent(getApplicationContext(), Profile.class));
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