package com.visualogyx.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.expandablelayout.library.ExpandableLayout;
import com.appeaser.sublimepickerlibrary.SublimePicker;
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.testfairy.TestFairy;
import com.visualogyx.R;
import com.visualogyx.Utils.SublimePickerFragment;
import com.visualogyx.Utils.Utils;
import com.visualogyx.adapter.inspector.InspectorAsyncTask;
import com.visualogyx.adapter.inspector.InspectorFinishLoadAsyncTask;
import com.visualogyx.adapter.inspector.InspectorListViewAdapter;
import com.visualogyx.apirequest.ApiRequest;
import com.visualogyx.apirequest.account.AccountSuccessResponseModel;
import com.visualogyx.apirequest.inspection.InspectionSuccessResponseModel;
import com.visualogyx.apirequest.inspection.add.AddInspectionApiRequest;
import com.visualogyx.apirequest.inspection.add.AddInspectionModel;
import com.visualogyx.apirequest.inspector.GetAllInspectorApiRequest;
import com.visualogyx.manager.Global;
import com.visualogyx.manager.GoogleMapManager;
import com.visualogyx.manager.ViewExtras;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class NewRequestActivity extends AppCompatActivity {

    private ExpandableLayout imageExpandableLayout, inspectorExpandableLayout, windowExpandableLayout, locationExpandableLayout;
    private RelativeLayout addImageRL, removeImageRL;
    private ImageView imageView;
    private ListView inspectorLV;
    private TextView inspectorTV, fromDateValueTV, toDateValueTV;
    private ScrollView mainScrollView;
    private String requestType;
    private Bitmap currentBitmap;
    private boolean typeDate = false;
    private DateFormat mDateFormatter, mTimeFormatter;
    private SublimePicker mSublimePicker;
    private EditText requestNameEditText;
    private GoogleMapManager googleMapManager;
    private String startDate, endDate;
    private InspectionSuccessResponseModel inspectionSuccessResponseModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TestFairy.begin(this, getResources().getString(R.string.test_fairy_id));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_request);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        } else {
            requestType = extras.getString("RequestType");
            if (requestType.compareTo("CopyType") == 0)
                inspectionSuccessResponseModel = (InspectionSuccessResponseModel) getIntent().getSerializableExtra("InspectionSuccessResponseModel");
        }

        createActionBar();
        loadData();
    }

    private void createActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new_request);
        setSupportActionBar(toolbar);

        TextView titleBar = (TextView) toolbar.findViewById(R.id.title_text);
        if (requestType.compareTo("NewType") == 0)
            titleBar.setText("New Request");
        else if (requestType.compareTo("CopyType") == 0)
            titleBar.setText("Copy Request");
        else
            titleBar.setText("Edit Request");
        titleBar.setTypeface(null, Typeface.BOLD);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void loadData() {
        requestNameEditText = (EditText) findViewById(R.id.requestNameEditText);
        imageExpandableLayout = (ExpandableLayout) findViewById(R.id.imageExpandableLayout);
        inspectorExpandableLayout = (ExpandableLayout) findViewById(R.id.inspectorExpandableLayout);
        windowExpandableLayout = (ExpandableLayout) findViewById(R.id.windowExpandableLayout);
        locationExpandableLayout = (ExpandableLayout) findViewById(R.id.locationExpandableLayout);

        //add image
        removeImageRL = (RelativeLayout) findViewById(R.id.removeImageRL);
        addImageRL = (RelativeLayout) findViewById(R.id.addImageRL);
        addImageRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 1);
            }
        });

        removeImageRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeImageRL.setVisibility(View.GONE);
                addImageRL.setVisibility(View.VISIBLE);
                currentBitmap.recycle();
                imageExpandableLayout.hide();
                imageView.setImageBitmap(null);
            }
        });
        imageView = (ImageView) findViewById(R.id.imageView);

        //add inspector
        inspectorTV = (TextView) findViewById(R.id.inspectorTV);
        inspectorLV = (ListView) findViewById(R.id.inspectorLV);
        inspectorLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                AccountSuccessResponseModel accountSuccessResponseModel = (AccountSuccessResponseModel) view.getTag();
                inspectorTV.setText(accountSuccessResponseModel.user_name);
                inspectorTV.setTag(accountSuccessResponseModel.email);
                inspectorExpandableLayout.hide();
            }
        });

        Utils.showHubNoTitle(this);
        ApiRequest request = new GetAllInspectorApiRequest(this, new GetAllInspectorRequestResponse());
        request.execute();

        //add window
        toDateValueTV = (TextView) findViewById(R.id.toDateValueTV);
        toDateValueTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
                typeDate = false;
            }
        });

        fromDateValueTV = (TextView) findViewById(R.id.fromDateValueTV);
        fromDateValueTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
                typeDate = true;
            }
        });

        startDate = Utils.getCurrentDateFormatServer();
        fromDateValueTV.setText(Utils.getCurrentDate());
        endDate = Utils.getCurrentDateFormatServer();
        toDateValueTV.setText(Utils.getCurrentDate());

        //add location
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);

        mainScrollView = (ScrollView) findViewById(R.id.mainScrollView);

        ImageView transparentImageView = (ImageView) findViewById(R.id.transparent_image);
        transparentImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    case MotionEvent.ACTION_UP:
                        mainScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;
                    default:
                        return true;
                }
            }
        });

        if (requestType.compareTo("CopyType") == 0) {
            requestNameEditText.setText(inspectionSuccessResponseModel.name);
            inspectorTV.setText(inspectionSuccessResponseModel.inspector_name);
            inspectorTV.setTag(inspectionSuccessResponseModel.inspector_name);
            String[] startDateList = Utils.getDateList(inspectionSuccessResponseModel.start_date);
            String[] endDateList = Utils.getDateList(inspectionSuccessResponseModel.end_date);

            if (startDateList.length > 0) {
                fromDateValueTV.setText(startDateList[0] + "-" + startDateList[1] + "-" + startDateList[2]);
                startDate = startDateList[0] + startDateList[1] + startDateList[2];
            }
            if (endDateList.length > 0) {
                toDateValueTV.setText(endDateList[0] + "-" + endDateList[1] + "-" + endDateList[2]);
                endDate = endDateList[0] + endDateList[1] + endDateList[2];
            }

            if (inspectionSuccessResponseModel.image_data.compareTo("") != 0)
                Picasso.with(this).load(inspectionSuccessResponseModel.image_data).into(target);

            String[] locations = inspectionSuccessResponseModel.location.split(",");
            if (locations.length > 1) {
                googleMapManager = new GoogleMapManager(this, mapFragment, new LatLng(Double.parseDouble(locations[0]), Double.parseDouble(locations[1])));
                googleMapManager.checkInHistoryDetail = true;
                googleMapManager.selectLocation = true;
            } else
                googleMapManager = new GoogleMapManager(this, mapFragment, null);

        } else
            googleMapManager = new GoogleMapManager(this, mapFragment, null);
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            setImage(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };


    public class GetAllInspectorRequestResponse implements ApiRequest.RequestResponse {
        @Override
        public void requestComplete(boolean status, Object responseObject) {
            if (status) {
                ArrayList<AccountSuccessResponseModel> result = (ArrayList<AccountSuccessResponseModel>) responseObject;
                InspectorListViewAdapter adapter = new InspectorListViewAdapter(getApplicationContext());
                inspectorLV.setAdapter(adapter);
                InspectorAsyncTask inspectorAsyncTask = new InspectorAsyncTask(adapter, new FinishLoadInspector(), result);
                inspectorAsyncTask.execute();
            } else {
                Utils.showDisconnectNetworkMessage(NewRequestActivity.this, "Can't fetch  list of inspectors. Please try again!");
                Utils.hideHub();
            }

        }
    }

    SublimePickerFragment.Callback datePickerCallback = new SublimePickerFragment.Callback() {

        @Override
        public void onCancelled() {
        }

        @Override
        public void onDateTimeRecurrenceSet(SelectedDate selectedDate, int hourOfDay, int minute, SublimeRecurrencePicker.RecurrenceOption recurrenceOption, String recurrenceRule) {
            String day = String.valueOf(selectedDate.getStartDate().get(Calendar.DAY_OF_MONTH));
            String month = String.valueOf(selectedDate.getStartDate().get(Calendar.MONTH) + 1);
            String year = String.valueOf(selectedDate.getStartDate().get(Calendar.YEAR));
            String result = month + "-" + day + "-" + year;
            if (typeDate) {
                startDate = month + day + year;
                fromDateValueTV.setText(result);

            } else {
                endDate = month + day + year;
                toDateValueTV.setText(result);

            }
        }
    };

    private void showDatePicker() {
        SublimePickerFragment pickerFrag = new SublimePickerFragment();
        pickerFrag.setCallback(datePickerCallback);
        Pair<Boolean, SublimeOptions> optionsPair = getOptions();
        Bundle bundle = new Bundle();
        bundle.putParcelable("SUBLIME_OPTIONS", optionsPair.second);
        pickerFrag.setArguments(bundle);
        pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        pickerFrag.show(getSupportFragmentManager(), "SUBLIME_PICKER");
    }

    Pair<Boolean, SublimeOptions> getOptions() {
        SublimeOptions options = new SublimeOptions();
        int displayOptions = 0;
        displayOptions |= SublimeOptions.ACTIVATE_DATE_PICKER;
        options.setDisplayOptions(displayOptions);
        return new Pair<>(displayOptions != 0 ? Boolean.TRUE : Boolean.FALSE, options);
    }

    private class FinishLoadInspector implements InspectorFinishLoadAsyncTask {
        @Override
        public void result(ArrayList<AccountSuccessResponseModel> accountSuccessResponseModels) {
            Utils.setListViewHeightBasedOnChildren(inspectorLV);
            Utils.hideHub();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1 && resultCode == Activity.RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                Bitmap realBitmap = BitmapFactory.decodeFile(imgDecodableString);
                setImage(Utils.resize(realBitmap, 1024, 768));
            }
        } catch (Exception e) {
        }
    }

    private void setImage(Bitmap bitmap) {
        removeImageRL.setVisibility(View.VISIBLE);
        addImageRL.setVisibility(View.GONE);
        currentBitmap = bitmap;
        imageView.setImageBitmap(currentBitmap);
    }

    public void createBtnClicked(View view) {
        if (currentBitmap == null) {
            new MaterialDialog.Builder(NewRequestActivity.this)
                    .title("Error")
                    .titleColor(ViewExtras.getColor(this, R.color.colorMain))
                    .content("Please select an image first!")
                    .positiveText(R.string.ok)
                    .positiveColor(ViewExtras.getColor(this, R.color.main_color))
                    .show();
            return;
        }

        if (inspectorTV.getTag() == null) {
            new MaterialDialog.Builder(NewRequestActivity.this)
                    .title("Error")
                    .titleColor(ViewExtras.getColor(this, R.color.colorMain))
                    .content("Please select inspector!")
                    .positiveText(R.string.ok)
                    .positiveColor(ViewExtras.getColor(this, R.color.main_color))
                    .show();
            return;
        }

        if (googleMapManager.myLatLng == null) {
            new MaterialDialog.Builder(NewRequestActivity.this)
                    .title("Error")
                    .titleColor(ViewExtras.getColor(this, R.color.colorMain))
                    .content("Please select location!")
                    .positiveText(R.string.ok)
                    .positiveColor(ViewExtras.getColor(this, R.color.main_color))
                    .show();
            return;
        }

        String location = googleMapManager.myLatLng.latitude + "," + googleMapManager.myLatLng.longitude;
        String inspector = "";
        if (inspectorTV.getTag() != null)
            inspector = inspectorTV.getTag().toString();
        Utils.showHubNoTitle(this);
        ApiRequest apiRequest = new AddInspectionApiRequest(this, new AddInspectionRequestResponse());
        AccountSuccessResponseModel accountSuccessResponseModel = Global.settingsManager.getRegisterUser();
        AddInspectionModel addInspectionModel = new AddInspectionModel(requestNameEditText.getText().toString(), accountSuccessResponseModel.email, inspector,
                location, startDate, endDate, "test_file_name.jpg", Utils.base64FromBitMap(currentBitmap));
        apiRequest.setPostObject(addInspectionModel);
        apiRequest.execute();
    }

    public class AddInspectionRequestResponse implements ApiRequest.RequestResponse {
        @Override
        public void requestComplete(boolean status, Object responseObject) {
            if (status) {
                Intent intent = new Intent(NewRequestActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                NewRequestActivity.this.finish();
            } else {
                Utils.showDisconnectNetworkMessage(NewRequestActivity.this, "Cannot upload image. Please try again!");
            }
            Utils.hideHub();
        }
    }

    public void cancelBtnClicked(View view) {
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}