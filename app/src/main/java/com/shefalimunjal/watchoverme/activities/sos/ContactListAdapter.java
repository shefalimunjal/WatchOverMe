package com.shefalimunjal.watchoverme.activities.sos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shefalimunjal.watchoverme.R;
import com.shefalimunjal.watchoverme.models.Contact;

import java.util.List;

public class ContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final String TAG = ContactListAdapter.class.getSimpleName();
    private final List<Contact> emergencyContactsList;
    private Context context;

    public ContactListAdapter(@NonNull List<Contact> emergencyContactList, Context context){
        this.emergencyContactsList = emergencyContactList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return emergencyContactsList.size();
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contactItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, parent, false);
        final ContactItemViewHolder contactItemViewHolder = new ContactItemViewHolder(contactItemView);
        contactItemView.setTag(contactItemViewHolder);
        return contactItemViewHolder;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        Contact contact = emergencyContactsList.get(position);
        ((ContactItemViewHolder)holder).bindData(contact);
    }

    public class ContactItemViewHolder extends RecyclerView.ViewHolder{
        ImageView profileImageView;
        TextView nameTextView;
        //TO-READ: you should always read the warning Android studio gives you. The access was
        // public, could just have been package-private here. Reason: we just need to access these
        // functions from within this java file, and nowhere else. That means it could be package
        // private (same java file also means same java package) and not public.
        ContactItemViewHolder(View contactItemView){
            super(contactItemView);
            profileImageView = contactItemView.findViewById(R.id.profile_pic);
            nameTextView = contactItemView.findViewById(R.id.contact_name);
        }

        void bindData(Contact contact){
            nameTextView.setText(contact.getName());
        }
    }
}
