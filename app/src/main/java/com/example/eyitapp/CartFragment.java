package com.example.eyitapp;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Html;
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

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.eyitapp.HttpLinks.getProducts;
import static com.example.eyitapp.HttpLinks.saveOrder;


/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {

    private ArrayList<String> proImage = new ArrayList<>();
    private ArrayList<String> proName = new ArrayList<>();
    private ArrayList<Integer> proID = new ArrayList<>();
    private ArrayList<Integer> proQuantiy = new ArrayList<>();
    private ArrayList<Integer> proPrice = new ArrayList<>();
    ArrayList<String> firebaseKeys = new ArrayList<>();
    private  DatabaseReference reference;
    private Cart_Adapter adapter;
    ImageButton confirm;
    private NestedScrollView orderView;
    private BottomSheetBehavior behavior;

    RecyclerView recyclerView;
    LinearLayout progress,empty,finished;
    String userPhone;
    TextView textView;
    List<Cart_Objects> list;
    int count;
    TextView sProducts;
    ImageButton process;
    List<Cart_Objects> postsList;
    String userHolder,priceHolder,FinePriceHolder,nameHolder;
    private EditText search_input;


    String IdHolder, QuantityHolder, UIDHolder,TotalHolder;

    TextView tt_pdts,sub_price,dollars,delivery,pay_mode,pay_time,discount_percent,discount_price,final_price;
    ProgressDialog progressDialog;
    String urlSave=saveOrder;
    HttpParse httpParse = new HttpParse();
    String finalResult;
    HashMap<String,String> hashMap = new HashMap<>();
    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_cart, container, false);
        View view2 =inflater.inflate(R.layout.delete_cart, container, false);
        recyclerView =view.findViewById(R.id.carRecycle);
        progress=view.findViewById(R.id.progressLay);
        finished=view.findViewById(R.id.finisedOrder);
        empty=view.findViewById(R.id.emptyLay);
        empty.setVisibility(View.GONE);
        textView=view.findViewById(R.id.text);
        confirm=view.findViewById(R.id.confirm);
        process=view.findViewById(R.id.process);
        search_input=view.findViewById(R.id.search_input);
        finished.setVisibility(View.GONE);
        confirm.setVisibility(View.GONE);

        orderView =view.findViewById(R.id.orderView);
        behavior = BottomSheetBehavior.from(orderView);


        sub_price=view.findViewById(R.id.sub_price);
        tt_pdts=view.findViewById(R.id.tt_products);
        dollars=view.findViewById(R.id.dollars);
        delivery=view.findViewById(R.id.delivery);
        pay_mode=view.findViewById(R.id.pay_mode);
        pay_time=view.findViewById(R.id.pay_time);
        discount_percent=view.findViewById(R.id.discount_percent);
        discount_price=view.findViewById(R.id.discount_amount);
        final_price=view.findViewById(R.id.final_price);

        progressDialog=new ProgressDialog(getContext());


