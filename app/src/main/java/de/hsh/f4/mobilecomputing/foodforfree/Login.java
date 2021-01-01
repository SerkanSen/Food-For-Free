package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
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

import java.math.BigInteger;

public class Login extends AppCompatActivity {

    EditText mEmail, mPasswort;
    Button mLoginBtn;
    TextView mRegisterBtn, mAktivitaet, mTitel, mChangePasswordBtn;
    ProgressBar progressBar;
    FirebaseAuth fireAuth;

    String email;

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
        mLoginBtn = findViewById(R.id.registrierenBtn);
        mRegisterBtn = findViewById(R.id.registerTextView);
        mRegisterBtn.setPaintFlags(mRegisterBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mChangePasswordBtn= findViewById(R.id.changePasswordTextView);
        mChangePasswordBtn.setPaintFlags(mChangePasswordBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString().trim();
                String passwort= mPasswort.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("EMail wird benötigt.");
                    return;
                }
                if(TextUtils.isEmpty(passwort)){
                    mPasswort.setError("Passwort wird benötigt.");
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

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });

        mChangePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Bitte das Email-Feld ausfüllen für eine Passwortrücksetzung.");
                    return;
                }else {
                    fireAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Login.this,"Für eine Passwortrücksetzung überprüfe deinen Email Account...",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this,Login.class));
                            }else{
                                String message = task.getException().getMessage();
                                Toast.makeText(Login.this,"Es ist ein Fehler aufgetreten"+message,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });
    }
}