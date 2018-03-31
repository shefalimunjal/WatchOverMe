package com.shefalimunjal.watchoverme.activities.tutorial;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.shefalimunjal.watchoverme.R;
import com.shefalimunjal.watchoverme.activities.sos.AlertButtonActivity;
import com.shefalimunjal.watchoverme.utils.Preferences;

public class TutorialActivity extends AppIntro{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance(
                "",
                null,
                "Press the button when not safe",
                null,
                R.drawable.red_emergency_button,
                ContextCompat.getColor(this, R.color.light_blue),
                ContextCompat.getColor(this, R.color.white),
                ContextCompat.getColor(this, R.color.white))
        );

        addSlide(AppIntroFragment.newInstance(
                "",
                null,
                "Your friends receive location updates",
                null,
                R.drawable.location,
                ContextCompat.getColor(this, R.color.light_blue),
                ContextCompat.getColor(this, R.color.white),
                ContextCompat.getColor(this, R.color.white))
        );

        addSlide(AppIntroFragment.newInstance(
                "",
                null,
                "Release the button when safe",
                null,
                R.drawable.green_emergency_button,
                ContextCompat.getColor(this, R.color.light_blue),
                ContextCompat.getColor(this, R.color.white),
                ContextCompat.getColor(this, R.color.white))
        );

        addSlide(AppIntroFragment.newInstance(
                "",
                null,
                "Report incidents around you",
                null,
                R.drawable.notify,
                ContextCompat.getColor(this, R.color.light_blue),
                ContextCompat.getColor(this, R.color.white),
                ContextCompat.getColor(this, R.color.white))
        );
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        startAlertButtonActivity();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startAlertButtonActivity();
    }

    private void startAlertButtonActivity() {
        Preferences.getInstance(getApplicationContext()).setUserSeenTutorial(true);

        Intent intent = new Intent(this, AlertButtonActivity.class);
        startActivity(intent);
        finish();
    }
}