//        adapter = new Cart_Adapter(process,sProducts,getActivity(),adapter,proName,proImage,proPrice,proID,proQuantiy,getActivity(),reference,firebaseKeys);
        postsList=new ArrayList <> (  );
        adapter=new Cart_Adapter ( getActivity (), postsList,firebaseKeys);
        userPhone= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        reference= FirebaseDatabase.getInstance().getReference().child("Cart").child(userPhone).child("yoCart");
        countCart();
        confirm.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_scale_transition));
        finished.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_in_transition));
        empty.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_in_transition));
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int totalPrice=0;
                for (int i=0;i<=postsList.size();i++){
                    try{
                        totalPrice= totalPrice+postsList.get(i).getPrice();
                    }catch (Exception e){
//                        Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                    }
                }
                sub_price.setText(""+totalPrice);
                tt_pdts.setText(""+postsList.size());
                dollars.setText(""+(totalPrice/3716));
                delivery.setText("Your Location");
                discount_percent.setText("1%");
                discount_price.setText("100");
                sub_price.setText(""+totalPrice);
                final_price.setText(""+totalPrice);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            }
        });

        process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num=(int)(1352+(Math.random())*4534);
                userHolder=FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                for (int i=0;i<=postsList.size();i++){
                    try {
                        IdHolder=String.valueOf(postsList.get(i).getID());
                        QuantityHolder=String.valueOf(postsList.get(i).getQuantity());
                        priceHolder=String.valueOf(postsList.get(i).getPrice());
                        UIDHolder=String.valueOf(num);
                        FinePriceHolder=final_price.getText().toString();
                        TotalHolder=String.valueOf(postsList.size());
                        nameHolder=postsList.get(i).getName().toString();

                        DataUploadToServerFunction(userHolder,IdHolder,QuantityHolder,priceHolder,UIDHolder,FinePriceHolder,TotalHolder,nameHolder);

                    }catch (Exception e){
//                        Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

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

    private  void countCart(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();

                if (count > 0){
                    String number="<b><u><big>"+count+"</big></u></b>";
                    textView.setText(""+Html.fromHtml(number) +" Items in Your Cart");
                    confirm.setVisibility(View.VISIBLE);
//                    loadObjects();
                    loadUserCart();
                }
                else{
                    String number="<b><u><big>"+count+"</big></u></b>";
                    textView.setText(""+Html.fromHtml(number) +" Items in Your Cart");
                    empty.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                    confirm.setVisibility(View.GONE);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadUserCart() {
        reference.keepSynced ( true );
        reference.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren ()){
                    firebaseKeys.add(ds.getKey());
                    Cart_Objects modelPosts=ds.getValue (Cart_Objects.class);
                    postsList.add ( modelPosts );
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(adapter);
                    progress.setVisibility(View.GONE);
                    confirm.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//if there is an error
                Toast.makeText ( getActivity () , "Error"+ databaseError.getMessage () , Toast.LENGTH_SHORT ).show ();
            }
        } );
    }
    private void loadObjects() {

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                proName.add(dataSnapshot.child("Name").getValue(String.class));
                proImage.add(dataSnapshot.child("Image").getValue(String.class));
                proID.add(dataSnapshot.child("ID").getValue(Integer.class));
                proPrice.add(dataSnapshot.child("Price").getValue(Integer.class));
                proQuantiy.add(dataSnapshot.child("Quantity").getValue(Integer.class));
                firebaseKeys.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
                progress.setVisibility(View.GONE);
                confirm.setVisibility(View.VISIBLE);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
//        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

    }

    public void DataUploadToServerFunction(String orderUser,String productID,String Quantity,String Price,String gIDHolder,String FinePrice,String TotalHold,String name){
        class AsyncTaskUploadClass extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();
                progressDialog.setTitle("Processing Order....");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();

                // Printing uploading success message coming from server on android app.

                if (string1.contains("Server Error") || string1.equals("Server Error")){
                    Toast.makeText(getContext(),"Something Wrong Just Happened : "+string1,Toast.LENGTH_LONG).show();
                }
                else{
                    reference.removeValue();
                    postsList.removeAll(postsList);
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    empty.setVisibility(View.GONE);
                    finished.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), ""+string1, Toast.LENGTH_SHORT).show();
                }

                // Setting image as transparent after done uploading.

            }
            @Override
            protected String doInBackground(String... params) {

                hashMap.put("UserPhone", params[0]);
                hashMap.put("ProductID", params[1]);
                hashMap.put("Quantity", params[2]);
                hashMap.put("SinglePrice", params[3]);
                hashMap.put("Order_ID", params[4]);
                hashMap.put("FinalPrice", params[5]);
                hashMap.put("TotalProducts", params[6]);
                hashMap.put("nameData", params[7]);

                finalResult = httpParse.postRequest(hashMap, urlSave);

                return finalResult;
            }
        }
        AsyncTaskUploadClass userRegisterFunctionClass = new AsyncTaskUploadClass();

        userRegisterFunctionClass.execute(userHolder,IdHolder,QuantityHolder,priceHolder,UIDHolder,FinePriceHolder,TotalHolder,nameHolder);
    }







}
