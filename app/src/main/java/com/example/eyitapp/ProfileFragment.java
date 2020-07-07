package com.example.eyitapp;


import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_porfile, container, false);

        final ImageView userImage=view.findViewById(R.id.userImage);
        final TextView userName=view.findViewById(R.id.userName);
        final TextView userPhone=view.findViewById(R.id.userPhone);
//
//        final Button openShare=view.findViewById(R.id.openShare);
//        final Button openBug =view.findViewById(R.id.openBug);
        final MaterialCardView openSuggest=view.findViewById(R.id.openSuggets);


        openSuggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View alertView= getLayoutInflater().inflate(R.layout.suggest,null);
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext())
                        .setView(alertView)
                        .setCancelable(true);
                AlertDialog dialog=builder.create();
                dialog.show();


            }
        });


        String FindPhone= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Profile").child("Clients");
        Query query=databaseReference.orderByChild("Phone").equalTo(FindPhone);
        query.keepSynced(true);
        databaseReference.keepSynced(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             if (dataSnapshot.exists()){
                 for (DataSnapshot ds : dataSnapshot.getChildren()){
                     userName.setText(ds.child("Name").getValue(String.class));
                     userPhone.setText(ds.child("Phone").getValue(String.class));

//                     Glide.with(getContext()).load(ds.child("Image").getValue(String.class)).into(userImage);
                 }
             }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



        return view;
    }

}
