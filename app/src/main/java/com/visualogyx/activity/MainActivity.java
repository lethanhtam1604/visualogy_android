package com.visualogyx.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;
import com.testfairy.TestFairy;
import com.visualogyx.R;
import com.visualogyx.Utils.ShapeRotation;
import com.visualogyx.apirequest.account.AccountSuccessResponseModel;
import com.visualogyx.fragment.AboutFragment;
import com.visualogyx.fragment.HistoryFragment;
import com.visualogyx.fragment.HomeFragment;
import com.visualogyx.fragment.InspectionFragment;
import com.visualogyx.fragment.TrainingFragment;
import com.visualogyx.interfaceUI.BackToCamera;
import com.visualogyx.manager.Global;
import com.visualogyx.manager.ViewExtras;

import java.io.File;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    // Drawer Menu
    private PrimaryDrawerItem homeItem, inspectionsItem, trainingItem, historyItem, aboutItem, logoutItem;
    private IProfile[] mallProfileList;
    private AccountHeader mDrawerHeader;
    private Drawer mDrawer;

    private ImageView icon_menu_imgView;
    private Toolbar toolbar_home;
    private RelativeLayout containHomeView;

    static { System.loadLibrary("MyLibrary");}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TestFairy.begin(this, getResources().getString(R.string.test_fairy_id));

        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        begin();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    private void begin() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        Global.screenWidth = size.x;
        Global.screenHeight = size.y;
        Global.screenRatio = (float) size.x / size.y;

        containHomeView = (RelativeLayout) findViewById(R.id.containHomeView);
        initialize();
        InitializeDrawerMenu();
    }

    public void initialize() {
        Global.context = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        createFolder();
        createActionBar();
    }

    private void createActionBar() {
        toolbar_home = (Toolbar) findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar_home);

        icon_menu_imgView = (ImageView) toolbar_home.findViewById(R.id.icon_menu_imgView);
    }

    private void createFolder() {
        File dir = new File("/sdcard/VISUALOGYX/");
        if (!dir.exists())
            dir.mkdirs();
    }

    private void InitializeDrawerMenu() {
        mallProfileList = new IProfile[1];

        AccountSuccessResponseModel accountSuccessResponseModel = Global.settingsManager.getRegisterUser();
        if (accountSuccessResponseModel.image_thumb_data != null) {
            DrawerImageLoader.init(new AbstractDrawerImageLoader() {
                @Override
                public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                    Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
                }

                @Override
                public void cancel(ImageView imageView) {
                    Picasso.with(imageView.getContext()).cancelRequest(imageView);
                }
            });
            mallProfileList[0] = new ProfileDrawerItem().withName(accountSuccessResponseModel.user_name).withEmail(accountSuccessResponseModel.email)
                    .withIcon(accountSuccessResponseModel.image_thumb_data);
        } else
            mallProfileList[0] = new ProfileDrawerItem().withName(accountSuccessResponseModel.user_name).withEmail(accountSuccessResponseModel.email)
                    .withIcon(R.drawable.default_user);

        homeItem = new PrimaryDrawerItem().withName("Home").withIcon(R.drawable.home_1).withIdentifier(1)
                .withSelectedIconColorRes(R.color.white).withIconColorRes(R.color.selected).withSelectedColorRes(R.color.selected)
                .withSelectedTextColorRes(R.color.white).withIconTinted(true);

        inspectionsItem = new PrimaryDrawerItem().withName("Inspections").withIcon(R.drawable.inspections_1).withIdentifier(2)
                .withSelectedIconColorRes(R.color.white).withIconColorRes(R.color.selected).withSelectedColorRes(R.color.selected)
                .withSelectedTextColorRes(R.color.white).withIconTinted(true);

        trainingItem = new PrimaryDrawerItem().withName("Training").withIcon(R.drawable.tranining).withIdentifier(3)
                .withSelectedIconColorRes(R.color.white).withIconColorRes(R.color.selected).withSelectedColorRes(R.color.selected)
                .withSelectedTextColorRes(R.color.white).withIconTinted(true);

        historyItem = new PrimaryDrawerItem().withName("History").withIcon(R.drawable.history_1).withIdentifier(4)
                .withSelectedIconColorRes(R.color.white).withIconColorRes(R.color.selected).withSelectedColorRes(R.color.selected)
                .withSelectedTextColorRes(R.color.white).withIconTinted(true);

        aboutItem = new PrimaryDrawerItem().withName("About").withIcon(R.drawable.ic_info_white_24dp).withIdentifier(5)
                .withSelectedIconColorRes(R.color.white).withIconColorRes(R.color.selected).withSelectedColorRes(R.color.selected)
                .withSelectedTextColorRes(R.color.white).withIconTinted(true);

        IconicsDrawable poweroff = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_power_off)
                .color(Color.RED)
                .sizeDp(24);

        logoutItem = new PrimaryDrawerItem().withName("Logout").withIcon(poweroff).withIdentifier(6)
                .withSelectedIconColorRes(R.color.white).withIconColorRes(R.color.selected).withSelectedColorRes(R.color.selected)
                .withSelectedTextColorRes(R.color.white).withIconTinted(true);

        mDrawerHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withHeaderBackground(R.drawable.header_navigation_view)
                .addProfiles(
                        mallProfileList
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, final IProfile profile, boolean currentProfile) {
                        return true;
                    }
                })
                .build();

        if (accountSuccessResponseModel.user_type.compareTo("manager") == 0) {
            mDrawer = new DrawerBuilder()
                    .withAccountHeader(mDrawerHeader)
                    .withSystemUIHidden(true)
                    .withActivity(this)
                    .addDrawerItems(
                            homeItem,
                            inspectionsItem,
                            trainingItem,
                            historyItem,
                            aboutItem,
                            logoutItem
                    )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            mDrawer.closeDrawer();
                            final long optionID = drawerItem.getIdentifier();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    selectItemMenu((int) optionID);
                                }
                            }, 300);
                            return true;
                        }
                    })
                    .build();
        } else {
            mDrawer = new DrawerBuilder()
                    .withAccountHeader(mDrawerHeader)
                    .withSystemUIHidden(true)
                    .withActivity(this)
                    .addDrawerItems(
                            homeItem,
                            trainingItem,
                            historyItem,
                            aboutItem,
                            logoutItem
                    )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            mDrawer.closeDrawer();
                            final long optionID = drawerItem.getIdentifier();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    selectItemMenu((int) optionID);
                                }
                            }, 300);
                            return true;
                        }
                    })
                    .build();
        }


        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentName);
        if (fragment != null)
            fragmentManager.beginTransaction().remove(fragment).commit();
        if (mDrawer != null)
            mDrawer.setSelection(homeItem, true);
    }

    private String fragmentName;
    Fragment fragment = null;

    private void selectItemMenu(int optionID) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (optionID) {
            case 1:
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                Global.isCanRotateOrientation = false;
                fragmentName = "HomeFragment";
                fragment = fragmentManager.findFragmentByTag(fragmentName);
//                if (fragment == null);
                fragment = new HomeFragment();
                containHomeView.setVisibility(View.VISIBLE);
                break;
            case 2:
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                fragmentName = "InspectionFragment";
                fragment = fragmentManager.findFragmentByTag(fragmentName);
                if (fragment == null)
                    fragment = new InspectionFragment();
                Global.backToCamera = new BackToCameraClass();
                break;
            case 3:
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                fragmentName = "TrainingFragment";
                fragment = fragmentManager.findFragmentByTag(fragmentName);
                if (fragment == null)
                    fragment = new TrainingFragment();
                Global.backToCamera = new BackToCameraClass();
                break;
            case 4:
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                fragmentName = "HistoryFragment";
                fragment = fragmentManager.findFragmentByTag(fragmentName);
                if (fragment == null)
                    fragment = new HistoryFragment();
                break;
            case 5:
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                fragmentName = "AboutFragment";
                fragment = fragmentManager.findFragmentByTag(fragmentName);
                if (fragment == null)
                    fragment = new AboutFragment();
                break;
            case 6:
                Global.settingsManager.setIsRememberMe(false);
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                this.finish();
                break;
        }

        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment, fragmentName)
                    .commit();
        }
    }

    public void icon_menu_click(View v) {
        mDrawer.openDrawer();
    }

    private OrientationEventListener mOrientationEventListener;

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (Global.sendForTraining) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDrawer.setSelection(trainingItem, true);
                        selectItemMenu(3);
                    }
                }, 300);
            }

            if (Global.pipeType != null) {
                ((HomeFragment) fragment).drawPipe(Global.pipeType);
            }

            if (mOrientationEventListener == null) {
                mOrientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
                    @Override
                    public void onOrientationChanged(int orientation) {
                        if (!Global.isCanRotateOrientation) {
                            int lastOrientation = Global.mOrientation;
                            if (orientation >= 315 || orientation < 45) {
                                if (Global.mOrientation != Global.ORIENTATION_PORTRAIT_NORMAL) {
                                    Global.mOrientation = Global.ORIENTATION_PORTRAIT_NORMAL;
                                }
                            } else if (orientation < 315 && orientation >= 225) {
                                if (Global.mOrientation != Global.ORIENTATION_LANDSCAPE_NORMAL) {
                                    Global.mOrientation = Global.ORIENTATION_LANDSCAPE_NORMAL;
                                }
                            } else if (orientation < 225 && orientation >= 135) {
                                if (Global.mOrientation != Global.ORIENTATION_PORTRAIT_INVERTED) {
                                    Global.mOrientation = Global.ORIENTATION_PORTRAIT_INVERTED;
                                }
                            } else { // orientation <135 && orientation > 45
                                if (Global.mOrientation != Global.ORIENTATION_LANDSCAPE_INVERTED) {
                                    Global.mOrientation = Global.ORIENTATION_LANDSCAPE_INVERTED;
                                }
                            }

                            if (lastOrientation != Global.mOrientation) {
                                changeRotation(Global.mOrientation);
                            }
                        }
                    }
                };
            }
            if (mOrientationEventListener.canDetectOrientation()) {
                mOrientationEventListener.enable();
            }
        } catch (Exception e) {

        }
    }

    private class BackToCameraClass implements BackToCamera {
        @Override
        public void back() {
            mDrawer.setSelection(homeItem, true);
            selectItemMenu(1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOrientationEventListener != null)
            mOrientationEventListener.disable();
    }

    int previousOrientation = 0, nextOrientation = 0;
    int typeOrientation = 0;

    private void changeRotation(int orientation) {
        if (fragment == null)
            return;
        switch (orientation) {
            case Global.ORIENTATION_PORTRAIT_NORMAL:
                if (typeOrientation == 3)
                    nextOrientation += 90;
                if (typeOrientation == 1)
                    nextOrientation -= 90;
                ((HomeFragment) fragment).changeOrientationBtn(previousOrientation, nextOrientation);
                menuBtnOrientation(previousOrientation, nextOrientation);
                typeOrientation = 0;
                previousOrientation = nextOrientation;
                break;
            case Global.ORIENTATION_LANDSCAPE_NORMAL:
                if (typeOrientation == 0)
                    nextOrientation += 90;
                if (typeOrientation == 2)
                    nextOrientation -= 90;
                ((HomeFragment) fragment).changeOrientationBtn(previousOrientation, nextOrientation);
                menuBtnOrientation(previousOrientation, nextOrientation);
                typeOrientation = 1;
                previousOrientation = nextOrientation;
                break;
            case Global.ORIENTATION_PORTRAIT_INVERTED:
                if (typeOrientation == 1)
                    nextOrientation += 90;
                if (typeOrientation == 3)
                    nextOrientation -= 90;
                ((HomeFragment) fragment).changeOrientationBtn(previousOrientation, nextOrientation);
                menuBtnOrientation(previousOrientation, nextOrientation);
                previousOrientation = nextOrientation;
                typeOrientation = 2;
                break;
            case Global.ORIENTATION_LANDSCAPE_INVERTED:
                if (typeOrientation == 2)
                    nextOrientation += 90;
                if (typeOrientation == 0)
                    nextOrientation -= 90;
                ((HomeFragment) fragment).changeOrientationBtn(previousOrientation, nextOrientation);
                menuBtnOrientation(previousOrientation, nextOrientation);
                previousOrientation = nextOrientation;
                typeOrientation = 3;
                break;
        }
    }

    public void menuBtnOrientation(int begin, int end) {
        ShapeRotation.rotate(begin, end, icon_menu_imgView);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
        } else {
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            new MaterialDialog.Builder(Global.context)
                    .title(R.string.app_name)
                    .titleColor(ViewExtras.getColor(this, R.color.colorMain))
                    .content(R.string.alert_exit)
                    .positiveText(R.string.close)
                    .positiveColor(ViewExtras.getColor(this, R.color.main_color))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            dialog.cancel();
                            Global.pipeType = null;
                            MainActivity.this.finish();
                        }
                    })
                    .negativeText(R.string.no)
                    .negativeColor(ViewExtras.getColor(this, R.color.main_color))
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            dialog.cancel();
                        }
                    })
                    .show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
