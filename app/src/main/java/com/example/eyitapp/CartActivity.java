package com.example.eyitapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
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

import static com.example.eyitapp.HttpLinks.saveOrder;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class CartActivity extends AppCompatActivity {
    private ArrayList<String> proImage = new ArrayList<>();
    private ArrayList<String> proName = new ArrayList<>();
    private ArrayList<Integer> proID = new ArrayList<>();
    private ArrayList<Integer> proQuantiy = new ArrayList<>();
    private ArrayList<Integer> proPrice = new ArrayList<>();
    ArrayList<String> firebaseKeys = new ArrayList<>();
    private  DatabaseReference reference;
    private Cart_Adapter adapter;
    TextView confirm;
    private NestedScrollView orderView;
    private BottomSheetBehavior behavior;

    RecyclerView recyclerView;
    LinearLayout progress,empty,finished;
    String userPhone;
    List<Cart_Objects> list;
    int count;
    TextView sProducts;
    TextView process;
    List<Cart_Objects> postsList;
    String userHolder,priceHolder,FinePriceHolder,nameHolder;
    private EditText search_input;
    Toolbar tool;
    ImageButton go_back;


    String IdHolder, QuantityHolder, UIDHolder,TotalHolder;

    TextView tt_pdts,sub_price,dollars,delivery,pay_mode,pay_time,discount_percent,discount_price,final_price;
    ProgressDialog progressDialog;
    String urlSave=saveOrder;
    HttpParse httpParse = new HttpParse();
    String finalResult;
    HashMap<String,String> hashMap = new HashMap<>();

    MaterialCardView materialCardView1,materialCardView2,materialCardView3,materialCardView4;
    String FindPhone;
    TextView countCartItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView =findViewById(R.id.carRecycle);
        progress=findViewById(R.id.progressLay);
        finished=findViewById(R.id.finisedOrder);
        empty=findViewById(R.id.emptyLay);
        empty.setVisibility(View.GONE);
        confirm=findViewById(R.id.confirm);
        process=findViewById(R.id.process);
        search_input=findViewById(R.id.search_input);
        finished.setVisibility(View.GONE);
        confirm.setVisibility(View.GONE);

        orderView =findViewById(R.id.orderView);
        behavior = BottomSheetBehavior.from(orderView);


        tool=findViewById(R.id.toolbar);
        go_back=findViewById(R.id.go_back);
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_back.setEnabled(false);
                startActivity(new Intent(CartActivity.this,HomeActivity.class));
                finish();
            }
        });
        tool.setTitle("Cart Items");
        setSupportActionBar(tool);
        fadeIn(tool);


     TextView check=findViewById(R.id.check);
     check.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             startActivity(new Intent(CartActivity.this,BasketActivity.class));
         }
     });
        sub_price=findViewById(R.id.sub_price);
        tt_pdts=findViewById(R.id.tt_products);
        dollars=findViewById(R.id.dollars);
        delivery=findViewById(R.id.delivery);
        pay_mode=findViewById(R.id.pay_mode);
        pay_time=findViewById(R.id.pay_time);
        discount_percent=findViewById(R.id.discount_percent);
        discount_price=findViewById(R.id.discount_amount);
        final_price=findViewById(R.id.final_price);

        progressDialog=new ProgressDialog(CartActivity.this);



        countCartItems=findViewById(R.id.countCart);
        materialCardView1=findViewById(R.id.smartContainer1);
        materialCardView2=findViewById(R.id.smartContainer2);
        materialCardView3=findViewById(R.id.smartContainer3);
        materialCardView4=findViewById(R.id.smartContainer4);
        materialCardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivity.this,BasketActivity.class));
            }
        });
        materialCardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivity.this,HomeActivity.class));
            }
        });
        materialCardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivity.this,DashboardActivity.class));
            }
        });
        FindPhone= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

TextView shopNow=findViewById(R.id.shopNow);
shopNow.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(CartActivity.this,HomeActivity.class));
    }
});


