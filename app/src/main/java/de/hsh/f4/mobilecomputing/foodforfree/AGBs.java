package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class AGBs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_g_bs);
    }

    public void ClickBack (View view) {
        MainActivity.redirectActivity(this, Information.class);
    }
}