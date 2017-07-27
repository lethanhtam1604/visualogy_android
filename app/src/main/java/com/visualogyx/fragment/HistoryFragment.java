package com.visualogyx.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ryanharter.android.tooltips.ToolTipLayout;
import com.visualogyx.R;
import com.visualogyx.Utils.Utils;
import com.visualogyx.activity.HistoryDetailActivity;
import com.visualogyx.adapter.history.HistoryAsyncTask;
import com.visualogyx.adapter.history.HistoryFinishLoadAsyncTask;
import com.visualogyx.adapter.history.HistoryListViewAdapter;
import com.visualogyx.apirequest.ApiRequest;
import com.visualogyx.apirequest.account.AccountSuccessResponseModel;
import com.visualogyx.apirequest.history.GetAllHistoryByInspectorApiRequest;
import com.visualogyx.apirequest.history.GetAllHistoryByManagerApiRequest;
import com.visualogyx.apirequest.history.HistorySuccessResponseModel;
import com.visualogyx.manager.Global;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    private ListView historyLV;
    private boolean isClickItemHistoryEnable = true;
    private ToolTipLayout tipContainer;
    private View abstractView;
    private LinearLayout emptyHistoryLL;
    private TextView refreshTV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

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
        title_text.setText("History");

        ImageView plusBtn = (ImageView) toolbar_main.findViewById(R.id.plusBtn);
        plusBtn.setVisibility(View.GONE);
    }

    private void initHome(View view) {
        emptyHistoryLL = (LinearLayout) view.findViewById(R.id.emptyHistoryLL);
        refreshTV = (TextView) view.findViewById(R.id.refreshTV);
        refreshTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadInspectionListView();
            }
        });
        initInspectionListView(view);
    }

    private void initInspectionListView(View view) {
        tipContainer = (ToolTipLayout) view.findViewById(R.id.tooltip_container);
        abstractView = view.findViewById(R.id.abstractView);

        historyLV = (ListView) view.findViewById(R.id.historyLV);
        historyLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                if (isClickItemHistoryEnable) {
                    HistorySuccessResponseModel historySuccessResponseModel = (HistorySuccessResponseModel) view.getTag();
                    Intent intent = new Intent(getActivity(), HistoryDetailActivity.class);
                    intent.putExtra("HistorySuccessResponseModel", historySuccessResponseModel);
                    startActivity(intent);
                }
            }
        });
        loadInspectionListView();
    }

    private void loadInspectionListView() {
        Utils.showHub(getContext());
        AccountSuccessResponseModel accountSuccessResponseModel = Global.settingsManager.getRegisterUser();
        if (accountSuccessResponseModel.user_type.compareTo("manager") == 0) {
            ApiRequest request = new GetAllHistoryByManagerApiRequest(getContext(), new GetAllHistoryRequestResponse(), accountSuccessResponseModel.email);
            request.execute();
        } else {
            ApiRequest request = new GetAllHistoryByInspectorApiRequest(getContext(), new GetAllHistoryRequestResponse(), accountSuccessResponseModel.email);
            request.execute();
        }
    }

    public class GetAllHistoryRequestResponse implements ApiRequest.RequestResponse {
        @Override
        public void requestComplete(boolean status, Object responseObject) {
            if(status) {
                ArrayList<HistorySuccessResponseModel> result = (ArrayList<HistorySuccessResponseModel>) responseObject;
                HistoryListViewAdapter adapter = new HistoryListViewAdapter(getContext(), tipContainer, abstractView);
                historyLV.setAdapter(adapter);
                HistoryAsyncTask historyAsyncTask = new HistoryAsyncTask(adapter, result, new FinishLoadAsyncTask());
                historyAsyncTask.execute();
            }
            else {
                Utils.hideHub();
                Utils.showDisconnectNetworkMessage(getContext(), "Can't fetch data. Please try again!");
            }
        }
    }

    private class FinishLoadAsyncTask implements HistoryFinishLoadAsyncTask {
        @Override
        public void result(ArrayList<HistorySuccessResponseModel> historySuccessResponseModels) {
            Utils.hideHub();
            if (historySuccessResponseModels.size() <= 0)
                emptyHistoryLL.setVisibility(View.VISIBLE);
            else
                emptyHistoryLL.setVisibility(View.GONE);
        }
    }
}
