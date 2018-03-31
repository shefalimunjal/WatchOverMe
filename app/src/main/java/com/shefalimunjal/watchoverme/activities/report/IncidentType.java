package com.shefalimunjal.watchoverme.activities.report;

import com.shefalimunjal.watchoverme.R;

public enum IncidentType {
    FIRE (R.drawable.fire, "Fire", R.color.fire ),
    MEDICAL (R.drawable.medical, "Medical", R.color.red ),
    ASSAULT (R.drawable.assault, "Assault", R.color.blue ),
    THEFT (R.drawable.theft, "Theft", R.color.purple ),
    ACCIDENT (R.drawable.accident, "Accident", R.color.brown );

    private final int iconResId;
    private final String name;
    private final int bgColorResId;

    private IncidentType (int iconResId, String name, int bgColorResId) {
        this.iconResId = iconResId;
        this.name = name;
        this.bgColorResId = bgColorResId ;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getName() {
        return name;
    }

    public int getBgColorResId() {
        return bgColorResId;
    }
}