package com.example.eyitapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
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

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.eyitapp.HttpLinks.countProfile;

public class DashboardActivity extends AppCompatActivity {

    private MaterialCardView openFeed,openPromotions,openSettings,openRate;
    CircleImageView userImage;
    TextView userName;
    TextView userPhone,countCartItems,countOrder,countPending,countStars,openProfile;
    LinearLayout profileLay;
    String FindPhone;
    MaterialCardView materialCardView1,materialCardView2,materialCardView3,materialCardView4;
    DatabaseReference reference;
    HashMap<String,String> hashMap = new HashMap<>();
    String ParseResult ;
    HashMap<String,String> ResultHash = new HashMap<>();
    String FinalJSonObject ;
    String HttpURL = countProfile;
    HttpParse httpParse = new HttpParse();
    String FindHolderPhone;
    String pendingCount,orderCount,priceCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Yo Dashboard");


          userImage=findViewById(R.id.userImage);
         userName=findViewById(R.id.userName);
          userPhone=findViewById(R.id.userPhone);
          profileLay=findViewById(R.id.profileLay);
          countCartItems=findViewById(R.id.countCart);
        countOrder=findViewById(R.id.countAll);
        countPending=findViewById(R.id.countPending);
        countStars=findViewById(R.id.countStars);
        openProfile=findViewById(R.id.openProfile);



        materialCardView1=findViewById(R.id.smartContainer1);
        materialCardView2=findViewById(R.id.smartContainer2);
        materialCardView3=findViewById(R.id.smartContainer3);
        materialCardView4=findViewById(R.id.smartContainer4);

        openFeed=findViewById(R.id.openFeed);
        openPromotions=findViewById(R.id.openPromotion);
        openRate=findViewById(R.id.openRate);
        openSettings=findViewById(R.id.openSetting);







        profileLay.setAnimation(AnimationUtils.loadAnimation(DashboardActivity.this,R.anim.fade_in_transition));
        materialCardView1.setAnimation(AnimationUtils.loadAnimation(DashboardActivity.this,R.anim.fade_in_transition));
        materialCardView2.setAnimation(AnimationUtils.loadAnimation(DashboardActivity.this,R.anim.fade_in_transition));
        materialCardView3.setAnimation(AnimationUtils.loadAnimation(DashboardActivity.this,R.anim.fade_in_transition));
        materialCardView4.setAnimation(AnimationUtils.loadAnimation(DashboardActivity.this,R.anim.fade_in_transition));
        FindPhone= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        reference= FirebaseDatabase.getInstance().getReference().child("Cart").child(FindPhone).child("yoCart");
      FindHolderPhone=FindPhone;


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Notification Not Implemented Yet", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        openFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                openFeed.setEnabled(false);
                startActivity(new Intent(DashboardActivity.this,HomeActivity.class));
            }
        });

        openSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"Service  not Activated Yet",Snackbar.LENGTH_SHORT).show();
            }
        });
        openPromotions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"No Product on Promotion Yet",Snackbar.LENGTH_SHORT).show();
            }
        });
        openRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"Service  not Activated Yet",Snackbar.LENGTH_SHORT).show();
            }
        });

        openProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startActivity(new Intent(DashboardActivity.this,YoProfileActivity.class));
            }
        });

        LoadUserInformation();
        countCart();
        HttpWebCall(FindHolderPhone);
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
                        try{
                            Glide.with(DashboardActivity.this)
                                    .load(""+ds.child("Image").getValue())
                                    .into(userImage);
                        }catch (Exception e){
                            Glide.with(DashboardActivity.this)
                                    .load(R.drawable.image_hint)
                                    .into(userImage);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DashboardActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_activity_items,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId=item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private  void countCart(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();

                if (count > 0){
                    countCartItems.setText(String.valueOf(count));

                }
                else{
                    countCartItems.setText("Empty");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DashboardActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void HttpWebCall(String FindUser){

        class HttpWebCallFunction extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

//                pDialog = ProgressDialog.show(,"Loading Data",null,true,true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

//                pDialog.dismiss();

                //Storing Complete JSon Object into String Variable.
                FinalJSonObject = httpResponseMsg ;

                //Parsing the Stored JSOn String to GetHttpResponse Method.
                new DashboardActivity.GetHttpResponse(DashboardActivity.this).execute();

            }

            @Override
            protected String doInBackground(String... params) {

                ResultHash.put("FindPhone",params[0]);

                ParseResult = httpParse.postRequest(ResultHash, HttpURL);

                return ParseResult;
            }
        }

        HttpWebCallFunction httpWebCallFunction = new HttpWebCallFunction();

        httpWebCallFunction.execute(FindHolderPhone);
    }


    // Parsing Complete JSON Object.
    private class GetHttpResponse extends AsyncTask<Void, Void, Void>
    {
        public Context context;

        public GetHttpResponse(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            try
            {
                if(FinalJSonObject != null)
                {
                    JSONArray jsonArray = null;

                    try {
                        jsonArray = new JSONArray(FinalJSonObject);

                        JSONObject jsonObject;

                        for(int i=0; i<jsonArray.length(); i++)
                        {
                            jsonObject = jsonArray.getJSONObject(i);
                            pendingCount= jsonObject.getString("Pending").toString();
                            orderCount= jsonObject.getString("Total").toString();
                            priceCount= jsonObject.getString("Price").toString();


                            // Storing Student Name, Phone Number, Class into Variables.
//                            Toast.makeText(context,  jsonObject.getString("Gender").toString(), Toast.LENGTH_SHORT).show();

                        }

                    }
                    catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {

            // Setting Student Name, Phone Number, Class into TextView after done all process .
          if (orderCount == null || Integer.parseInt(orderCount) == 0){
              countOrder.setText("Empty");
              countStars.setText("Empty");
          }
          else {
              countOrder.setText(""+orderCount);

          }

            if (pendingCount == null || Integer.parseInt(pendingCount) == 0){
                countPending.setText("None");
            }
            else {
                countPending.setText(""+pendingCount);
            }

        }
    }


}
