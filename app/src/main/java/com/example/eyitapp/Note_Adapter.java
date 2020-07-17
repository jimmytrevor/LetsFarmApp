package com.example.eyitapp;

import android.content.Context;
import android.content.Intent;
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

import static com.example.eyitapp.HttpLinks.countProfile;
import static com.example.eyitapp.HttpLinks.imagePath;

public class Note_Adapter  extends RecyclerView.Adapter<Note_Adapter.TipViewHolder> implements Filterable {

    private Context mCtx;
    String status;
    private int doll;
    List<Note_Objects> mData;
    List<Note_Objects> mDataFiltered;

    public Note_Adapter(Context mCtx, List<Note_Objects> mData) {
        this.mCtx = mCtx;
        this.mData = mData;
        this.mDataFiltered = mData;
    }

    @Override
    public TipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.row_notes, parent,false);
//        RecyclerView.LayoutParams layoutParams=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        view.setLayoutParams(layoutParams);

        return new TipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TipViewHolder holder, final int position) {
        final Note_Objects note = mDataFiltered.get(position);

        if (note.getTag().contains("Deleted") || note.getTag().contains("deleted")){
            Glide.with(mCtx).load(R.drawable.ic_playlist_add_check_black_24dp).into(holder.noteImage);
        }
        else if (note.getTag().contains("New") || note.getTag().contains("new")){
            Glide.with(mCtx).load(R.drawable.ic_star_border_black_24dp).into(holder.noteImage);
        }
        else {
            Glide.with(mCtx).load(R.drawable.ic_notifications_none_black_24dp).into(holder.noteImage);
        }

        holder.nTag.setText(""+note.getTag());
        holder.nBody.setText(""+note.getBody());
        holder.nDate.setText(""+note.getDate());
        holder.container.setAnimation(AnimationUtils.loadAnimation(mCtx,R.anim.fade_in_transition));
      holder.container.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if (note.getTag().contains("Deleted") || note.getTag().contains("deleted")){
                 mCtx.startActivity(new Intent(mCtx,BasketActivity.class));
              }
              else if(note.getTag().contains("New Order") || note.getTag().contains("new order")){
                  mCtx.startActivity(new Intent(mCtx,BasketActivity.class));
              }
              else{

              }
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
                List<Note_Objects> FilteredList=new ArrayList<>();
                for (Note_Objects row: mData){
                    if (row.getTag().toString().contains(key) || String.valueOf(row.getBody()).contains(key) || row.getDate().toUpperCase().contains(key)|| row.getBody().toLowerCase().contains(key) || String.valueOf(row.getTag()).contains(key)){
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

            mDataFiltered=(List<Note_Objects>)results.values;
//            mData.clear();
//            mData.addAll((Collection<? extends Home_Objects>) results.values);
            notifyDataSetChanged();
        }
    };
    class TipViewHolder extends RecyclerView.ViewHolder {
        private TextView nTag,nBody,nDate;
        private ImageView noteImage;
        private MaterialCardView container;
        public TipViewHolder(View itemView) {
            super(itemView);
            nTag=itemView.findViewById(R.id.nTag);
            nBody=itemView.findViewById(R.id.nBody);
            nDate=itemView.findViewById(R.id.nDate);
            noteImage=itemView.findViewById(R.id.nImage);
            container=itemView.findViewById(R.id.smartContainer);

        }
    }



}