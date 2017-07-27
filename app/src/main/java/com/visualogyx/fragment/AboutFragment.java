package com.visualogyx.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.visualogyx.R;
import com.visualogyx.activity.AboutDetailActivity;
import com.visualogyx.manager.Global;

public class AboutFragment extends Fragment {

    private RelativeLayout termsRL, licenseRL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        Global.isCanRotateOrientation = true;

        initToolBar();
        initHome(view);

        return view;
    }

    private void initToolBar() {
        Toolbar toolbar_home = (Toolbar) getActivity().findViewById(R.id.toolbar_home);
        toolbar_home.setVisibility(View.GONE);
        Toolbar toolbar_main = (Toolbar) getActivity().findViewById(R.id.toolbar_main);
        toolbar_main.setVisibility(View.VISIBLE);

        TextView title_text = (TextView) toolbar_main.findViewById(R.id.title_text);
        title_text.setText("About");

        ImageView plusBtn = (ImageView) toolbar_main.findViewById(R.id.plusBtn);
        plusBtn.setVisibility(View.GONE);
    }

    private void initHome(View view) {

        termsRL = (RelativeLayout) view.findViewById(R.id.termsRL);
        termsRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AboutDetailActivity.class);
                intent.putExtra("AboutType", "Term");
                startActivity(intent);
            }
        });
        licenseRL = (RelativeLayout) view.findViewById(R.id.licenseRL);
        licenseRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AboutDetailActivity.class);
                intent.putExtra("AboutType", "License");
                startActivity(intent);
            }
        });
    }

}
