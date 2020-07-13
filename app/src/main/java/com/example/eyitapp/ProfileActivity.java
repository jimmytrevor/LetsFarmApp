package com.example.eyitapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.eyitapp.HttpLinks.saveProfile;

public class ProfileActivity extends AppCompatActivity {
    private static final int MY_CAMERA_PERMISSION_CODE = 10;
    private static final int RC_SETTINGS = 100;
    private CircleImageView userImage;
   private ImageButton pickImage;
   private EditText userName,HomeAddress;
   private ImageButton getStarted;
   private Uri imageUri;
   private LinearLayout getProgress;
   private ScrollView scrollView;
   private ProgressBar bar;
   private RadioGroup genderGroup;
   private RadioButton  RadioButtonx;
   String sex;
   private ProgressDialog progressDialog;

    String urlSave=saveProfile;
    HttpParse httpParse = new HttpParse();
    String finalResult;
    HashMap<String,String> hashMap = new HashMap<>();
    String nameHolder,imgxHolder,genderHolder,addressHolder,phoneHolder;
    String getGender="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userImage=findViewById(R.id.userImage);
        pickImage=findViewById(R.id.pickImage);
        userName=findViewById(R.id.username);
        getStarted=findViewById(R.id.get_text);
        scrollView=findViewById(R.id.progressScrol);
        getProgress =findViewById(R.id.showProgress);
        bar=findViewById(R.id.progressBar);
        HomeAddress=findViewById(R.id.homeAddress);
        genderGroup = findViewById(R.id.radioGender);



//
        progressDialog=new ProgressDialog(ProfileActivity.this);

        scrollView.setVisibility(View.VISIBLE);
        getProgress.setVisibility(View.GONE);



        TextView showPhone=findViewById(R.id.showPhone);
        String phone=FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        if (phone.isEmpty() || phone !=null){
            showPhone.setText(phone);
        }


        pickImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {


                getProfileImage();


            }
        });


        getStarted.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                int selectedId = genderGroup.getCheckedRadioButtonId();
                RadioButtonx =  findViewById(selectedId);
                try {
                    if (selectedId ==R.id.radioMale){
                        getGender="Male";
                    }
                    else if (selectedId ==R.id.radioFemale){
                        getGender="Female";
                    }
                    else {
                        getGender="";
                    }
                }catch (Exception e){
                    Toast.makeText(ProfileActivity.this, ""+e, Toast.LENGTH_LONG).show();
                }


               final String getName=userName.getText().toString();
                final String Home=HomeAddress.getText().toString();

                  if (imageUri == null){
                    Snackbar.make(v,"Oops! You didn't provide your image!",Snackbar.LENGTH_LONG).show();
                }
               else if (getName.isEmpty() || getName.length()<3){
                    Snackbar.make(v,"Oops! You didn't provide your name!",Snackbar.LENGTH_LONG).show();
                    userName.requestFocus();
                }
               else if(getGender == ""){
                      Snackbar.make(v, "Oops! You didn't provide your gender!", Snackbar.LENGTH_SHORT).show();
               }
                else if (Home.isEmpty() || Home.length()< 5){
                    Snackbar.make(v,"Oops! You didn't provide your address correctly!",Snackbar.LENGTH_LONG).show();
                    HomeAddress.requestFocus();
                }


                else{
//                    upload information
                    getProgress.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.GONE);
 // initiate the progress bar
                    bar.setMax(100); // 100 maximum value for the progress value
                    bar.setProgress(55); // 50 default progress value for the progress bar
                    bar.setIndeterminate(true);

                    progressDialog.setTitle("Saving ...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    try{
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        final StorageReference filepath = storageReference.child("Profile").child("Clients").child(""+imageUri.getLastPathSegment());
                        filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Profile").child("Clients");
                                        final DatabaseReference db = databaseReference.push();
                                        String  phone= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

                                        Calendar calendar = Calendar.getInstance();
                                        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
                                       Date date=new Date();
                                       calendar.setTime(date);
                                       int hour=calendar.get(Calendar.HOUR_OF_DAY);
                                       int minute=calendar.get(Calendar.MINUTE);
                                       int second=calendar.get(Calendar.SECOND);
                                       String time=String.valueOf(hour+":"+minute+":"+second);
                                        db.child("Access").setValue("on");
                                        db.child("Time").setValue(time);
                                        db.child("Date").setValue(currentDate);
                                        db.child("Gender").setValue(getGender);
                                        db.child("Address").setValue(Home);
                                        db.child("Image").setValue(String.valueOf(uri));
                                        db.child("Phone").setValue(phone);
                                        db.child("Name").setValue(getName);

                                        nameHolder=getName;
                                        genderHolder=getGender;
                                        addressHolder=Home;
                                        phoneHolder=phone;
                                        imgxHolder=String.valueOf(uri);

                                        Toast.makeText(ProfileActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        DataUploadToServerFunction(phoneHolder,nameHolder,genderHolder,addressHolder,imgxHolder);
                                        launchHomeScreen();

                                    }
                                });


                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                int progress = (int) (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage(""+progress+ "%");

                            }
                        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(ProfileActivity.this, "Upload Paused", Toast.LENGTH_SHORT).show();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(ProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }).addOnCanceledListener(new OnCanceledListener() {
                            @Override
                            public void onCanceled() {
                                progressDialog.dismiss();
                                Toast.makeText(ProfileActivity.this, "Upload Cancelled. Try Again", Toast.LENGTH_SHORT).show();

                            }
                        });



                    }catch (Exception e){
                        getProgress.setVisibility(View.GONE);
                        progressDialog.dismiss();
                        scrollView.setVisibility(View.VISIBLE);
                        Snackbar.make(v,""+e.getMessage(),Snackbar.LENGTH_LONG).show();
                    }

                }

            }
        });
    }

    private void launchHomeScreen() {
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getProfileImage() {

            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            }
        CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(ProfileActivity.this);
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK ) {
                Uri resultUri = result.getUri();
                userImage.setImageURI(resultUri);
                imageUri = resultUri;
            }
        } else if (requestCode == RC_SETTINGS) {
            if (data.getData().toString().equals("yes")) {
                String sent = "yes";
                data.setData(Uri.parse(sent));
                setResult(RESULT_OK, data);
                finish();
            }
        }
        else{

        }

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
      new AlertDialog.Builder(ProfileActivity.this)
              .setTitle("Alert")
              .setMessage(Html.fromHtml("<small>Are sure you want to cancel operation?</small>"))
              .setCancelable(false)
              .setNegativeButton("Yes,Exit", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                      Toast.makeText(ProfileActivity.this, "Account Profile Not Set", Toast.LENGTH_SHORT).show();
                      finish();
                  }
              })
              .setPositiveButton("No,Close", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                      dialog.dismiss();
                  }
              })
              .create()
              .show();


    }

    public void DataUploadToServerFunction(String phone,String name,String gender,String address,String image){
        class AsyncTaskUploadClass extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = ProgressDialog.show(ProfileActivity.this,"Data Uploading to server","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();

                // Printing uploading success message coming from server on android app.
                Toast.makeText(ProfileActivity.this,string1,Toast.LENGTH_LONG).show();

                // Setting image as transparent after done uploading.
                userImage.setImageResource(android.R.color.transparent);
            }
            @Override
            protected String doInBackground(String... params) {

                hashMap.put("phoneNumber", params[0]);
                hashMap.put("userName", params[1]);
                hashMap.put("userGender", params[2]);
                hashMap.put("userAddress", params[3]);
                hashMap.put("userImage", params[4]);

                finalResult = httpParse.postRequest(hashMap, urlSave);

                return finalResult;
            }
        }
        AsyncTaskUploadClass userRegisterFunctionClass = new AsyncTaskUploadClass();

        userRegisterFunctionClass.execute(phoneHolder,nameHolder,genderHolder,addressHolder,imgxHolder);
    }
}
