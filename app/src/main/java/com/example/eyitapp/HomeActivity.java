package com.example.eyitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView navigationView;
    FrameLayout frameLayout;
    Toolbar tool;
    ImageButton go_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        navigationView=findViewById(R.id.nav_view);
        frameLayout=findViewById(R.id.frame);
        go_back=findViewById(R.id.go_back);
        tool=findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        fadeIn(tool);
        setFragment(new HomeFragment());
        tool.setTitle("Product List");

            String tag = getIntent().getStringExtra("Tag");
           if (tag == null){

           }else if (tag=="Cart"){
               Toast.makeText(this, "We Have Refreshed Your Cart. Check out Now", Toast.LENGTH_LONG).show();
           }
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemSelectd=menuItem.getItemId();
                switch (itemSelectd){
                    case R.id.home:
                        tool.setTitle("Dashboard");
                        setFragment(new HomeFragment());
                        return true;
                    case R.id.Cart:
                        tool.setTitle("New Cart/ Order");
                        setFragment(new CartFragment());
                        return true;
                    case R.id.History:
                        tool.setTitle("Orders Track");
                        setFragment(new HistoryFragment());
                        return true;
                        default:
                            return false;
                }

            }
        });

           go_back.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   startActivity(new Intent(HomeActivity.this,DashboardActivity.class));
                   finish();
               }
           });
    }

//    Transacting fragments
   private void setFragment(Fragment fragment) {
    getSupportFragmentManager().beginTransaction().replace(R.id.frame,fragment).commit();

}
private void fadeIn(View view){
    AlphaAnimation animation=new AlphaAnimation(0.0f,1.0f);
    animation.setDuration(1500);
    view.startAnimation(animation);
    view.setVisibility(View.VISIBLE);

}
}
