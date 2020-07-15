package com.example.eyitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth auth;
    LinearLayout progress,dataLay,denial;
    TextView com1,com2,com3,version,com4;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();

        Animation appAnim = AnimationUtils.loadAnimation(this, R.anim.fadein);
        ImageView imageView=findViewById(R.id.appLogo);
        TextView appName=findViewById(R.id.AppName);
        TextView appInform=findViewById(R.id.appInform);
        progress=findViewById(R.id.progressLay);
        denial=findViewById(R.id.inform);
        com1=findViewById(R.id.communicate1);
        com2=findViewById(R.id.communicate2);
        com3=findViewById(R.id.communicate3);
        com4=findViewById(R.id.communicate4);
        version=findViewById(R.id.version);
        dataLay=findViewById(R.id.lay1);
        imageView.setAnimation(AnimationUtils.loadAnimation(this,R.anim.fade_scale_transition));
        appName.setAnimation(AnimationUtils.loadAnimation(this,R.anim.fade_scale_transition));
        appInform.setAnimation(AnimationUtils.loadAnimation(this,R.anim.fade_in_transition));



        com2.setAnimation(AnimationUtils.loadAnimation(this,R.anim.fade_in_transition));
        com3.setAnimation(AnimationUtils.loadAnimation(this,R.anim.fade_in_transition));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dataLay.setVisibility(View.GONE);
                version.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                denial.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        com1.setAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.fade_in_transition));
                        com1.setVisibility(View.VISIBLE);
                        com2.setVisibility(View.GONE);
                        com3.setVisibility(View.GONE);
                        com4.setVisibility(View.GONE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                com2.setAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.fade_in_transition));
                                com2.setVisibility(View.VISIBLE);
                                com1.setVisibility(View.GONE);
                                com3.setVisibility(View.GONE);
                                com4.setVisibility(View.GONE);
                            ;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        com3.setAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.fade_in_transition));
                                        com3.setVisibility(View.VISIBLE);
                                        com2.setVisibility(View.GONE);
                                        com1.setVisibility(View.GONE);
                                        com4.setVisibility(View.GONE);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                com4.setAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.fade_in_transition));
                                                com4.setVisibility(View.VISIBLE);
                                                com2.setVisibility(View.GONE);
                                                com1.setVisibility(View.GONE);
                                                com3.setVisibility(View.GONE);

                                            }
                                        },10000);
                                    }
                                },10000);
                            }
                        },10000);
                    }
                },10000);
            }
        },5000);



        getConnectionService();
    }



    private void getConnectionService(){
        ConnectivityManager manager=(ConnectivityManager)getSystemService ( Context.CONNECTIVITY_SERVICE );
        NetworkInfo networkInfo=manager.getActiveNetworkInfo ();
        if (networkInfo !=null && networkInfo.isConnected ()){
//            if there is internet connection
            auth.addAuthStateListener ( new FirebaseAuth.AuthStateListener () {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser ();
                    if (user != null) {
                        operation(user.getPhoneNumber());

                    } else {
                        finish();
//                        set auth
                        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());

// Create and launch sign-in intent
                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setAvailableProviders(providers)
                                        .setIsSmartLockEnabled(false)
                                        .build(),
                                RC_SIGN_IN);
                    }
                }
            } );
        }
//        there is no internet connection
        else {

            AlertDialog.Builder  builder=new AlertDialog.Builder(MainActivity.this)
                    .setCancelable(true)
                    .setMessage("No internet connection please turn on data or WiFi for better experience and use of application")
                    .setTitle("Service Failure")
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog =builder.create();
            dialog.show();

        }
    }


    private void operation(String es) {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Profile").child("Clients");
        Query query=databaseReference.orderByChild("Phone").equalTo(es);
        query.keepSynced(true);
        databaseReference.keepSynced(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    int getUser = (int) dataSnapshot.getChildrenCount();
                    if (getUser > 0 ){

//                    user found

//                        finish();

                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String access= (String) ds.child("Access").getValue();
                           if (access.contains("on") || access.equalsIgnoreCase("on") || access.equals("on")){
                               checkPermission();
                           }
                           else {
                               progress.setVisibility(View.GONE);
                               dataLay.setVisibility(View.GONE);
                               denial.setVisibility(View.VISIBLE);
                           }
                        }

                    }
                    else{


                        Toast.makeText(MainActivity.this, "No Profile", Toast.LENGTH_SHORT).show();
//                    set up profile
                        startActivity ( new Intent ( MainActivity.this , ProfileActivity.class ) );
                        finish ();

                    }

                }
                else {
//                    set up profile
                    startActivity ( new Intent ( MainActivity.this , ProfileActivity.class ) );
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }



    public void checkPermission(){
        Dexter.withActivity(MainActivity.this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        startActivity ( new Intent( MainActivity.this , DashboardActivity.class ) );
                        finish();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if(response.isPermanentlyDenied()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Permission Denied")
                                    .setMessage("Permission to access device location is permanently denied. you need to go to setting to allow the permission.")
                                    .setNegativeButton("Cancel", null)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                                        }
                                    })
                                    .show();
                        } else {
                            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                String user = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                operation(user);
//                get User Information
                // ...
            } else {
                Toast.makeText(this, ""+response.getError().getErrorCode(), Toast.LENGTH_SHORT).show();
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }


}
