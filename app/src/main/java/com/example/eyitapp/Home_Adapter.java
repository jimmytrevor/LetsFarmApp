package com.example.eyitapp;

import android.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.eyitapp.HttpLinks.imagePath;

public class Home_Adapter  extends RecyclerView.Adapter<Home_Adapter.TipViewHolder> implements Filterable {

    Context mCtx;
    List<Home_Objects> mData;
    List<Home_Objects> mDataFiltered;

    public Home_Adapter(Context context, List<Home_Objects> mData) {
        this.mCtx = context;
        this.mData = mData;
        this.mDataFiltered = mData;

    }

    @Override
    public TipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.row_product, parent,false);
        return new TipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TipViewHolder holder, final int position) {
        final Home_Objects product = mDataFiltered.get(position);
        final String savedImage=imagePath+""+product.getId() +".jpg";
         int total;

            Glide.with(mCtx)
                    .load(savedImage)
                    .into(holder.PImage);
            holder.PImage.setAnimation(AnimationUtils.loadAnimation(mCtx,R.anim.fade_in_transition));
        holder.container.setAnimation(AnimationUtils.loadAnimation(mCtx,R.anim.fade_scale_transition));
        holder.PName.setText(product.getName());
        holder.Price.setText(String.valueOf(product.getPrice()));
        holder.category.setText(String.valueOf(product.getCategory()));


        String userPhone=FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        final DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Cart").child(userPhone).child("yoCart");
        final Query query= FirebaseDatabase.getInstance().getReference().child("Cart").child(userPhone).child("yoCart")
                .orderByChild("ID")
                .equalTo(product.getId());



        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalx =(int)dataSnapshot.getChildrenCount();
                if (totalx >=1){

                   holder.removeSpot.setVisibility(View.VISIBLE);
                   holder.addCart.setVisibility(View.GONE);
                }
                else{
                    holder.removeSpot.setVisibility(View.GONE);
                    holder.addCart.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mCtx, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

      holder.removeSpot.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              mCtx.startActivity(new Intent(mCtx,CartActivity.class));
          }
      });


        holder.addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

//                Toast.makeText(mCtx,"Cart Adding "+product.getName()+"......", Toast.LENGTH_SHORT).show();
//                addToCart(product.getId(),product.getName(),savedImage,product.getPrice(),reference);
                ValueEventListener valueEventListener=new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       int totalx =(int)dataSnapshot.getChildrenCount();
                        if (totalx >=1){

                            Toast.makeText(mCtx, ""+product.getName()+" Already Added", Toast.LENGTH_SHORT).show();
                            holder.removeSpot.setVisibility(View.VISIBLE);
                            holder.addCart.setVisibility(View.GONE);
                        }
                        else{
                            addToCart(product.getId(),product.getName(),savedImage,product.getPrice(),reference);
                            holder.removeSpot.setVisibility(View.VISIBLE);
                            holder.addCart.setVisibility(View.GONE);

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(mCtx, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                };
                query.addListenerForSingleValueEvent(valueEventListener);



            }
        });
    }

    private void addToCart(int id,String name,String image,int price,DatabaseReference reference) {

        final DatabaseReference db=reference.push();
        db.child("Quantity").setValue(1);
        db.child("Image").setValue(image);
        db.child("Price").setValue(price);
        db.child("Name").setValue(name);
        db.child("ID").setValue(id);
        Toast.makeText(mCtx, ""+name+" Added to Cart", Toast.LENGTH_SHORT).show();

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
                List<Home_Objects> FilteredList=new ArrayList<>();
                for (Home_Objects row: mData){
                    if (row.getName().toString().contains(key) || row.getCategory().toString().contains(key) || row.getName().equalsIgnoreCase(key) || row.getName().toLowerCase().contains(key) || row.getName().toUpperCase().contains(key) || row.getCategory().toUpperCase().contains(key)){
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

            mDataFiltered=(List<Home_Objects>)results.values;
//            mData.clear();
//            mData.addAll((Collection<? extends Home_Objects>) results.values);
            notifyDataSetChanged();
        }
    };


    class TipViewHolder extends RecyclerView.ViewHolder {
        private TextView PName,Price,category;
        private ImageView PImage;
        private TextView addCart,removeSpot;
        private MaterialCardView container;
        public TipViewHolder(View itemView) {
            super(itemView);

            PName = itemView.findViewById(R.id.pName);
            PImage = itemView.findViewById(R.id.pImage);
            Price = itemView.findViewById(R.id.pPrice);
            category = itemView.findViewById(R.id.category);
            removeSpot = itemView.findViewById(R.id.removeSpot);
            addCart=itemView.findViewById(R.id.addCart);
            container=itemView.findViewById(R.id.smartContainer);
        }
    }



}