package com.example.eyitapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    HttpParse httpParse = new HttpParse();
    String finalResult;
    HashMap<String,String> ResultHash = new HashMap<>();
    String FinalJSonObject ;
    String orderIDHolder;
    java.util.List<Single_Object> List;
    Single_Adapter adapter;
    EditText search_input;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        tool=findViewById(R.id.toolbar);
        dateMade = findViewById(R.id.dateMade);
        orderID = findViewById(R.id.orderID);
        subPrice=findViewById(R.id.sub_price);
        totalProduct = findViewById(R.id.orderProducts);
        totalPrice = findViewById(R.id.orderPrice);
        orderStatus = findViewById(R.id.orderStatus);
        dollars =findViewById(R.id.dollars);
       search_input=findViewById(R.id.search_input);

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




}
