package de.hsh.f4.mobilecomputing.foodforfree;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity {
    EditText mName,mEMail,mPasswort,mPasswort1;
    Button mRegistrierenBtn;
    TextView mLoginBtn;
    FirebaseAuth fireAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;
    public static final String TAG = "TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mName =findViewById(R.id.editName);
        mEMail=findViewById(R.id.email);
        mPasswort=findViewById(R.id.passwort);
        mPasswort1=findViewById(R.id.editPasswortKontrolle);
        mRegistrierenBtn =findViewById(R.id.registrierenBtn);
        mLoginBtn= findViewById(R.id.anmeldenTextView);

        fireAuth =FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        if(fireAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
        mRegistrierenBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String email = mEMail.getText().toString().trim();
                String passwort= mPasswort.getText().toString().trim();
                String passwort1= mPasswort1.getText().toString().trim();
                String name = mName.getText().toString();

                if(TextUtils.isEmpty(email)){
                    mEMail.setError("Email wird benötigt.");
                    return;
                }
                if(TextUtils.isEmpty(passwort)){
                    mPasswort.setError("Passwort wird benötigt.");
                    return;
                }
                if (passwort.length() < 6) {
                    mPasswort.setError("Passwort muss 6 Zeichen lang sein");
                    return;
                }

                if (!passwort.equals(passwort1)){
                    mPasswort1.setError("Die Passwörter stimmen nicht überein.");
                    return;
                }

                try {
                    progressBar.setVisibility(View.VISIBLE);
                }catch(NullPointerException e){
                    System.out.print(" Ein Fehler ist aufgetreten");
                }

                fireAuth.createUserWithEmailAndPassword(email,passwort).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this, "Neues Konto wurde erstellt.", Toast.LENGTH_SHORT).show();
                            userID = fireAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", name);
                            user.put("email", email);
                            documentReference.set(user).addOnSuccessListener((OnSuccessListener) (aVoid) -> {
                                Log.d(TAG, "onSuccess: user Profile is created for " + userID);
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else{
                            Toast.makeText(Register.this, "Fehler!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

            }

        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });


    }
}