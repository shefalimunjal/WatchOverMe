package com.shefalimunjal.watchoverme.activities.report;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shefalimunjal.watchoverme.R;

public class IncidentItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static interface IncidentTypeSelectedListener {
        void onIncidentTypeSelected(IncidentType incidentType);
    }

    private Context context;
    LayoutInflater inflater;
    IncidentTypeSelectedListener listener;

    public IncidentItemAdapter(Context context, @NonNull IncidentTypeSelectedListener listener) {
        this.context = context;
        this.listener = listener;
        inflater = (LayoutInflater.from(context));
    }


    @Override
    public int getItemCount() {
        return IncidentType.values().length;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View IncidentItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_incident_item, parent, false);
        final IncidentItemViewHolder incidentItemViewHolder = new IncidentItemViewHolder(IncidentItemView);
        IncidentItemView.setTag(incidentItemViewHolder);
        return incidentItemViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        IncidentType type = IncidentType.values()[position];
        ((IncidentItemViewHolder) holder).bindData(type);
    }

    public class IncidentItemViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView incidentNameTextView;
        View view;
        IncidentItemViewHolder(View incidentItemView) {
            super(incidentItemView);
            view = incidentItemView;
            iconImageView = incidentItemView.findViewById(R.id.icon);
            incidentNameTextView = incidentItemView.findViewById(R.id.icon_name);
        }

        void bindData(final IncidentType incidentType){
            incidentNameTextView.setText(incidentType.getName());
            incidentNameTextView.setTextColor(ContextCompat.getColor(context, incidentType.getBgColorResId()));
            iconImageView.setImageResource(incidentType.getIconResId());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onIncidentTypeSelected(incidentType);
                }
            });
        }
    }
}