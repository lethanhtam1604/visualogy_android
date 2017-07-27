package com.visualogyx.adapter.inspector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.visualogyx.R;
import com.visualogyx.apirequest.account.AccountSuccessResponseModel;
import com.visualogyx.manager.ViewExtras;

import java.util.ArrayList;

public class InspectorListViewAdapter extends BaseAdapter {

    private ArrayList<AccountSuccessResponseModel> accountSuccessResponseModels;
    private Context context;

    public InspectorListViewAdapter(Context context) {
        this.context = context;
        accountSuccessResponseModels = new ArrayList<>();
    }

    public void addInspector(AccountSuccessResponseModel accountSuccessResponseModels) {
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

        AccountSuccessResponseModel accountSuccessResponseModel = accountSuccessResponseModels.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_inspector, parent, false);
        }

        final RelativeLayout progressBarRL = (android.widget.RelativeLayout) convertView.findViewById(R.id.progressBarRL);
        ImageView iconImgView = (ImageView) convertView.findViewById(R.id.iconImgView);

        progressBarRL.setVisibility(View.VISIBLE);
        if (accountSuccessResponseModel.image_thumb_data.compareTo("") == 0) {
            iconImgView.setImageDrawable(ViewExtras.getDrawable(context, R.drawable.square));
        } else {
            Glide.with(context)
                    .load(accountSuccessResponseModel.image_thumb_data)
                    .centerCrop()
                    .crossFade()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progressBarRL.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBarRL.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(iconImgView);

        }

        TextView user_nameTV = (TextView) convertView.findViewById(R.id.user_nameTV);
        TextView emailTV = (TextView) convertView.findViewById(R.id.emailTV);
        user_nameTV.setText(accountSuccessResponseModel.user_name);
        emailTV.setText(accountSuccessResponseModel.email);
        convertView.setTag(accountSuccessResponseModel);
        return convertView;
    }
}
