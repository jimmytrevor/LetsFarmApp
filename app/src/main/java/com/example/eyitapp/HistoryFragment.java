package com.example.eyitapp;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.eyitapp.HttpLinks.returnOrder;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

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
    List<History_Objects> List;


    RecyclerView recyclerView;
    LinearLayout progress,noOrder,dataLay;
    private  Context context;

    History_Adapter adapter;
    EditText search_input;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView =view.findViewById(R.id.tipsRecycle);
        progress=view.findViewById(R.id.progressLay);
        dataLay=view.findViewById(R.id.dataLay);
        test=view.findViewById(R.id.text);
        search_input=view.findViewById(R.id.search_input);
        noOrder=view.findViewById(R.id.emptyLay);
        recyclerView.setHasFixedSize(true);
        noOrder.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
//        recyclerView.setLayoutManager(layoutManager);

        List = new ArrayList<>();

//        proceed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getContext(),HomeActivity.class));
//                Toast.makeText(getContext(), "Feed Your Cart Now", Toast.LENGTH_SHORT).show();
//            }
//        });
        noOrder.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_in_transition));
//        test.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_scale_transition));
       adapter = new History_Adapter(getContext(), List);
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

        return view;
    }

    //Method to show current record Current Selected Record
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
                new HistoryFragment.GetHttpResponse(getActivity()).execute();

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