//        adapter = new Cart_Adapter(process,sProducts,getActivity(),adapter,proName,proImage,proPrice,proID,proQuantiy,getActivity(),reference,firebaseKeys);
        postsList=new ArrayList <> (  );
        adapter=new Cart_Adapter ( CartActivity.this, postsList,firebaseKeys);
        userPhone= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        reference= FirebaseDatabase.getInstance().getReference().child("Cart").child(userPhone).child("yoCart");
        countCart();
        confirm.setAnimation(AnimationUtils.loadAnimation(CartActivity.this,R.anim.fade_scale_transition));
        finished.setAnimation(AnimationUtils.loadAnimation(CartActivity.this,R.anim.fade_in_transition));
        empty.setAnimation(AnimationUtils.loadAnimation(CartActivity.this,R.anim.fade_in_transition));
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
                discount_percent.setText("1%");
                discount_price.setText("100");
                delivery.setText("Your Location");
                sub_price.setText(""+totalPrice);
                final_price.setText(""+totalPrice);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            }
        });

        process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View confirmView=getLayoutInflater().inflate(R.layout.confirm_alert,null);
                AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this)
                        .setCancelable(false)
                        .setView(confirmView);
                final AlertDialog dialog=builder.create();
                dialog.show();

                TextView review=confirmView.findViewById(R.id.review);
                TextView save=confirmView.findViewById(R.id.save);

                review.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

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



    }
    private void fadeIn(View view){
        AlphaAnimation animation=new AlphaAnimation(0.0f,1.0f);
        animation.setDuration(1500);
        view.startAnimation(animation);
        view.setVisibility(View.VISIBLE);

    }
    private  void countCart(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();

                if (count > 0){
                    String number="<b><u><big>"+count+"</big></u></b>";
                    countCartItems.setText(""+ Html.fromHtml(number) +"");
                    confirm.setVisibility(View.VISIBLE);
//                    loadObjects();
                    loadUserCart();
                }
                else{
                    String number="<b><u><big>"+count+"</big></u></b>";
                    countCartItems.setText("Empty");
                    empty.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                    confirm.setVisibility(View.GONE);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CartActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                    recyclerView.setLayoutManager(new LinearLayoutManager(CartActivity.this));
                    recyclerView.setAdapter(adapter);
                    progress.setVisibility(View.GONE);
                    confirm.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//if there is an error
                Toast.makeText (CartActivity.this , "Error"+ databaseError.getMessage () , Toast.LENGTH_SHORT ).show ();
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
                Toast.makeText(CartActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
//        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CartActivity.this));
        recyclerView.setAdapter(adapter);

    }

    public void DataUploadToServerFunction(String orderUser, String productID, String Quantity, String Price, String gIDHolder, String FinePrice, final String TotalHold, String name){
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
                    Toast.makeText(CartActivity.this,"Something Wrong Just Happened : "+string1,Toast.LENGTH_LONG).show();
                }
                else if (string1.contains("Pending") || string1.contains("pending")){
                    loadNotification();
                    Toast.makeText(CartActivity.this, ""+string1, Toast.LENGTH_SHORT).show();
                }
                else if (string1.contains("Target Machine Error") || string1.contains("Target Machine Error") || string1.equalsIgnoreCase("Connection Interrupted Target Machine Error") || string1.contains("Error") || string1.contains("error")){
                    loadError();
                    Toast.makeText(CartActivity.this, ""+string1, Toast.LENGTH_SHORT).show();
                }
                else if (string1.equals("") || string1.equalsIgnoreCase(" ")){
                    loadError();
                    Toast.makeText(CartActivity.this, ""+string1, Toast.LENGTH_SHORT).show();
                }
                else{
                    reference.removeValue();
                    postsList.removeAll(postsList);
                    reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
//                            Toast.makeText(getContext(), "Cart Cleared", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    empty.setVisibility(View.GONE);
                    finished.setVisibility(View.VISIBLE);
                    Toast.makeText(CartActivity.this, ""+string1, Toast.LENGTH_SHORT).show();
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

    public  void loadError(){
        int NOTIFICATION_ID = 234;
        @SuppressLint("RestrictedApi") NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            CHANNEL_ID = "my_zone_02";
            CharSequence name = "Order Error";
            String Description = "Lets Notification Service";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(R.color.colorPrimary);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        RemoteViews collapse=new RemoteViews(CartActivity.this.getPackageName(),R.layout.notification);
        RemoteViews expand=new RemoteViews(CartActivity.this.getPackageName(),R.layout.notification);

        assert CHANNEL_ID != null;
        @SuppressLint("RestrictedApi") NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.safe)
                .setSubText("Order Error")
                .setContentText("Hey, Your Order on Lets Farm App didn't complete. Server Detected. Try again")
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle());


        @SuppressLint("RestrictedApi") Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
        @SuppressLint("RestrictedApi") TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(HomeActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }
    public  void loadNotification(){
        int NOTIFICATION_ID = 234;
        @SuppressLint("RestrictedApi") NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            CHANNEL_ID = "my_zone_01";
            CharSequence name = "Pending Order";
            String Description = "Lets Notification Service";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(R.color.colorPrimary);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        RemoteViews collapse=new RemoteViews(CartActivity.this.getPackageName(),R.layout.notification);
        RemoteViews expand=new RemoteViews(CartActivity.this.getPackageName(),R.layout.notification);

        assert CHANNEL_ID != null;
        @SuppressLint("RestrictedApi") NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.safe)
                .setCustomContentView(collapse)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomBigContentView(expand);


        @SuppressLint("RestrictedApi") Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
        @SuppressLint("RestrictedApi") TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(HomeActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }

}
