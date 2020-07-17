package com.example.eyitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.eyitapp.HttpLinks.countProfile;
import static com.example.eyitapp.HttpLinks.getProducts;
import static com.example.eyitapp.HttpLinks.returnOrder;

public class HomeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Home_Objects> List;
    String URL_TIPS=getProducts;
    LinearLayout progress;
    private  Context context;
    private EditText search_input;
    Home_Adapter adapter;
    ImageButton go_back;
    Toolbar tool;

    MaterialCardView materialCardView1,materialCardView2,materialCardView3,materialCardView4;
    String FindPhone;
    TextView countCartItems;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


//    Transacting fragments
        tool=findViewById(R.id.toolbar);
        go_back=findViewById(R.id.go_back);
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_back.setEnabled(false);
                startActivity(new Intent(HomeActivity.this,DashboardActivity.class));
                finish();
            }
        });
        tool.setTitle("Shop Now");
        setSupportActionBar(tool);
        fadeIn(tool);


        recyclerView =findViewById(R.id.tipsRecycle);
        progress=findViewById(R.id.progressLay);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        search_input=findViewById(R.id.search_input);

        List = new ArrayList<>();
        adapter = new Home_Adapter(HomeActivity.this, List);



        countCartItems=findViewById(R.id.countCart);
        materialCardView1=findViewById(R.id.smartContainer1);
        materialCardView2=findViewById(R.id.smartContainer2);
        materialCardView3=findViewById(R.id.smartContainer3);
        materialCardView4=findViewById(R.id.smartContainer4);
//        materialCardView3.setVisibility(View.GONE);
        materialCardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,CartActivity.class));
            }
        });
        materialCardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,BasketActivity.class));
            }
        });
        materialCardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,DashboardActivity.class));
            }
        });
        FindPhone= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();






        search_input.setImeOptions(EditorInfo.IME_ACTION_DONE);
        search_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        loadObjects();
        countCart();
    }
    private void fadeIn(View view){
        AlphaAnimation animation=new AlphaAnimation(0.0f,1.0f);
        animation.setDuration(1500);
        view.startAnimation(animation);
        view.setVisibility(View.VISIBLE);

    }
    //Method to show current record Current Selected Record
    private void loadObjects() {

        /*
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_TIPS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject pro = array.getJSONObject(i);

                                //adding the product to product list
                                List.add(new Home_Objects(
                                        pro.getInt("id"),
                                        pro.getString("food_name"),
                                        pro.getInt("food_price"),
                                        pro.getString("food_description"),
                                        pro.getString("food_sub_category")
                                ));
                            }
                            //creating adapter object and setting it to recyclerview

                            recyclerView.setAdapter(adapter);
                            progress.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String e= error.getMessage();
                        if (e == null){
                            Toast.makeText(HomeActivity.this, "Network Error. Please check your connection", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(HomeActivity.this, "Error: "+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(HomeActivity.this).add(stringRequest);
    }


    private  void countCart(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Cart").child(FindPhone).child("yoCart");
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
                Toast.makeText(HomeActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
