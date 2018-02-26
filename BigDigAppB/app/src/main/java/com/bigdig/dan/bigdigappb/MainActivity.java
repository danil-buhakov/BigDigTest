package com.bigdig.dan.bigdigappb;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements MainMenuFragment.IExit {

    private static final String INTENT_STATUS = "Status";
    private static final String INTENT_ID = "Id";
    private static final String INTENT_URL = "String";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkStartApp();
    }

    private void checkStartApp() {
        String urls = getIntent().getStringExtra(INTENT_URL);
        Fragment fragment;
        if (urls == null) {
            fragment = new MainMenuFragment();
            ((MainMenuFragment) fragment).setIExit(this);
        } else {
            fragment = AppAFragment.newInstance(
                    getIntent().getIntExtra(INTENT_STATUS, -1),
                    getIntent().getIntExtra(INTENT_ID, -1),
                    urls
            );
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }


    @Override
    public void Exit() {
        finish();
    }
}
