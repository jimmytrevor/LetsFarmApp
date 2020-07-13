package com.example.eyitapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.eyitapp.HttpLinks.cancelOrder;
import static com.example.eyitapp.HttpLinks.customOrder;
import static com.example.eyitapp.HttpLinks.saveOrder;

public class OrderActivity extends AppCompatActivity {
    Toolbar tool;
    private TextView dateMade,totalProduct,totalPrice,orderStatus,orderID,subPrice,dollars;
    RecyclerView recyclerView;
    LinearLayout progress,empty,finished;
    String ParseResult ;
    ProgressDialog progressDialog;
    String getCustome=customOrder;
    String deleteURLHttp=cancelOrder;
    HttpParse httpParse = new HttpParse();
    String finalResult;
    HashMap<String,String> ResultHash = new HashMap<>();
    String FinalJSonObject ;
    String orderIDHolder;
   List<Single_Object> List;
    Single_Adapter adapter;
    EditText search_input;
    String deleteIDHolder,clientHolder;
    private RatingBar ratingBar;
     ImageButton go_back;
     TextView delete,rateShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        tool=findViewById(R.id.toolbar);
        dateMade = findViewById(R.id.dateMade);
        orderID = findViewById(R.id.orderID);
        subPrice=findViewById(R.id.sub_price);
        delete=findViewById(R.id.deleteOrder);
        totalProduct = findViewById(R.id.orderProducts);
        totalPrice = findViewById(R.id.orderPrice);
        orderStatus = findViewById(R.id.orderStatus);
        dollars =findViewById(R.id.dollars);
        rateShow=findViewById(R.id.rateShow);
       search_input=findViewById(R.id.search_input);
       ratingBar=findViewById(R.id.rateOrder);

      go_back=findViewById(R.id.go_back);
       go_back.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               go_back.setEnabled(false);
               startActivity(new Intent(OrderActivity.this,HomeActivity.class));
               finish();
           }
       });

        deleteIDHolder=getIntent().getStringExtra("ID");
        clientHolder= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        recyclerView =findViewById(R.id.tipsRecycle);
        progress=findViewById(R.id.progressLay);
        recyclerView.setLayoutManager(new LinearLayoutManager(OrderActivity.this));
