package com.example.eyitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import static com.example.eyitapp.HttpLinks.returnOrder;

public class BasketActivity extends AppCompatActivity {
    String finalResult ;
    HashMap<String,String> hashMap = new HashMap<>();
    String ParseResult ;
    String urlReturn=returnOrder;
    HashMap<String,String> ResultHash = new HashMap<>();
    String FinalJSonObject ;
    String TempItem;
    String name;
    HttpParse httpParse = new HttpParse();
    ProgressDialog pDialog;
    ProgressDialog progressDialog2;
    String userPhoneHolder;
    TextView test;
    java.util.List<History_Objects> List;


    RecyclerView recyclerView;
    LinearLayout progress,noOrder,dataLay;
    private Context context;

    History_Adapter adapter;
    EditText search_input;
    Toolbar tool;
    ImageButton go_back;


    MaterialCardView materialCardView1,materialCardView2,materialCardView3,materialCardView4;
    String FindPhone;
    TextView countCartItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        recyclerView =findViewById(R.id.tipsRecycle);
        progress=findViewById(R.id.progressLay);
        dataLay=findViewById(R.id.dataLay);
        test=findViewById(R.id.text);
        search_input=findViewById(R.id.search_input);
        noOrder=findViewById(R.id.emptyLay);
        recyclerView.setHasFixedSize(true);
        noOrder.setVisibility(View.GONE);



        tool=findViewById(R.id.toolbar);
        go_back=findViewById(R.id.go_back);
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_back.setEnabled(false);
                startActivity(new Intent(BasketActivity.this,HomeActivity.class));
                finish();
            }
        });
        tool.setTitle("Your Basket");
        setSupportActionBar(tool);
        fadeIn(tool);


        recyclerView.setLayoutManager(new LinearLayoutManager(BasketActivity.this));

//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
//        recyclerView.setLayoutManager(layoutManager);

        List = new ArrayList<>();
        TextView shopNow=findViewById(R.id.shopNow);
        shopNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BasketActivity.this,HomeActivity.class));
            }
        });

        countCartItems=findViewById(R.id.countCart);
        materialCardView1=findViewById(R.id.smartContainer1);
        materialCardView2=findViewById(R.id.smartContainer2);
        materialCardView3=findViewById(R.id.smartContainer3);
        materialCardView4=findViewById(R.id.smartContainer4);
        materialCardView2.setVisibility(View.GONE);
        materialCardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BasketActivity.this,CartActivity.class));
            }
        });
        materialCardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BasketActivity.this,HomeActivity.class));
            }
        });
        materialCardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BasketActivity.this,DashboardActivity.class));
            }
        });
        FindPhone= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();



countCart();




//        proceed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getContext(),HomeActivity.class));
//                Toast.makeText(getContext(), "Feed Your Cart Now", Toast.LENGTH_SHORT).show();
//            }
//        });
        noOrder.setAnimation(AnimationUtils.loadAnimation(BasketActivity.this,R.anim.fade_in_transition));
//        test.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_scale_transition));
        adapter = new History_Adapter(BasketActivity.this, List);
        userPhoneHolder= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        HttpWebCall(userPhoneHolder);

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

    }
    private void fadeIn(View view){
        AlphaAnimation animation=new AlphaAnimation(0.0f,1.0f);
        animation.setDuration(1500);
        view.startAnimation(animation);
        view.setVisibility(View.VISIBLE);

    }
    //Method to show current record Current Selected Record
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
                Toast.makeText(BasketActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void HttpWebCall(String phone){

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
                new BasketActivity.GetHttpResponse(BasketActivity.this).execute();

            }

            @Override
            protected String doInBackground(String... params) {

                ResultHash.put("userPhone",params[0]);

                ParseResult = httpParse.postRequest(ResultHash, urlReturn);

                return ParseResult;
            }
        }

        HttpWebCallFunction httpWebCallFunction = new HttpWebCallFunction();

        httpWebCallFunction.execute(userPhoneHolder);
    }

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

        @SuppressLint("WrongThread")
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
                            name= jsonObject.getString("Price").toString();
                            List.add(new History_Objects(
                                    jsonObject.getInt("ID"),
                                    jsonObject.getInt("Price"),
                                    jsonObject.getInt("TotalProducts"),
                                    jsonObject.getInt("Order_status_ID"),
                                    jsonObject.getString("Customer_ID"),
                                    jsonObject.getString("DateMade")
                            ));

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

            if (List.size() <= 0){
                progress.setVisibility(View.GONE);
                dataLay.setVisibility(View.GONE);
                noOrder.setVisibility(View.VISIBLE);
                test.setVisibility(View.GONE);

            }
            else{


                recyclerView.setAdapter(adapter);
                progress.setVisibility(View.GONE);
            }


        }
    }

}
