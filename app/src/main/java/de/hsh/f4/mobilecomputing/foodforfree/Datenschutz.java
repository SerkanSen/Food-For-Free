package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Datenschutz extends AppCompatActivity {
    TextView dDatenschutz, dDatenschutzText, dStatus;
    Button zustimmenBtn;
    FirebaseUser fuser;
    FirebaseAuth fireAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datenschutz);


        dDatenschutz=findViewById(R.id.datenschutz);
        dStatus=findViewById(R.id.status);
        dDatenschutzText=findViewById(R.id.datenschutzText);
        zustimmenBtn =findViewById(R.id.bestätigen);

        fireAuth= fireAuth.getInstance();
        fuser= fireAuth.getCurrentUser();
        boolean emailVerify = fuser.isEmailVerified();


        if(!emailVerify){
            dStatus.setText("Email noch nicht verifiziert. Verifizieren gleich nochmal probieren");
            dStatus.setTextColor(Color.parseColor("red"));
        }else{
            dStatus.setText("Email verifiziert, Anmelden nun möglich");
            //dStatus.setTextColor(Color.parseColor("lightgreen"));
        }

        zustimmenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    if (!emailVerify) {
                        Toast.makeText(Datenschutz.this, "Email verifizieren,kurz warten und  nochmal probieren", Toast.LENGTH_SHORT).show();
                        fuser.reload();
                        finish();
                        startActivity(getIntent());

                    } else {startActivity(new Intent(Datenschutz.this, MainActivity.class));
                        Toast.makeText(Datenschutz.this, "Account angelegt", Toast.LENGTH_SHORT).show();

                    }

            }
        });



    }

}