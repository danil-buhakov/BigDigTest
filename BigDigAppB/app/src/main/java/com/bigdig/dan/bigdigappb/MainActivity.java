package com.bigdig.dan.bigdigappb;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements MainMenuFragment.IExit {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkStartApp();
    }

    private void checkStartApp(){
        String urls = getIntent().getStringExtra("String");
        Fragment fragment;
        if(urls ==null) {
            fragment = new MainMenuFragment();
            ((MainMenuFragment) fragment).setIExit(this);
        }
        else {
            fragment = AppAFragment.newInstance(
                    getIntent().getIntExtra("Status",-1),
                    getIntent().getIntExtra("Id",-1),
                    urls
            );
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container,fragment)
                .commit();
    }


    @Override
    public void Exit() {
        finish();
    }
}
