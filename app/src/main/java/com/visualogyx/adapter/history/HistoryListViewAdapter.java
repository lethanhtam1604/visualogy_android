package com.visualogyx.adapter.history;

import android.content.Context;
import android.graphics.Bitmap;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.ryanharter.android.tooltips.ToolTip;
import com.ryanharter.android.tooltips.ToolTipLayout;
import com.squareup.picasso.Picasso;
import com.visualogyx.R;
import com.visualogyx.Utils.Utils;
import com.visualogyx.apirequest.history.HistorySuccessResponseModel;
import com.visualogyx.manager.ShareManager;
import com.visualogyx.manager.ViewExtras;

import java.util.ArrayList;

public class HistoryListViewAdapter extends BaseAdapter {

    private ArrayList<HistorySuccessResponseModel> historySuccessResponseModels;
    private Context context;
    private ToolTipLayout tipContainer;
    private View abstractView;

    public HistoryListViewAdapter(Context context, ToolTipLayout tipContainer, View abstractView) {
        this.context = context;
        historySuccessResponseModels = new ArrayList<>();
        this.tipContainer = tipContainer;
        this.abstractView = abstractView;
    }

    public void addHistory(HistorySuccessResponseModel historySuccessResponseModel) {
        this.historySuccessResponseModels.add(historySuccessResponseModel);
    }

    @Override
    public Object getItem(int position) {
        return historySuccessResponseModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return historySuccessResponseModels.size();
    }

    public void clear() {
        historySuccessResponseModels.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HistorySuccessResponseModel historySuccessResponseModel = historySuccessResponseModels.get(position);


        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        }

        ImageView ivMap = (ImageView) convertView.findViewById(R.id.ivMap);
        Picasso.with(context).load("http://maps.google.com/maps/api/staticmap?center=" + historySuccessResponseModel.location + "&zoom=15&size=100x50&sesor=false").into(ivMap);

        final RelativeLayout progressBarRL = (RelativeLayout) convertView.findViewById(R.id.progressBarRL);
        final ImageView iconImgView = (ImageView) convertView.findViewById(R.id.iconImgView);

        progressBarRL.setVisibility(View.VISIBLE);
        if (historySuccessResponseModel.proc_image_thumb_data.compareTo("") == 0) {
            progressBarRL.setVisibility(View.INVISIBLE);
            iconImgView.setImageDrawable(ViewExtras.getDrawable(context, R.drawable.square));
        } else {
            Glide.with(context)
                    .load(historySuccessResponseModel.proc_image_thumb_data)
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

        nameTV.setText(historySuccessResponseModel.name);
        addressTV.setText(historySuccessResponseModel.address);
        dateTV.setText(Utils.getDate(historySuccessResponseModel.start_date));
        timeTV.setText(Utils.getTime(historySuccessResponseModel.start_date));
        descriptionTV.setText(historySuccessResponseModel.description);

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

        ImageView shareImgView = (ImageView) convertView.findViewById(R.id.shareImgView);
        shareImgView.setTag(historySuccessResponseModel);
        shareImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolTip = createToolTip(view, (HistorySuccessResponseModel) view.getTag());
                abstractView.setVisibility(View.VISIBLE);
                tipContainer.addTooltip(toolTip);
            }
        });

        convertView.setTag(historySuccessResponseModel);
        return convertView;
    }


    private ToolTip toolTip;

    private ToolTip createToolTip(View target, HistorySuccessResponseModel historySuccessResponseModel) {

        ToolTip toolTip = new ToolTip.Builder(context)
                .anchor(target)
                .gravity(Gravity.BOTTOM)
                .color(Color.WHITE)
                .pointerSize(20)
                .contentView(createView(historySuccessResponseModel))
                .build();
        return toolTip;
    }

    private void dismissTooltip() {
        tipContainer.dismiss();
        abstractView.setVisibility(View.GONE);
        if (toolTip != null)
            tipContainer.removeView(toolTip.getView());
    }

    private View createView(HistorySuccessResponseModel historySuccessResponseModel) {
        final View itemView = LayoutInflater.from(context).inflate(R.layout.item_show_share_history, null, false);

        ImageView facebookImgView = (ImageView) itemView.findViewById(R.id.facebookImgView);
        facebookImgView.setTag(historySuccessResponseModel);
        facebookImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissTooltip();

                if (!Utils.isNetworkConnected(context)) {
                    Utils.showDisconnectNetworkMessage(context, "Failed to share. Please try again later!");
                    return;
                }

                Utils.showHubNoTitle(context);
                HistorySuccessResponseModel historySuccessResponseModel = (HistorySuccessResponseModel) view.getTag();

                Glide.with(context)
                        .load(historySuccessResponseModel.proc_image_data)
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                ShareManager.getInstance(context).shareFB(resource);
                                Utils.hideHub();
                            }
                        });
            }
        });

        ImageView twitterImgView = (ImageView) itemView.findViewById(R.id.twitterImgView);
        twitterImgView.setTag(historySuccessResponseModel);
        twitterImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissTooltip();

                if (!Utils.isNetworkConnected(context)) {
                    Utils.showDisconnectNetworkMessage(context, "Failed to share. Please try again later!");
                    return;
                }

                Utils.showHubNoTitle(context);
                HistorySuccessResponseModel historySuccessResponseModel = (HistorySuccessResponseModel) view.getTag();
                final String shareTitle = historySuccessResponseModel.description;
                Glide.with(context)
                        .load(historySuccessResponseModel.proc_image_data)
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                ShareManager.getInstance(context).shareTwitter(shareTitle, resource);
                                Utils.hideHub();
                            }
                        });
            }
        });

        ImageView whatsAppImgView = (ImageView) itemView.findViewById(R.id.whatsAppImgView);
        whatsAppImgView.setTag(historySuccessResponseModel);
        whatsAppImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissTooltip();

                if (!Utils.isNetworkConnected(context)) {
                    Utils.showDisconnectNetworkMessage(context, "Failed to share. Please try again later!");
                    return;
                }

                Utils.showHubNoTitle(context);
                HistorySuccessResponseModel historySuccessResponseModel = (HistorySuccessResponseModel) view.getTag();
                final String shareTitle = historySuccessResponseModel.description;
                Glide.with(context)
                        .load(historySuccessResponseModel.proc_image_data)
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                ShareManager.getInstance(context).shareWhatsApp(shareTitle, resource);
                                Utils.hideHub();
                            }
                        });

            }
        });

        return itemView;
    }


}