//        get extras
        dateMade.setText(""+getIntent().getStringExtra("DATE"));
        orderID.setText(""+getIntent().getStringExtra("ID"));
        subPrice.setText("UGX "+getIntent().getStringExtra("PRICE"));
        totalPrice.setText("UGX "+getIntent().getStringExtra("PRICE"));
        orderStatus.setText(""+getIntent().getStringExtra("STATUS"));
        totalProduct.setText(""+getIntent().getStringExtra("PRODUCTS"));
        dollars.setText("$ "+getIntent().getStringExtra("DOLLARS"));
        List = new ArrayList<>();
        tool.setTitle("Order : " +getIntent().getStringExtra("ID"));
        setSupportActionBar(tool);
        fadeIn(tool);

        adapter = new Single_Adapter(this, List);
        orderIDHolder=""+getIntent().getStringExtra("ID");

        if (getIntent().getStringExtra("STATUS").equalsIgnoreCase("Pending")){
            delete.setVisibility(View.VISIBLE);
        }
        else {
            delete.setVisibility(View.GONE);
        }

        float price=Float.parseFloat(String.valueOf(getIntent().getStringExtra("PRICE")));
        if (price >= 200000){
            ratingBar.setRating(5);
        }
        else if (price<200000 && price >=100000){
            ratingBar.setRating((float) 4.5);
        }
        else if (price<100000 && price >=80000){
            ratingBar.setRating((float) 4);
        }
        else if (price<80000 && price >=50000){
            ratingBar.setRating((float) 3.5);
        }
        else if (price<50000 && price >=30000){
            ratingBar.setRating((float) 3);
        }
        else if (price<30000 && price >=10000){
            ratingBar.setRating((float) 2.5);
        }
        else{
            ratingBar.setRating(2);
        }

        rateShow.setText(String.valueOf(ratingBar.getRating())+"/"+String.valueOf(ratingBar.getNumStars()));
        HttpWebCall(orderIDHolder);

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

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view=getLayoutInflater().inflate(R.layout.confirm_delete,null);
                AlertDialog.Builder builder=new AlertDialog.Builder(OrderActivity.this)
                        .setView(view)
                        .setCancelable(false);
                final AlertDialog dialog=builder.create();
                dialog.show();
                TextView go_delete=view.findViewById(R.id.go_delete);
                 TextView leave=view.findViewById(R.id.leave_me);

                leave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                go_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        DeleteThisOrder(deleteIDHolder,clientHolder);
                    }
                });

            }
        });

    }

    private void fadeIn(View view){
        AlphaAnimation animation=new AlphaAnimation(0.0f,1.0f);
        animation.setDuration(1500);
        view.startAnimation(animation);
        view.setVisibility(View.VISIBLE);

    }


    public void HttpWebCall(String orderID){

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
                new OrderActivity.GetHttpResponse(OrderActivity.this).execute();

            }

            @Override
            protected String doInBackground(String... params) {

                ResultHash.put("Order_ID",params[0]);


                ParseResult = httpParse.postRequest(ResultHash, getCustome);

                return ParseResult;
            }
        }

        HttpWebCallFunction httpWebCallFunction = new HttpWebCallFunction();

        httpWebCallFunction.execute(orderIDHolder);
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

                            List.add(new Single_Object(
                                    jsonObject.getInt("Product_ID"),
                                    jsonObject.getInt("Quantity"),
                                    jsonObject.getInt("Single_Price"),
                                    jsonObject.getString("Name")
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
//            Toast.makeText(context, "Empty: "+List.get(1).getName(), Toast.LENGTH_SHORT).show();
            if (List.size() <= 0){
                progress.setVisibility(View.GONE);
//                dataLay.setVisibility(View.GONE);
//                noOrder.setVisibility(View.VISIBLE);
//                test.setVisibility(View.GONE);
                Toast.makeText(context, "Empty: "+List.get(1).getName(), Toast.LENGTH_SHORT).show();

            }
            else{
                recyclerView.setAdapter(adapter);
                progress.setVisibility(View.GONE);
            }


        }
    }


    public void DeleteThisOrder(String ID,String client){
        class AsyncTaskUploadClass extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = ProgressDialog.show(OrderActivity.this,"Deleting Order","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();

                // Printing uploading success message coming from server on android app.
              if (string1.equalsIgnoreCase("Deleted") || string1.equals("Deleted")){
                  Toast.makeText(OrderActivity.this, "Order Has been Deleted", Toast.LENGTH_LONG).show();
              startActivity(new Intent(OrderActivity.this,HomeActivity.class));
              }
              else if (string1.equals("Order Not Pending") || string1.equalsIgnoreCase("Order Not Pending") || string1.contains("Order Not Pending")){
                  delete.setVisibility(View.GONE);
                  Toast.makeText(OrderActivity.this, "Order Could not be deleted. Already accepted.", Toast.LENGTH_LONG).show();
              }
              else {
                  Toast.makeText(OrderActivity.this, ""+string1, Toast.LENGTH_LONG).show();
              }

                // Setting image as transparent after done uploading.
            }
            @Override
            protected String doInBackground(String... params) {

                ResultHash.put("deleteOrderID", params[0]);
                ResultHash.put("Customer_ID",params[1]);


                finalResult = httpParse.postRequest(ResultHash, deleteURLHttp);

                return finalResult;
            }
        }
        AsyncTaskUploadClass userRegisterFunctionClass = new AsyncTaskUploadClass();

        userRegisterFunctionClass.execute(deleteIDHolder,clientHolder);
    }



}
