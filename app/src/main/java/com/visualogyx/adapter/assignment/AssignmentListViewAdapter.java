package com.visualogyx.adapter.assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.visualogyx.R;
import com.visualogyx.Utils.Utils;
import com.visualogyx.apirequest.inspection.InspectionSuccessResponseModel;

import java.util.ArrayList;

public class AssignmentListViewAdapter extends BaseAdapter {

    private ArrayList<InspectionSuccessResponseModel> accountSuccessResponseModels;
    private Context context;

    public AssignmentListViewAdapter(Context context) {
        this.context = context;
        accountSuccessResponseModels = new ArrayList<>();
    }

    public void addAssignment(InspectionSuccessResponseModel accountSuccessResponseModels) {
        this.accountSuccessResponseModels.add(accountSuccessResponseModels);
    }

    @Override
    public Object getItem(int position) {
        return accountSuccessResponseModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return accountSuccessResponseModels.size();
    }

    public void clear() {
        accountSuccessResponseModels.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        InspectionSuccessResponseModel accountSuccessResponseModel = accountSuccessResponseModels.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_assignment, parent, false);
        }

        TextView tvID = (TextView) convertView.findViewById(R.id.tvID);
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);

        tvID.setText(accountSuccessResponseModel.name);
        tvName.setText(accountSuccessResponseModel.address);
        tvDate.setText(Utils.getDate(accountSuccessResponseModel.start_date));
        tvTime.setText(Utils.getTime(accountSuccessResponseModel.start_date));

        convertView.setTag(accountSuccessResponseModel);
        return convertView;
    }
}
