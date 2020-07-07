package com.example.eyitapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.example.eyitapp.HttpLinks.imagePath;

public class Home_Adapter  extends RecyclerView.Adapter<Home_Adapter.TipViewHolder> {

    private Context mCtx;

    private List<Home_Objects> productList;

    public Home_Adapter(Context mCtx, List<Home_Objects> objects) {
        this.mCtx = mCtx;
        this.productList = objects;
    }

    @Override
    public TipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.row_product, null);
        return new TipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TipViewHolder holder, final int position) {
        final Home_Objects product = productList.get(position);
        final String savedImage=imagePath+""+product.getId() +".jpg";
         int total;

            Glide.with(mCtx)
                    .load(savedImage)
                    .into(holder.PImage);
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
        return productList.size();
    }
    class TipViewHolder extends RecyclerView.ViewHolder {
        private TextView PName,Price,category;
        private ImageView PImage;
        private ImageButton addCart,removeSpot;
        public TipViewHolder(View itemView) {
            super(itemView);

            PName = itemView.findViewById(R.id.pName);
            PImage = itemView.findViewById(R.id.pImage);
            Price = itemView.findViewById(R.id.pPrice);
            category = itemView.findViewById(R.id.category);
            addCart=itemView.findViewById(R.id.addCart);
            removeSpot=itemView.findViewById(R.id.removeSpot);
        }
    }



}