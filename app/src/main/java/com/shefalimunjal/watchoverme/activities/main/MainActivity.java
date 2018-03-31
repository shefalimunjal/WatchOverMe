package com.shefalimunjal.watchoverme.activities.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.shefalimunjal.watchoverme.R;
import com.shefalimunjal.watchoverme.activities.sos.AlertButtonActivity;
import com.shefalimunjal.watchoverme.activities.tutorial.TutorialActivity;
import com.shefalimunjal.watchoverme.utils.Preferences;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE = 1001;
    private static final String TAG = MainActivity.class.getSimpleName();

    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Preferences.getInstance(getApplicationContext()).hasUserSeenTutorial()) {
            goToAlertButton();
        } else {
            goToTutorial();
        }
    }

    private void goToTutorial() {
        Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToAlertButton() {
        Intent intent = new Intent(this, AlertButtonActivity.class);
        startActivity(intent);
        finish();
    }
}
