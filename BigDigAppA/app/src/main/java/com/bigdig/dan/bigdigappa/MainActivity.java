package com.bigdig.dan.bigdigappa;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText editTextUrl;

    private DbOpenHelper mDbOpenHelper;
    private SQLiteDatabase mDatabase;

    private List<Link> mLinks;
    private HistoryAdapter mHistoryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDatabase();
        initTabs();
        initUI();
    }

    private void initTabs(){
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("tab1");
        spec.setContent(R.id.tab1);
        spec.setIndicator(getString(R.string.tab_1_name));
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("tab2");
        spec.setContent(R.id.tab2);
        spec.setIndicator(getString(R.string.tab_2_name));
        tabHost.addTab(spec);
    }

    private void initDatabase(){
        mDbOpenHelper = new DbOpenHelper(this);
        mDatabase = mDbOpenHelper.getReadableDatabase();
    }
    
    private void initUI(){
        editTextUrl = (EditText) findViewById(R.id.image_url);
        Button btnOpenImage = (Button) findViewById(R.id.btn_open_image);
        btnOpenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent("com.bigdig.dan.IMAGE");
                i.putExtra("String",editTextUrl.getText().toString());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
        RecyclerView hisoryRecycler = (RecyclerView) findViewById(R.id.tab2);
        hisoryRecycler.setLayoutManager(new LinearLayoutManager(this));
        mLinks = new ArrayList<>();
        mHistoryAdapter = new HistoryAdapter(mLinks);
        hisoryRecycler.setAdapter(mHistoryAdapter);
        updateLinks();
    }

    private void updateLinks(){
        Cursor cursor = mDatabase
                .query(
                        DbOpenHelper.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
        mLinks.clear();
        mLinks.addAll(Link.getLinksFromCursor(cursor));
        cursor.close();
        mHistoryAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        updateLinks();
        super.onResume();
    }

    private class LinkHolder extends RecyclerView.ViewHolder {
        TextView mUrlTextView;
        TextView mTimeTextView;
        View cardView;
        public LinkHolder(View itemView) {
            super(itemView);
            cardView = itemView;
            mUrlTextView = itemView.findViewById(R.id.url);
            mTimeTextView = itemView.findViewById(R.id.time);
        }

        public void bindLink(Link link){
            mUrlTextView.setText(link.getUrl());
            Date linkDate = link.getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM HH:mm:ss");
            String time = simpleDateFormat.format(linkDate);
            mTimeTextView.setText(String.valueOf(time));
            switch(link.getStatus()){
                case 1:
                    cardView.setBackgroundColor(Color.GREEN);
                    break;
                case 2:
                    cardView.setBackgroundColor(Color.RED);
                    break;
                case 3:
                    cardView.setBackgroundColor(Color.GRAY);
                    break;
            }
        }
    }

    private class HistoryAdapter extends RecyclerView.Adapter<LinkHolder> {
        private List<Link> mLinkList;
        public HistoryAdapter(List<Link> links){
            mLinkList = links;
        }
        @Override
        public LinkHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.link_item,parent,false);
            return new LinkHolder(v);
        }

        @Override
        public void onBindViewHolder(LinkHolder holder, int position) {
            holder.bindLink(mLinkList.get(position));
        }

        @Override
        public int getItemCount() {
            return mLinkList.size();
        }
    }
}
