package com.example.eyitapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.eyitapp.HttpLinks.loadNotification;
import static com.example.eyitapp.HttpLinks.returnOrder;

public class NotificationActivity extends AppCompatActivity {


    String finalResult ;
    HashMap<String,String> hashMap = new HashMap<>();
    String ParseResult ;
    String getNotes=loadNotification;
    HashMap<String,String> ResultHash = new HashMap<>();
    String FinalJSonObject ;
    String TempItem;
    HttpParse httpParse = new HttpParse();
    ProgressDialog pDialog;
    ProgressDialog progressDialog2;
    String userPhoneHolder;
    TextView test;
    List<Note_Objects> List;
    Note_Adapter adapter;
    RecyclerView recyclerView;


    LinearLayout progress;

    Toolbar tool;
    ImageButton go_back;
    LinearLayout emptyNotification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        recyclerView =findViewById(R.id.noteRecycle);
        progress=findViewById(R.id.progressLay);


        tool=findViewById(R.id.toolbar);
        emptyNotification=findViewById(R.id.emptyNote);
        go_back=findViewById(R.id.go_back);
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_back.setEnabled(false);
                startActivity(new Intent(NotificationActivity.this,DashboardActivity.class));
                finish();
            }
        });
        tool.setTitle("Notifications");
        setSupportActionBar(tool);
        fadeIn(tool);

        TextView shopNow=findViewById(R.id.shopNow);
        shopNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotificationActivity.this,HomeActivity.class));
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));



        List = new ArrayList<>();
        adapter = new Note_Adapter(NotificationActivity.this, List);
        userPhoneHolder= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        HttpWebCall(userPhoneHolder);


    }

    private void fadeIn(View view){
        AlphaAnimation animation=new AlphaAnimation(0.0f,1.0f);
        animation.setDuration(1500);
        view.startAnimation(animation);
        view.setVisibility(View.VISIBLE);

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
                new NotificationActivity.GetHttpResponse(NotificationActivity.this).execute();

            }

            @Override
            protected String doInBackground(String... params) {

                ResultHash.put("userPhone",params[0]);

                ParseResult = httpParse.postRequest(ResultHash, getNotes);

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
                            List.add(new Note_Objects(
                                    jsonObject.getInt("id"),
                                    jsonObject.getString("tag"),
                                    jsonObject.getString("body"),
                                    jsonObject.getString("visibility"),
                                    jsonObject.getString("destination"),
                                    jsonObject.getString("date")
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
                emptyNotification.setVisibility(View.VISIBLE);

            }
            else{

                recyclerView.setAdapter(adapter);
                progress.setVisibility(View.GONE);
            }


        }
    }


}
