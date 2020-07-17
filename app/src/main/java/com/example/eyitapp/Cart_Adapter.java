package com.example.eyitapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Cart_Adapter extends RecyclerView.Adapter<Cart_Adapter.MyViewHolder> implements Filterable {
    private Context mContext;
    private ArrayList<String> keys;
     List<Cart_Objects> mData;
    List<Cart_Objects> mDataFiltered;


    public Cart_Adapter(Context mContext, List<Cart_Objects> mData,ArrayList<String> keys) {
        this.mContext = mContext;
        this.mData = mData;
        this.mDataFiltered = mData;
        this.keys=keys;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.row_cart, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
//        spdts.setText(""+proPrice.size());
final int intial_price=mDataFiltered.get(position).getPrice();

        Glide.with(mContext)
                .load(mDataFiltered.get(position).getImage())
                .into(holder.PImage);

        holder.PImage.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_scale_transition));
        holder.container.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_scale_transition));
        holder.PName.setText(""+mDataFiltered.get(position).getQuantity()+" "+mDataFiltered.get(position).getName());
        holder.Price.setText(""+mDataFiltered.get(position).getPrice());

        holder.numberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                int price=(newValue*intial_price);
                holder.Price.setText(String.valueOf(price));
                mDataFiltered.get(position).setPrice(price);
                mDataFiltered.get(position).setQuantity(newValue);
                holder.PName.setText(""+mDataFiltered.get(position).getQuantity()+" "+mDataFiltered.get(position).getName());

            }
        });


        holder.closeThis.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                LayoutInflater inflater=LayoutInflater.from(mContext);
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
                        Toast.makeText(mContext, ""+mDataFiltered.get(position).getName()+" removed from Cart. Cart Refreshed", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
//                        mDataFiltered.clear();
//                        mData.clear();
//                        mContext.startActivity(new Intent(mContext.getApplicationContext(),CartActivity.class));
                        Intent intent=new Intent(mContext,CartActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        mContext.startActivity(intent);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return myFilterData;
    }


    private Filter myFilterData = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String key=constraint.toString();
            if (key.isEmpty()){
                mDataFiltered=mData;
            }
            else{
                List<Cart_Objects> FilteredList=new ArrayList<>();
                for (Cart_Objects row: mData){
                    if (row.getName().toString().contains(key)  || row.getName().toLowerCase().contains(key) || row.getName().toUpperCase().contains(key)){
                        FilteredList.add(row);
                    }
                }

                mDataFiltered=FilteredList;
            }
            FilterResults  filterResults=new FilterResults();
            filterResults.values=mDataFiltered;
            return filterResults;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            mDataFiltered=(List<Cart_Objects>)results.values;
            notifyDataSetChanged();
        }
    };


    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView PName,Price;
        private ImageButton closeThis;
        private ImageView PImage;
        private MaterialCardView container;
        private ElegantNumberButton numberButton;

        MyViewHolder(View view) {
            super(view);
            PName = itemView.findViewById(R.id.cName);
            closeThis = itemView.findViewById(R.id.closeThis);
            PImage = itemView.findViewById(R.id.pImage);
            Price = itemView.findViewById(R.id.cAmount);
            numberButton=itemView.findViewById(R.id.elegant);
            container=itemView.findViewById(R.id.smartContainer);

        }
    }

}
