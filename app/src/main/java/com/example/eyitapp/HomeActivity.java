package com.example.eyitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView navigationView;
    FrameLayout frameLayout;
    Toolbar tool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        navigationView=findViewById(R.id.nav_view);
        frameLayout=findViewById(R.id.frame);
        tool=findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        fadeIn(tool);
        setFragment(new HomeFragment());
        tool.setTitle("Prodcut List");

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
                    case R.id.Account:
                            tool.setTitle("Profile & Setting");
                        setFragment(new ProfileFragment());
                        return true;
                        default:
                            return false;
                }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_activity_items,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId=item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
