package com.visualogyx.adapter.inspection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mikepenz.iconics.view.IconicsImageView;
import com.ryanharter.android.tooltips.ToolTip;
import com.ryanharter.android.tooltips.ToolTip.Builder;
import com.ryanharter.android.tooltips.ToolTipLayout;
import com.squareup.picasso.Picasso;
import com.visualogyx.R;
import com.visualogyx.Utils.Utils;
import com.visualogyx.activity.NewRequestActivity;
import com.visualogyx.apirequest.inspection.InspectionSuccessResponseModel;
import com.visualogyx.manager.ViewExtras;

import java.util.ArrayList;

public class InspectionListViewAdapter extends BaseAdapter {

    private ArrayList<InspectionSuccessResponseModel> inspectionSuccessResponseModels;
    private Context context;
    private ToolTipLayout tipContainer;
    private View abstractView;

    public InspectionListViewAdapter(Context context, ToolTipLayout tipContainer, View abstractView) {
        this.context = context;
        inspectionSuccessResponseModels = new ArrayList<>();
        this.tipContainer = tipContainer;
        this.abstractView = abstractView;
    }

    public void addInspection(InspectionSuccessResponseModel inspectionSuccessResponseModel) {
        this.inspectionSuccessResponseModels.add(inspectionSuccessResponseModel);
    }

    @Override
    public Object getItem(int position) {
        return inspectionSuccessResponseModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return inspectionSuccessResponseModels.size();
    }

    public void clear() {
        inspectionSuccessResponseModels.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        InspectionSuccessResponseModel inspectionSuccessResponseModel = inspectionSuccessResponseModels.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_inspection, parent, false);
        }

        ImageView ivMap = (ImageView) convertView.findViewById(R.id.ivMap);
        Picasso.with(context).load("http://maps.google.com/maps/api/staticmap?center=" + inspectionSuccessResponseModel.location + "&zoom=15&size=100x50&sensor=false").into(ivMap);

        final RelativeLayout progressBarRL = (RelativeLayout) convertView.findViewById(R.id.progressBarRL);
        ImageView iconImgView = (ImageView) convertView.findViewById(R.id.iconImgView);

        progressBarRL.setVisibility(View.VISIBLE);
        if (inspectionSuccessResponseModel.image_thumb_data.compareTo("") == 0) {
            iconImgView.setImageDrawable(ViewExtras.getDrawable(context, R.drawable.square));
        } else {
            Glide.with(context)
                    .load(inspectionSuccessResponseModel.image_thumb_data)
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

        TextView nameTV = (TextView) convertView.findViewById(R.id.nameTV);
        TextView addressTV = (TextView) convertView.findViewById(R.id.addressTV);
        TextView dateTV = (TextView) convertView.findViewById(R.id.dateTV);
        TextView timeTV = (TextView) convertView.findViewById(R.id.timeTV);
        TextView descriptionTV = (TextView) convertView.findViewById(R.id.descriptionTV);

        nameTV.setText(inspectionSuccessResponseModel.name);
        addressTV.setText(inspectionSuccessResponseModel.address);
        dateTV.setText(Utils.getDate(inspectionSuccessResponseModel.start_date));
        timeTV.setText(Utils.getTime(inspectionSuccessResponseModel.start_date));
        descriptionTV.setText(inspectionSuccessResponseModel.description);

        tipContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dismissTooltip();

                }
                return false;
            }
        });


        IconicsImageView arrowDownImgView = (IconicsImageView) convertView.findViewById(R.id.arrowDownImgView);
        arrowDownImgView.setTag(inspectionSuccessResponseModel);
        arrowDownImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolTip = createToolTip(view, (InspectionSuccessResponseModel) view.getTag());
                abstractView.setVisibility(View.VISIBLE);
                tipContainer.addTooltip(toolTip);
            }
        });

        return convertView;
    }

    private ToolTip toolTip;

    private ToolTip createToolTip(View target, InspectionSuccessResponseModel inspectionSuccessResponseModel) {

        ToolTip toolTip = new Builder(context)
                .anchor(target)
                .gravity(Gravity.BOTTOM)
                .color(Color.WHITE)
                .pointerSize(20)
                .contentView(createView(inspectionSuccessResponseModel))
                .build();
        return toolTip;
    }

    private void dismissTooltip() {
        tipContainer.dismiss();
        abstractView.setVisibility(View.GONE);
        if (toolTip != null)
            tipContainer.removeView(toolTip.getView());
    }

    private View createView(InspectionSuccessResponseModel inspectionSuccessResponseModel) {
        final View itemView = LayoutInflater.from(context).inflate(R.layout.item_show_mode_management, null, false);

        IconicsImageView copyBtn = (IconicsImageView) itemView.findViewById(R.id.copyBtn);
        copyBtn.setTag(inspectionSuccessResponseModel);
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissTooltip();
                Intent intent = new Intent(context, NewRequestActivity.class);
                intent.putExtra("RequestType", "CopyType");
                intent.putExtra("InspectionSuccessResponseModel", (InspectionSuccessResponseModel) view.getTag());
                context.startActivity(intent);
            }
        });

        IconicsImageView deleteBtn = (IconicsImageView) itemView.findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(context)
                        .title("Confirm")
                        .titleColor(ViewExtras.getColor(context, R.color.colorMain))
                        .content("Do you want to delete this request?")
                        .negativeText("NO")
                        .negativeColor(ViewExtras.getColor(context, R.color.main_color))
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .positiveText("YES")
                        .positiveColor(ViewExtras.getColor(context, R.color.main_color))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                                dismissTooltip();
                            }
                        })
                        .canceledOnTouchOutside(false)
                        .cancelable(false)
                        .show();
            }
        });

        return itemView;
    }
}