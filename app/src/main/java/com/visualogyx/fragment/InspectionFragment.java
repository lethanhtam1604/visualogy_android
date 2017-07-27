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
import com.visualogyx.activity.NewRequestActivity;
import com.visualogyx.adapter.inspection.InspectionAsyncTask;
import com.visualogyx.adapter.inspection.InspectionFinishLoadAsyncTask;
import com.visualogyx.adapter.inspection.InspectionListViewAdapter;
import com.visualogyx.apirequest.ApiRequest;
import com.visualogyx.apirequest.account.AccountSuccessResponseModel;
import com.visualogyx.apirequest.inspection.InspectionSuccessResponseModel;
import com.visualogyx.apirequest.inspection.get.GetAllInspectionByInspectorApiRequest;
import com.visualogyx.apirequest.inspection.get.GetAllInspectionByManagerApiRequest;
import com.visualogyx.manager.Global;

import java.util.ArrayList;

public class InspectionFragment extends Fragment {

    private ListView inspectionLV;
    private ToolTipLayout tipContainer;
    private View abstractView;
    private LinearLayout emptyInspectionLL;
    private TextView refreshTV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inspection, container, false);
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
        title_text.setText("Manager Mode");

        ImageView plusBtn = (ImageView) toolbar_main.findViewById(R.id.plusBtn);
        plusBtn.setVisibility(View.VISIBLE);
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewRequestActivity.class);
                intent.putExtra("RequestType", "NewType");
                startActivity(intent);
            }
        });
    }

    private void initHome(View view) {
        emptyInspectionLL = (LinearLayout) view.findViewById(R.id.emptyInspectionLL);
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
        inspectionLV = (ListView) view.findViewById(R.id.inspectionLV);
        inspectionLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {

            }
        });
        loadInspectionListView();
    }

    private void loadInspectionListView() {
        Utils.showHub(getContext());
        AccountSuccessResponseModel accountSuccessResponseModel = Global.settingsManager.getRegisterUser();
        if (accountSuccessResponseModel.user_type.compareTo("manager") == 0) {
            ApiRequest request = new GetAllInspectionByManagerApiRequest(getContext(), new GetAllInspectionRequestResponse(), accountSuccessResponseModel.email);
            request.execute();
        } else {
            ApiRequest request = new GetAllInspectionByInspectorApiRequest(getContext(), new GetAllInspectionRequestResponse(), accountSuccessResponseModel.email);
            request.execute();
        }
    }

    public class GetAllInspectionRequestResponse implements ApiRequest.RequestResponse {
        @Override
        public void requestComplete(boolean status, Object responseObject) {
            if(status) {
                ArrayList<InspectionSuccessResponseModel> result = (ArrayList<InspectionSuccessResponseModel>) responseObject;
                InspectionListViewAdapter adapter = new InspectionListViewAdapter(getContext(), tipContainer, abstractView);
                inspectionLV.setAdapter(adapter);
                InspectionAsyncTask boardAsyncTask = new InspectionAsyncTask(adapter, result, new FinishLoadAsyncTask());
                boardAsyncTask.execute();
            }
            else {
                Utils.showDisconnectNetworkMessage(getContext(), "Can't fetch data. Please try again!");
                Utils.hideHub();
            }
        }
    }

    private class FinishLoadAsyncTask implements InspectionFinishLoadAsyncTask {
        @Override
        public void result(ArrayList<InspectionSuccessResponseModel> inspectionSuccessResponseModels) {
            Utils.hideHub();
            if (inspectionSuccessResponseModels.size() <= 0)
                emptyInspectionLL.setVisibility(View.VISIBLE);
            else
                emptyInspectionLL.setVisibility(View.GONE);
        }
    }
}
