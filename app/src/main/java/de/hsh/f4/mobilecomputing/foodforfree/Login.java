package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText mEmail, mPasswort;
    Button mLoginBtn;
    TextView mCreateBtn, mAktivitaet, mTitel;
    ProgressBar progressBar;
    FirebaseAuth fireAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTitel = findViewById(R.id.titel);
        mAktivitaet = findViewById(R.id.aktivitaet);
        mEmail = findViewById(R.id.email);
        mPasswort = findViewById(R.id.passwort);
        progressBar = findViewById(R.id.progressBar);
        fireAuth = FirebaseAuth.getInstance();
        mLoginBtn = findViewById(R.id.loginBtn);
        mCreateBtn = (TextView)findViewById(R.id.registerTextView);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString().trim();
                String passwort= mPasswort.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("EMail wird benötigt.");
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

                progressBar.setVisibility(View.VISIBLE);

                fireAuth.signInWithEmailAndPassword(email, passwort).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Erfolgreich angemeldet.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        } else {
                            Toast.makeText(Login.this, "Fehler!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });
    }
}