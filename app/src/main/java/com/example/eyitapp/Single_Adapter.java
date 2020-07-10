package com.example.eyitapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import static com.example.eyitapp.HttpLinks.imagePath;

public class Single_Adapter  extends RecyclerView.Adapter<Single_Adapter.TipViewHolder> implements Filterable {

    private Context mCtx;
    String status;
    private int doll;
    List<Single_Object> mData;
    List<Single_Object> mDataFiltered;

    public Single_Adapter(Context mCtx, List<Single_Object> mData) {
        this.mCtx = mCtx;
        this.mData = mData;
        this.mDataFiltered = mData;
    }

    @Override
    public TipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.row_single, parent,false);
//        RecyclerView.LayoutParams layoutParams=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        view.setLayoutParams(layoutParams);

        return new TipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TipViewHolder holder, final int position) {
        final Single_Object product = mDataFiltered.get(position);
        final String savedImage=imagePath+""+product.getProduct_ID()+".jpg";
        Glide.with(mCtx)
                .load(savedImage)
                .into(holder.SingleImage);
        holder.SingleQuantity.setText(""+product.getQuantity());
        holder.SingleName.setText(""+product.getName());
        holder.SinglePrice.setText(""+product.getSingle_Price());
        holder.container.setAnimation(AnimationUtils.loadAnimation(mCtx,R.anim.fade_in_transition));


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
                List<Single_Object> FilteredList=new ArrayList<>();
                for (Single_Object row: mData){
                    if (row.getName().toString().contains(key) || String.valueOf(row.getQuantity()).contains(key) || row.getName().toUpperCase().contains(key)|| row.getName().toLowerCase().contains(key) || String.valueOf(row.getSingle_Price()).contains(key)){
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

            mDataFiltered=(List<Single_Object>)results.values;
//            mData.clear();
//            mData.addAll((Collection<? extends Home_Objects>) results.values);
            notifyDataSetChanged();
        }
    };
    class TipViewHolder extends RecyclerView.ViewHolder {
        private TextView SingleName,SinglePrice,SingleQuantity;
        private ImageView SingleImage;
        private MaterialCardView container;
        public TipViewHolder(View itemView) {
            super(itemView);
            SingleName=itemView.findViewById(R.id.SingleName);
            SinglePrice=itemView.findViewById(R.id.SinglePrice);
            SingleQuantity=itemView.findViewById(R.id.singleQuantity);
            SingleImage=itemView.findViewById(R.id.singleImage);
            container=itemView.findViewById(R.id.smartContainer);

        }
    }



}