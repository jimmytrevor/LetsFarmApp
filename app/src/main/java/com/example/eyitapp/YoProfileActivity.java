package com.example.eyitapp;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

public class YoProfileActivity extends AppCompatActivity {
String FindPhone;
TextView userName,userPhone,DateJoined,userAddress;
CircleImageView userImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yo_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        userImage=findViewById(R.id.userImage);
        userAddress=findViewById(R.id.userAddress);
        userName=findViewById(R.id.userName);
        userPhone=findViewById(R.id.userPhone);
        DateJoined=findViewById(R.id.joinDate);

        FindPhone= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Change Image Not Yet Implement", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LoadUserInformation();
    }


    private void LoadUserInformation(){

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Profile").child("Clients");
        Query query=databaseReference.orderByChild("Phone").equalTo(FindPhone);
        query.keepSynced(true);
        databaseReference.keepSynced(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        userName.setText(ds.child("Name").getValue(String.class).toUpperCase());
                        userPhone.setText(ds.child("Phone").getValue(String.class));
                        DateJoined.setText(DateJoined.getText()+" "+ds.child("Date").getValue(String.class)+" "+ds.child("Time").getValue(String.class));
                        userAddress.setText(ds.child("Address").getValue(String.class));
                        try{
                            Glide.with(YoProfileActivity.this)
                                    .load(""+ds.child("Image").getValue())
                                    .into(userImage);
                        }catch (Exception e){
                            Glide.with(YoProfileActivity.this)
                                    .load(R.drawable.image_hint)
                                    .into(userImage);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(YoProfileActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
