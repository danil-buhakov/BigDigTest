package com.bigdig.dan.bigdigappb;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String s=getIntent().getStringExtra("String");
        ImageView loadedImage = (ImageView) findViewById(R.id.loaded_image);
        if(s==null)
            s="";
        else
            Picasso.with(this).load(s).into(loadedImage);
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }
    
}
