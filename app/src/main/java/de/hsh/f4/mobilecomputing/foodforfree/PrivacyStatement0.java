package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class PrivacyStatement0 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_statement);
    }

    public void ClickBack (View view) {
        MainActivity.redirectActivity(this, Register.class);
    }
}