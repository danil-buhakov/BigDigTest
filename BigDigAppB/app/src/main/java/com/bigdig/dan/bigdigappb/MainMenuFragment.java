package com.bigdig.dan.bigdigappb;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainMenuFragment extends Fragment {
    TextView mCountdownTextView;
    IExit mIExit;
    final int conutTime = 10000;
    final int conutInterval = 1000;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_menu, container, false);
        mCountdownTextView = (TextView) v.findViewById(R.id.txt_countdown);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new CountDownTimer(conutTime, conutInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                mCountdownTextView.setText(String.valueOf(millisUntilFinished / conutInterval));
            }

            @Override
            public void onFinish() {
                mIExit.Exit();
            }
        }.start();
    }

    public void setIExit(IExit exit) {
        mIExit = exit;
    }

    public interface IExit {
        void Exit();
    }
}
