package com.shefalimunjal.watchoverme.activities.sos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.shefalimunjal.watchoverme.activities.report.IncidentReportActivity;
import com.shefalimunjal.watchoverme.R;
import com.shefalimunjal.watchoverme.models.Contact;
import com.shefalimunjal.watchoverme.utils.Preferences;

import java.util.ArrayList;
import java.util.List;

public class AlertButtonActivity extends AppCompatActivity implements UploadLocationService.ServiceStatusListener,LocationListener{
    private static final String TAG = AlertButtonActivity.class.getSimpleName();
    private static final int RESULT_PICK_CONTACT = 1002;
    private static final int PERMISSIONS_REQUEST_CODE = 1001;

    private List<Contact> emergencyContacts = new ArrayList<>();
    private String emergencyContactNo;


    private View alertButton;
    private TextView reportIncident;
    private TextView emergency;
    private TextView addContactButton;
    private TextView reportIncidentButton;
    private RecyclerView contactRecyclerView ;
    private ContactListAdapter contactListAdapter;
    private static final String GOOGLE_MAPS_BASE_FORMAT_URL =
            "https://www.google.com/maps/search/?api=1&query=%f,%f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_button);
        askRequiredPermissions();
        initUIElements();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAlertButton();
        UploadLocationService.setServiceStatusListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UploadLocationService.setServiceStatusListener(null);
    }

    private void askRequiredPermissions() {
        String[] requiredPermissions = getRequiredPermissionsArray();
        if (requiredPermissions != null) {
            ActivityCompat.requestPermissions(this, requiredPermissions, PERMISSIONS_REQUEST_CODE);
        } else {
            Log.i(TAG,"all permissions already granted");
        }
    }

    private String[] getRequiredPermissionsArray(){
        boolean isSmsPermissionGranted = isSmsPermissionGranted();
        boolean isLocationPermissionGranted = isLocationPermissionGranted();

        if (!isSmsPermissionGranted && !isLocationPermissionGranted) {
            return new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS};
        } else if (!isSmsPermissionGranted) {
            return new String[] {Manifest.permission.SEND_SMS};
        } else if (!isLocationPermissionGranted) {
            return new String[] {Manifest.permission.ACCESS_FINE_LOCATION};
        } else {
            return null;
        }
    }

    private boolean isSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                //reask for Manifest.permission if not granted already
                askRequiredPermissions();

            }
        }
    }

    private void initUIElements(){
        contactRecyclerView = findViewById(R.id.contact_recycler_view);
        alertButton = (View)findViewById(R.id.alert_button);
        addContactButton = findViewById(R.id.add_contact);
        reportIncident = findViewById(R.id.report_incident);
        emergency = (TextView) findViewById(R.id.emergency);

        configureListView();
        setAddContactClickListener();
        setReportIncidentClickListner();
        setAlertButtonClickListner();
    }

    private void configureListView() {
        contactListAdapter = new ContactListAdapter(
                Preferences.getInstance(getApplicationContext()).getEmergencyContacts(),
                getApplicationContext()
        );
        LinearLayoutManager verticalLayoutManager =
                new LinearLayoutManager(
                        AlertButtonActivity.this,
                        LinearLayoutManager.VERTICAL,
                        false
                );
        contactRecyclerView.setLayoutManager(verticalLayoutManager);
        contactRecyclerView.setAdapter(contactListAdapter);
    }

    private void setAddContactClickListener() {
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickContact();
            }
        });
    }

    private void setAlertButtonClickListner(){
        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (UploadLocationService.isStarted()) {
                    Toast.makeText(getApplicationContext(), "We're glad you are safe!", Toast.LENGTH_LONG).show();
                    stopLocationService();
                } else {
                    UploadLocationService.setLocationListner(AlertButtonActivity.this);
                    startLocationService();
                }

            }
        });
    }

    private void startLocationService() {
        Intent intent = new Intent(AlertButtonActivity.this, UploadLocationService.class);
        startService(intent);
    }

    private void stopLocationService() {
        Intent intent = new Intent(AlertButtonActivity.this, UploadLocationService.class);
        stopService(intent);
    }

    private void sendSMSToEmergencyContact(Location location) {
        emergencyContacts= Preferences.getInstance(getApplicationContext()).getEmergencyContacts();
        for(int i= 0;i<emergencyContacts.size();i++) {
            String emergencyContactNo = emergencyContacts.get(i).getPhone();
            Log.i(TAG, "got emergency contact info from shared preferences" + emergencyContactNo);
            if (emergencyContactNo.equals("DEFAULT")) {
                Toast.makeText(getApplicationContext(), "Emergency contact no is not saved", Toast.LENGTH_SHORT).show();

            } else {
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    String message = getResources().getString(R.string.message);
                    if (location != null) {
                        message += " "
                                + String.format(
                                GOOGLE_MAPS_BASE_FORMAT_URL,
                                location.getLatitude(),
                                location.getLongitude());
                    }
                    smsManager.sendTextMessage(emergencyContactNo, null, message, null, null);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again later!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    Log.e(TAG, "error is" + e);
                    stopLocationService();
                }
            }
        }
    }



    private void setReportIncidentClickListner() {
        reportIncident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToIncidentReportPage();
            }
        });
    }

    private void pickContact() {
        Intent contactPickerIntent = new Intent(
                Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        );
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    Cursor cursor = null;
                    try {
                        Uri uri = data.getData();
                        if(uri !=null) {
                        cursor = getContentResolver().query
                                (uri, null,
                                        null,
                                        null,
                                        null); }
                        if(cursor != null && cursor.moveToFirst()) {
                            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                            String phoneNo = cursor.getString(phoneIndex);
                            String name = cursor.getString(nameIndex);
                            addContact(name, phoneNo);
                            Log.i(TAG,"phone no and name  is: " + phoneNo + " " + name);
                            cursor.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else {
            Log.e("Failed", "Not able to pick contact");
        }

    }

    private void addContact(String name, String phoneNo) {
        Contact contact = new Contact();
        contact.setName(name);
        contact.setPhone(phoneNo);
        Preferences.getInstance(getApplicationContext()).addNewContact(contact);
        contactListAdapter.notifyDataSetChanged();
        updateAlertButton();
        alertButton.setEnabled(true);
    }

    private void goToIncidentReportPage() {
        Intent intent = new Intent(this, IncidentReportActivity.class);
        startActivity(intent);
    }

    private void updateAlertButton() {
        Log.d(TAG, "updating alert button");
        if(Preferences.getInstance(getApplicationContext()).getEmergencyContacts().size()==0) {
            alertButton.setBackground(ContextCompat.getDrawable(this,R.drawable.bg_grey_circle));
            Toast.makeText(
                    getApplicationContext(),
                    "Please add emergency contacts",
                    Toast.LENGTH_LONG).show();
            alertButton.setEnabled(false);
        }
        else if (UploadLocationService.isStarted()) {
            alertButton.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_green_circle));
            emergency.setText(R.string.safe_message);
        } else {
            emergency.setText(R.string.unsafe_message);
            alertButton.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_red_circle));
        }
    }

    @Override
    public void onServiceStatusChanged(boolean connected) {
        updateAlertButton();
    }

    @Override
    public void onLocationChanged(Location location){
        sendSMSToEmergencyContact(location);
        Toast.makeText(
                getApplicationContext(),
                "Notified emergency contacts and sending your live location",
                Toast.LENGTH_LONG).show();
        UploadLocationService.setLocationListner(null);
        Log.i(TAG,"Last location from service:" + location);
    }


}
