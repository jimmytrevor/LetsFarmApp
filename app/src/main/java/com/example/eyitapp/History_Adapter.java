package com.example.eyitapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import static com.example.eyitapp.HttpLinks.imagePath;

public class History_Adapter  extends RecyclerView.Adapter<History_Adapter.TipViewHolder> implements Filterable {

    private Context mCtx;
    String status;
    private int doll;
    List<History_Objects> mData;
    List<History_Objects> mDataFiltered;

    public History_Adapter(Context mCtx, List<History_Objects> mData) {
        this.mCtx = mCtx;
        this.mData = mData;
        this.mDataFiltered = mData;
    }

    @Override
    public TipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.row_order, parent,false);
//        RecyclerView.LayoutParams layoutParams=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        view.setLayoutParams(layoutParams);

        return new TipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TipViewHolder holder, final int position) {
        final History_Objects product = mDataFiltered.get(position);
        final String savedImage=imagePath+""+product.getID() +".jpg";
        int statusID=product.getOrder_status_ID();

        if (statusID == 1){
            status="Pending";
        }
        else if (statusID == 2){
            status="Accepted";
        }
        else if (statusID == 3){
            status="Delivered";
        }
        else {
            status="Cancelled";
        }
        holder.orderStatus.setText(""+status);
        holder.dateMade.setText(""+product.getDateMade());
        holder.totalPrice.setText("UGX "+product.getPrice());
        holder.totalProduct.setText(""+product.getTotalProducts()+"pdt(s)");
        holder.subPrice.setText("UGX "+product.getPrice());
        holder.orderID.setText(""+product.getID());
         doll=(product.getPrice()/3700);
        holder.dollars.setText("$"+doll);

        holder.expandCart.setAnimation(AnimationUtils.loadAnimation(mCtx,R.anim.fade_in_transition));
        holder.container.setAnimation(AnimationUtils.loadAnimation(mCtx,R.anim.fade_in_transition));
        holder.expandCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCtx.startActivity(new Intent(mCtx,OrderActivity.class)
                        .putExtra("ID",String.valueOf(product.getID()))
                        .putExtra("DOLLARS",String.valueOf(doll))
                        .putExtra("PRICE",String.valueOf(product.getPrice()))
                        .putExtra("STATUS",status)
                        .putExtra("DATE",product.getDateMade())
                        .putExtra("PRODUCTS",String.valueOf(product.getTotalProducts())));
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
                List<History_Objects> FilteredList=new ArrayList<>();
                for (History_Objects row: mData){
                    if (String.valueOf(row.getID()).contains(key) || String.valueOf(row.getPrice()).contains(key) || String.valueOf(row.getTotalProducts()).contains(key) || row.getDateMade().contains(key)){
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

            mDataFiltered=(List<History_Objects>)results.values;
//            mData.clear();
//            mData.addAll((Collection<? extends Home_Objects>) results.values);
            notifyDataSetChanged();
        }
    };
    class TipViewHolder extends RecyclerView.ViewHolder {
        private TextView dateMade,totalProduct,totalPrice,orderStatus,orderID,subPrice,dollars;
        private ImageButton expandCart;
        private MaterialCardView container;
        public TipViewHolder(View itemView) {
            super(itemView);
            dollars = itemView.findViewById(R.id.dollars);
            dateMade = itemView.findViewById(R.id.dateMade);
            orderID = itemView.findViewById(R.id.orderID);
            subPrice=itemView.findViewById(R.id.sub_price);
            expandCart=itemView.findViewById(R.id.orderDetails);
            totalProduct = itemView.findViewById(R.id.orderProducts);
            totalPrice = itemView.findViewById(R.id.orderPrice);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            container = itemView.findViewById(R.id.smartContainer);
        }
    }



}