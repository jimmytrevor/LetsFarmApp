package com.example.eyitapp;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.List;

import static com.example.eyitapp.HttpLinks.getProducts;


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
    LinearLayout progress,empty;
    String userPhone;
    TextView textView;
    List<Cart_Objects> list;
    int count;
    TextView sProducts;
    ImageButton process;


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
        empty=view.findViewById(R.id.emptyLay);
        sProducts=view.findViewById(R.id.spdts);
        empty.setVisibility(View.GONE);
        textView=view.findViewById(R.id.text);
        confirm=view.findViewById(R.id.confirm);
        process=view.findViewById(R.id.process);
        confirm.setVisibility(View.GONE);

        orderView =view.findViewById(R.id.orderView);
        behavior = BottomSheetBehavior.from(orderView);




        adapter = new Cart_Adapter(process,sProducts,getActivity(),adapter,proName,proImage,proPrice,proID,proQuantiy,getActivity(),reference,firebaseKeys);
        list=new ArrayList<>();

        userPhone= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        reference= FirebaseDatabase.getInstance().getReference().child("Cart").child(userPhone).child("yoCart");
        countCart();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                Toast.makeText(getContext(), ""+adapter.pricex, Toast.LENGTH_SHORT).show();

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
                    loadObjects();
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






}
