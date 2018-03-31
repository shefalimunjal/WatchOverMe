package com.shefalimunjal.watchoverme.activities.report;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.VolleyError;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shefalimunjal.watchoverme.R;
import com.shefalimunjal.watchoverme.activities.sos.AlertButtonActivity;
import com.shefalimunjal.watchoverme.networking.IncidentUploadRequest;
import com.shefalimunjal.watchoverme.networking.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import static android.content.ContentValues.TAG;

public class SubmitIncidentActivity extends AppCompatActivity {
    private static final int GALLERY_REQUEST_CODE = 1001;
    private static final String TAG = SubmitIncidentActivity.class.getSimpleName();

    private ConstraintLayout constraintLayout;
    private ImageView photoImageView;
    private String mIncidentType;
    private TextView titleTextView;
    private Bitmap bitmap;
    private EditText incidentDescription;
    private TextView submit;
    private CheckBox isLifeThreatening;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_incident);
        initUIElements();
    }

    private void initUIElements() {
        constraintLayout = (ConstraintLayout)findViewById(R.id.parent_layout);
        photoImageView = (ImageView)findViewById(R.id.photo_upload);
        titleTextView = (TextView)findViewById(R.id.title);
        submit = (TextView)findViewById(R.id.submit);
        mIncidentType = getIntent().getStringExtra("incident_type");
        titleTextView.setText("Report " + mIncidentType + " Incident");
        isLifeThreatening = (CheckBox)findViewById(R.id.checkBox);
        incidentDescription =(EditText)findViewById(R.id.description);

        hideKeypadOnClickingOutsideEditText();
        setSubmitClickListner();
        setPhotoClickListener();
        incidentDescription = (EditText)findViewById(R.id.description);
    }
    private void hideKeypadOnClickingOutsideEditText() {
        constraintLayout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });

    }
    private void setPhotoClickListener() {
        photoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhotoFromGallary();
            }
        });
    }

    private void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                if (data != null) {
                    Uri contentURI = data.getData();
                    if (contentURI != null) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                            photoImageView.setImageBitmap(bitmap);


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
    }

    private void setSubmitClickListner() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIncidentDetailsToCloud();
                Toast.makeText(getApplicationContext(), "Successfully reported incident", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void sendIncidentDetailsToCloud() {
        IncidentUploadRequest request = new IncidentUploadRequest(
                "http://192.168.1.101/fakeurl/",
                null,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                },
                mIncidentType,
                bitmap,
                isLifeThreatening.isChecked(),
                incidentDescription.getText().toString()
        );
        VolleySingleton
                .getInstance(getApplicationContext())
                .getRequestQueue(this.getApplicationContext())
                .add(request);


    }




}
