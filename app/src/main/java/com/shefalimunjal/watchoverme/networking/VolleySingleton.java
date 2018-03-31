package com.shefalimunjal.watchoverme.networking;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    private static VolleySingleton mInstance;
    private RequestQueue mrequestQueue;
    //private Context context;

    public static VolleySingleton getInstance(Context context){
        if(mInstance == null){
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    private VolleySingleton(Context context){
        mrequestQueue = getRequestQueue(context);

    }

    public RequestQueue getRequestQueue(Context context){
        if(mrequestQueue == null){
            mrequestQueue = Volley.newRequestQueue(context);
        }

        return mrequestQueue;
    }


}
