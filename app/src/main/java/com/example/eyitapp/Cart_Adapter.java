package com.example.eyitapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Cart_Adapter extends RecyclerView.Adapter<Cart_Adapter.MyViewHolder> {
    public String pricex;
    int total=0,price=0;
    private Context mContext;
    private ArrayList<String> proName;
    private ArrayList<String> proImage;
    private ArrayList<Integer> proPrice;
    private ArrayList<Integer> proID;
    private ArrayList<Integer> proQuantity;
    private ArrayList<Cart_Objects> cart_objects;
    private Activity activity;
    private Cart_Adapter adapter;
    private static final int MY_CONST = 100;
    private DatabaseReference reference;
    private ArrayList<String> keys;
    NestedScrollView nestedView;
    private  String numberString;
    BottomSheetBehavior behavior;
    private TextView spdts;
    private ImageButton process;

    public Cart_Adapter(ImageButton process, TextView spdt, Context mContext, Cart_Adapter adapter1, ArrayList<String> proName, ArrayList<String> proImagex, ArrayList<Integer> proPrice, ArrayList<Integer> proID, ArrayList<Integer> proQuantity, Activity activity, DatabaseReference reference, ArrayList<String> keys) {
        this.mContext = mContext;
        this.spdts= spdt;
        this.process =process;
        this.proName = proName;
        this.proPrice = proPrice;
        this.proID = proID;
        this.adapter=adapter1;
        this.proImage=proImagex;
        this.proQuantity = proQuantity;
        this.activity = activity;
        this.reference = reference;
        this.keys = keys;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.row_cart, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        spdts.setText(""+proPrice.size());

        Glide.with(activity)
                .load(proImage.get(position))
                .into(holder.PImage);
        holder.PName.setText(proName.get(position));
        holder.Price.setText(String.valueOf(proPrice.get(position)));
        pricex=holder.Price.getText().toString();
        final int p=    proPrice.set(position,Integer.parseInt(holder.Price.getText().toString()));
        holder.numberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                int price=(newValue*proPrice.get(position));
                holder.Price.setText(String.valueOf(price));

                Toast.makeText(mContext, ""+p, Toast.LENGTH_SHORT).show();
            }
        });

        /* int pricex= Integer.parseInt(holder.numberButton.getNumber())*proPrice.get(position); */


        process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog=new ProgressDialog(mContext);
                progressDialog.setMessage("Order Processing......");
                progressDialog.setCancelable(false);
                progressDialog.show();

            }
        });

        holder.closeThis.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                LayoutInflater inflater=LayoutInflater.from(activity);
                View view=inflater.inflate(R.layout.delete_cart,null);

                Button close=view.findViewById(R.id.closeBtn);
                Button delete=view.findViewById(R.id.deleteCart);


                AlertDialog.Builder builder=new AlertDialog.Builder(mContext)
                        .setView(view)
                        .setCancelable(false);
                final AlertDialog dialog=builder.create();
                dialog.show();
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String userPhone= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Cart").child(userPhone).child("yoCart");
                        reference.child(keys.get(position)).removeValue();
                        Toast.makeText(activity, ""+proName.get(position)+" removed from Cart", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        mContext.startActivity(new Intent(activity.getApplicationContext(),HomeActivity.class).putExtra("Tag","Cart"));

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return proImage.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView PName,Price,closeThis;
        private ImageView PImage;
        private ElegantNumberButton numberButton;

        MyViewHolder(View view) {
            super(view);
            PName = itemView.findViewById(R.id.cName);
            closeThis = itemView.findViewById(R.id.closeThis);
            PImage = itemView.findViewById(R.id.pImage);
            Price = itemView.findViewById(R.id.cAmount);
            numberButton=itemView.findViewById(R.id.elegant);

        }
    }

}
