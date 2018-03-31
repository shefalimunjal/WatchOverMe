package com.shefalimunjal.watchoverme.activities.report;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.shefalimunjal.watchoverme.R;

public class IncidentReportActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private static final int NUMBER_OF_COLUMNS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_report);

        initUIElements();
    }

    private void initUIElements() {
        recyclerView = (RecyclerView) findViewById(R.id.incident_items_recylerView);
        configureListView();
    }

    private void configureListView() {
        IncidentItemAdapter incidentItemAdapter = new IncidentItemAdapter(
                getApplicationContext(),
                new IncidentItemAdapter.IncidentTypeSelectedListener() {
                    @Override
                    public void onIncidentTypeSelected(IncidentType incidentType) {
                        goToSubmitIncidentPage(incidentType);
                    }
                }
        );

        recyclerView.setLayoutManager(new GridLayoutManager(this, NUMBER_OF_COLUMNS));
        recyclerView.setAdapter(incidentItemAdapter);
        incidentItemAdapter.notifyDataSetChanged();
    }

    private void goToSubmitIncidentPage(IncidentType incidentType) {
        Intent intent = new Intent(this, SubmitIncidentActivity.class);
        intent.putExtra("incident_type", incidentType.getName());
        startActivity(intent);
        finish();
    }
}