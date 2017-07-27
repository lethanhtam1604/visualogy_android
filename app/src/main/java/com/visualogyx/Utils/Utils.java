package com.visualogyx.Utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.visualogyx.R;
import com.visualogyx.manager.Global;
import com.visualogyx.manager.ViewExtras;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static KProgressHUD hub;

    public static void showHub(Context context) {
        hub = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading...");
        hub.show();
    }

    public static void showHubNoTitle(Context context) {
        hub = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
        hub.show();
    }

    public static void hideHub() {
        hub.dismiss();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    public static void animateImageView(final ImageView imageView) {
        imageView.setScaleX(0.0f);
        imageView.setScaleY(0.0f);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(imageView, "scaleX", 1.3f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(imageView, "scaleY", 1.3f);
        scaleDownX.setDuration(250);
        scaleDownY.setDuration(250);
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.start();
        scaleDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(imageView, "scaleX", 1f);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(imageView, "scaleY", 1f);
                scaleDownX.setDuration(100);
                scaleDownY.setDuration(100);
                AnimatorSet scaleDown = new AnimatorSet();
                scaleDown.play(scaleDownX).with(scaleDownY);
                scaleDown.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
    }

    public static Bitmap resize(Bitmap bitmap, float height, float width) {
        float imageScale = Math.min(width / bitmap.getWidth(), height / bitmap.getHeight());
        if (imageScale >= 1) {
            return bitmap;
        }

        int aspectSizeWidth =  Math.round(bitmap.getWidth() * imageScale);
        int aspectSizeHeight =  Math.round(bitmap.getHeight() * imageScale);
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, aspectSizeWidth,
                aspectSizeHeight, true);
        return newBitmap;
    }

    public static Bitmap cropImage(Bitmap bitmap, int toWidth, int toHeight) {
        float ratio = (float) Global.screenWidth / Global.screenHeight;

        float cropWidth = toWidth;
        float cropHeight = toHeight;

        float posX = 0;
        float posY = 0;

        if (bitmap.getHeight() >= bitmap.getWidth()) {
            cropHeight = bitmap.getHeight();
            cropWidth = bitmap.getHeight() * ratio;
            posX = (bitmap.getWidth() - cropWidth) / 2;
        } else {
            cropHeight = bitmap.getWidth() * ratio;
            cropWidth = bitmap.getWidth();
            posY = (bitmap.getHeight() - cropHeight) / 2;
        }
        Bitmap result = Bitmap.createBitmap(bitmap, (int) posX, (int) posY, (int) cropWidth, (int) cropHeight);
        return result;
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isMobileValid(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() < 6 || phone.length() > 13) {
                check = false;
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }

    public static String getDate(String timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(timestamp);
            String result = new SimpleDateFormat("MM/dd/yy").format(date);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static String[] getDateList(String timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(timestamp);
            String result = new SimpleDateFormat("MM-dd-yy").format(date);
            String[] list = result.split("-");
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getTime(String timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = format.parse(timestamp);

            String result = new SimpleDateFormat("HH:mm").format(date);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static String addressFromLocation(Context context, Location location) {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.getDefault());

            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);
            return address;
        } catch (Exception e) {
            return "";
        }
    }

    public static String base64FromBitMap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public static void showDisconnectNetworkMessage(Context context, String message) {
        new MaterialDialog.Builder(context)
                .title("Error")
                .titleColor(ViewExtras.getColor(context, R.color.colorMain))
                .content(message
                )
                .positiveText(R.string.ok)
                .positiveColor(ViewExtras.getColor(context, R.color.main_color))
                .show();
    }

    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH) + "-" + c.get(Calendar.DATE) + "-" + c.get(Calendar.YEAR);
    }

    public static String getCurrentDateFormatServer() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH) + c.get(Calendar.DATE) + c.get(Calendar.YEAR) + "";
    }

    public static String saveImage(Bitmap bitmap) {
        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "VISUALOGYX/VISUALOGYX" + Global.getCurrentDateAndTime() + ".png";
            File pictureFile = new File(path);
            FileOutputStream outStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            return path;
        } catch (Exception e) {
            return "";
        }
    }
}
