package de.hsh.f4.mobilecomputing.foodforfree;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    EditText mName,mEMail,mPasswort,mPasswort1;
    Button mRegistrierenButton;
    TextView mLoginButton;
    FirebaseAuth fireAuth;
    ProgressBar ladebalken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mName =findViewById(R.id.editName);
        mEMail=findViewById(R.id.editEMail);
        mPasswort=findViewById(R.id.editPassword);
        mPasswort1=findViewById(R.id.editPasswortKontrolle);
        mRegistrierenButton =findViewById(R.id.button);
        mLoginButton= findViewById(R.id.textView4);

        fireAuth =FirebaseAuth.getInstance();
        ladebalken = findViewById(R.id.ladebalken);

        if(fireAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
        mRegistrierenButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String email = mEMail.getText().toString().trim();
                String passwort= mPasswort.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEMail.setError("EMail wird benötigt.");
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
                if (!mPasswort.equals(mPasswort1)){
                    mPasswort.setError("Die Passwörter stimmen nicht überein.");
                     return;
                }
                ladebalken.setVisibility(View.VISIBLE);

                fireAuth.createUserWithEmailAndPassword(email,passwort).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this, "Benutzer wurde erstellt.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else{
                            Toast.makeText(Register.this, "Fehler!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        });


    }
}