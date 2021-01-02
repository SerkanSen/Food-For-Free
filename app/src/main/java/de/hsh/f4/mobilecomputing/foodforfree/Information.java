//Dieser Code wurde erstellt von Laura Nguyen
package de.hsh.f4.mobilecomputing.foodforfree;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class Information extends AppCompatActivity {

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        drawerLayout = findViewById(R.id.drawer_layout);
    }

    //AGBs
    public void ClickAGBs (View view) {
        MainActivity.redirectActivity(this, AGBs.class);
    }

    //Datenschutzerklärung
    public void ClickPrivacyStatement (View view) {
        MainActivity.redirectActivity(this, PrivacyStatement.class);
    }

    //Navigationsmenü
    public void ClickMenu(View view) {
        //Open Drawer
        MainActivity.openDrawer(drawerLayout);
    }

    public void ClickLogo(View view) {
        //Close Drawer
        MainActivity.closeDrawer(drawerLayout);
    }

    public void ClickHome(View view) {
        //Redirect activity to home
        MainActivity.redirectActivity(this, MainActivity.class);
    }

    public void ClickProfile(View view) {
        //Redirect activity to Profile
        MainActivity.redirectActivity(this, Profile.class);
    }

    public void ClickAds(View view) {
        //Redirect activity to MyAds
        MainActivity.redirectActivity(this, MyAds.class);
    }

    public void ClickMessages(View view) {
        //Redirect activity to Messages
        MainActivity.redirectActivity(this, Messages.class);
    }

    public void ClickInfo(View view) {
        //Recreate activity
        recreate();
    }

    public void ClickLogout(View view) {
        //logout
        logout(this);
    }

    public void logout(Activity activity) {
        //Initialze Alert Dialog
        AlertDialog.Builder builder =new AlertDialog.Builder(activity);
        //Set title
        builder.setTitle("Abmelden");
        //Set message
        builder.setMessage("Bist Du sicher, dass Du Dich abmelden willst?");
        //positive button
        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
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

    @Override
    protected void onPause(){
        super.onPause();
        //close drawer
        MainActivity.closeDrawer(drawerLayout);
    }
}