package com.visualogyx.manager;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore.Images;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

public class ShareManager {

    private static ShareManager instance = null;
    private ShareDialog shareDialog;
    private Context context;

    private ShareManager(Context context) {
        this.context = context;
        FacebookSdk.sdkInitialize(context);
        CallbackManager.Factory.create();
        shareDialog = new ShareDialog((Activity) context);
    }

    public static ShareManager getInstance(Context context) {
        if (instance == null)
            instance = new ShareManager(context);
        return instance;
    }


    public void shareFB(Bitmap bitmap) {
        if (!GooglePlayApp.isInstalledApp(context, "com.facebook.katana"))
            return;

        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        shareDialog.show(content);
    }

    public void shareTwitter(String shareTitle, Bitmap bitmap) {
        if (!GooglePlayApp.isInstalledApp(context, "com.twitter.android"))
            return;

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("image/*");

        String bitmapPath = Images.Media.insertImage(context.getContentResolver(), bitmap, shareTitle, null);
        Uri bitmapUri = Uri.parse(bitmapPath);

        sendIntent.putExtra(Intent.EXTRA_TEXT, shareTitle);
        sendIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        sendIntent.setPackage("com.twitter.android");

        try {
            context.startActivity(sendIntent);
        } catch (Exception e) {
        }
    }

    public void shareWhatsApp(String shareTitle, Bitmap bitmap) {
        if (!GooglePlayApp.isInstalledApp(context, "com.whatsapp"))
            return;

        Intent waIntent = new Intent(Intent.ACTION_SEND);
        waIntent.setType("image/*");

        String bitmapPath = Images.Media.insertImage(context.getContentResolver(), bitmap, shareTitle, null);
        Uri bitmapUri = Uri.parse(bitmapPath);

        waIntent.putExtra(Intent.EXTRA_TEXT, shareTitle);
        waIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        waIntent.setPackage("com.whatsapp");

        context.startActivity(waIntent);
    }
}
