package com.example.eyitapp;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.eyitapp.HttpLinks.getProducts;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    RecyclerView recyclerView;
    List<Home_Objects> List;
    String URL_TIPS=getProducts;
    LinearLayout progress;
    private  Context context;
    private EditText search_input;
    Home_Adapter adapter;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView =view.findViewById(R.id.tipsRecycle);
        progress=view.findViewById(R.id.progressLay);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        search_input=view.findViewById(R.id.search_input);

        List = new ArrayList<>();
        adapter = new Home_Adapter(getActivity(), List);
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
        return view;
    }

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
                           Toast.makeText(context, "Network Error. Please check your connection", Toast.LENGTH_SHORT).show();
                       }
                       else {
                           Toast.makeText(context, "Error: "+e, Toast.LENGTH_SHORT).show();
                       }
                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
