package com.visualogyx.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mikepenz.iconics.view.IconicsImageView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.visualogyx.R;
import com.visualogyx.Utils.DrawingPad;
import com.visualogyx.Utils.ShapeRotation;
import com.visualogyx.Utils.Utils;
import com.visualogyx.activity.MyNDK;
import com.visualogyx.activity.TotalAccountActivity;
import com.visualogyx.apirequest.ApiRequest;
import com.visualogyx.apirequest.account.AccountSuccessResponseModel;
import com.visualogyx.apirequest.processimage.ProcessImageApiRequest;
import com.visualogyx.apirequest.processimage.ProcessImageSuccessResponseModel;
import com.visualogyx.apirequest.training.TrainingModel;
import com.visualogyx.interfaceUI.FinishTakePhoto;
import com.visualogyx.manager.CameraPreview;
import com.visualogyx.manager.Global;
import com.visualogyx.manager.ImageViewTouchListener;
import com.visualogyx.manager.ViewExtras;
import com.visualogyx.model.ImageResultType;
import com.visualogyx.model.PipeType;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private CameraPreview cameraPreview;
    private ImageView galleryBtn, switchBtn;
    private IconicsImageView flashBtn;
    private ImageView settingBtn;
    private CircularImageView captureBtn, captureSmallBtn;

    //preview picture
    private RelativeLayout pictureContainView, captureRL, yellowSquareRL;
    private ImageView imageView, overLayImageView;

    private ImageView yellowSquareImageView;
    private boolean isTakingPicture = false;

    public static DrawingPad drawingPad;
    private ImageResultType imageResultType;

    //image processing
    private MyNDK imageProcessingNDK = new MyNDK();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        drawingPad = new DrawingPad();

        initToolBar();
        initHome(view);
        initOverlay(view);

        return view;
    }

    public void changeOrientationBtn(int begin, int end) {
        ShapeRotation.rotate(begin, end, galleryBtn);
        ShapeRotation.rotate(begin, end, flashBtn);
        ShapeRotation.rotate(begin, end, switchBtn);
    }

    private void initToolBar() {
        Toolbar toolbar_main = (Toolbar) getActivity().findViewById(R.id.toolbar_main);
        toolbar_main.setVisibility(View.GONE);
        Toolbar toolbar_home = (Toolbar) getActivity().findViewById(R.id.toolbar_home);
        toolbar_home.setVisibility(View.VISIBLE);
    }

    private void initCamera(View view) {
        cameraPreview = new CameraPreview(getContext());
        final FrameLayout camera_view = (FrameLayout) view.findViewById(R.id.camera_view);
        camera_view.addView(cameraPreview);
        cameraPreview.setFinishTakePhotoInterface(new FinishTakePhotoResponse());
        camera_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        PackageManager packageManager = getActivity().getPackageManager();
                        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
                            return false;
                        }
                        cameraPreview.focusCamera();
                        return true;
                }
                return false;
            }
        });

        setFlashMode();
    }

    private void initHome(View view) {
        galleryBtn = (ImageView) view.findViewById(R.id.galleryBtn);
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 1);

            }
        });

        captureBtn = (CircularImageView) view.findViewById(R.id.captureBtn);
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        captureSmallBtn = (CircularImageView) view.findViewById(R.id.captureSmallBtn);
        captureSmallBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (isTakingPicture)
                            return false;
                        isTakingPicture = true;
                        if (imageResultType != ImageResultType.Picked) {
                            Global.isRecordVideo = false;
                            counterTime = 0;
                            playTimer();
                        }
                        captureSmallBtn.setBorderColor(ViewExtras.getColor(getContext(), R.color.colorRed));
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (imageResultType != ImageResultType.Picked) {
                            if (timerHandler != null)
                                timerHandler.removeCallbacks(timerRunnable);
                            counterTime = 0;
                            if (Global.isRecordVideo) {
                                cameraPreview.stopRecordCamera();
                                imageResultType = ImageResultType.Recorded;
                                showPreviewPicture(null);
                            } else {
                                cameraPreview.take_photo();
                            }
                        } else {
                            sendImageToServer(Global.currentBitmap);
                            yellowSquareRL.setVisibility(View.INVISIBLE);
                            galleryBtn.setVisibility(View.INVISIBLE);
                            captureRL.setVisibility(View.INVISIBLE);
                        }
                        captureSmallBtn.setBorderColor(ViewExtras.getColor(getContext(), R.color.white));
                        captureBtn.setBorderColor(ViewExtras.getColor(getContext(), R.color.captureColor));
                        return true;
                }
                return false;
            }
        });

        flashBtn = (IconicsImageView) view.findViewById(R.id.flashBtn);
        flashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFlashMode();
            }
        });

        switchBtn = (ImageView) view.findViewById(R.id.switchBtn);
        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    cameraPreview.switchCamera();
                    changeFlashMode();
                } else {
                    Toast toast = Toast.makeText(getContext(), "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        initCamera(view);

        yellowSquareRL = (RelativeLayout) view.findViewById(R.id.yellowSquareRL);
        captureRL = (RelativeLayout) view.findViewById(R.id.captureRL);
        pictureContainView = (RelativeLayout) view.findViewById(R.id.pictureContainView);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        overLayImageView = (ImageView) view.findViewById(R.id.overLayImageView);
        overLayImageView.setOnTouchListener(drawingPad);

        settingBtn = (ImageView) view.findViewById(R.id.settingBtn);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TotalAccountActivity.class);
                intent.putExtra("pipeCount", drawingPad.pipes.size());
                startActivity(intent);

            }
        });
    }

    private void setFlashMode() {
        if (cameraPreview.hasFlash() && Global.settingsManager.getTypeCamera() == 0) {
            if (Global.settingsManager.getFlashMode().compareTo("off") == 0)
                turnOffFlashCamera();
            else if (Global.settingsManager.getFlashMode().compareTo("on") == 0)
                turnOnFlashCamera();
        } else
            turnOffFlashCamera();
    }

    private void changeFlashMode() {
        if (cameraPreview.hasFlash() && Global.settingsManager.getTypeCamera() == 0) {
            if (Global.settingsManager.getFlashMode().compareTo("off") == 0)
                turnOnFlashCamera();
            else if (Global.settingsManager.getFlashMode().compareTo("on") == 0)
                turnOffFlashCamera();
        } else
            turnOffFlashCamera();
    }

    private void turnOnFlashCamera() {
        flashBtn.setIcon("ion-ios-bolt");
        cameraPreview.setFlashMode("on");
        Global.settingsManager.setFlashMode("on");
    }

    private void turnOffFlashCamera() {
        flashBtn.setIcon("ion-flash-off");
        cameraPreview.setFlashMode("off");
        Global.settingsManager.setFlashMode("off");
    }

    public class FinishTakePhotoResponse implements FinishTakePhoto {
        @Override
        public void finish(Bitmap bitmap) {
            imageResultType = ImageResultType.Took;
            showPreviewPicture(bitmap);
        }

        @Override
        public void changeRotation() {

        }
    }

    private final static int LENGTH_EDGE = 300;

    private void sendImageToServer(Bitmap bitmap) {
        //image processing from server
        imageView.setVisibility(View.INVISIBLE);
        overLayImageView.setBackgroundColor(Color.BLACK);

        float[] f = new float[9];
        matrix.getValues(f);
        float scaleBitmap = f[Matrix.MSCALE_X];
        int olHeight = Math.round(LENGTH_EDGE * scaleBitmap);

        float scaleX = (float) bitmap.getWidth() / yellowSquareImageView.getWidth();
        float scaleY = (float) bitmap.getHeight() / yellowSquareImageView.getHeight();
        float scale = Math.min(scaleX, scaleY);

        int tag_description = Math.round((int) (olHeight * scale));

        Utils.showHubNoTitle(getContext());
        ApiRequest apiRequest = new ProcessImageApiRequest(getContext(), new SendProcessImageRequestResponse());
        AccountSuccessResponseModel accountSuccessResponseModel = Global.settingsManager.getRegisterUser();
        TrainingModel trainingModel = new TrainingModel(accountSuccessResponseModel.email, String.valueOf(tag_description), "imageName.jpg", Utils.base64FromBitMap(bitmap));
        apiRequest.setPostObject(trainingModel);
        apiRequest.execute();

        //image processing from local
//        imageView.setVisibility(View.VISIBLE);
//        overLayImageView.setBackgroundColor(Color.TRANSPARENT);
//        try {
//            String outPut = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "VISUALOGYX/VISUALOGYX" + Global.getCurrentDateAndTime() + ".png";
//            imageProcessingNDK.processImage(Utils.saveImage(Global.currentBitmap), outPut);
//            Bitmap result = BitmapFactory.decodeStream(new FileInputStream(new File(outPut)));
//            Global.currentBitmap = result;
//            imageView.setImageBitmap(result);
//        } catch (Exception e) {
//        }
//        if (imageResultType == ImageResultType.Took)
//            Utils.animateImageView(imageView);
//        settingBtn.setVisibility(View.VISIBLE);
    }

    public class SendProcessImageRequestResponse implements ApiRequest.RequestResponse {
        @Override
        public void requestComplete(boolean status, Object responseObject) {
            if (status) {
                ArrayList<ProcessImageSuccessResponseModel> processImageSuccessResponseModels = (ArrayList<ProcessImageSuccessResponseModel>) responseObject;
                for (int i = 0; i < processImageSuccessResponseModels.size(); i++) {
                    ProcessImageSuccessResponseModel processImageSuccessResponseModel = processImageSuccessResponseModels.get(i);
                    int centerX = Integer.parseInt(processImageSuccessResponseModel.centre_x);
                    int centerY = Integer.parseInt(processImageSuccessResponseModel.centre_y);
                    int radius = Integer.parseInt(processImageSuccessResponseModel.radius);
                    int id = Integer.parseInt(processImageSuccessResponseModel.id);
                    drawingPad.type = PipeType.Detected;
                    drawingPad.createPipes(centerX, centerY, radius, id);
                }
                if (processImageSuccessResponseModels.size() > 0) {
                    drawingPad.overlayImageView = overLayImageView;
                    drawingPad.drawOverlay();
                    drawingPad.type = null;
                }
                imageView.setVisibility(View.VISIBLE);
                overLayImageView.setBackgroundColor(Color.TRANSPARENT);
                if (imageResultType == ImageResultType.Took)
                    Utils.animateImageView(imageView);
                settingBtn.setVisibility(View.VISIBLE);
            } else {
                Utils.showDisconnectNetworkMessage(getContext(), "Cannot upload image. Please try again!");
            }
            Utils.hideHub();
        }
    }

    private void showPreviewPicture(Bitmap resultImage) {
        hideSomethingBeforeShowingPic();
        if (imageResultType == ImageResultType.Recorded) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.panorama);
            drawingPad.backgroundImage = bitmap;
            Global.currentBitmap = bitmap;
        } else {
            Global.currentBitmap = resultImage;
            drawingPad.backgroundImage = resultImage;
        }

        Global.currentBitmap = Utils.resize(Global.currentBitmap, 1024, 768);

        imageView.setImageBitmap(Global.currentBitmap);
        if (imageResultType != ImageResultType.Picked) {
            sendImageToServer(Global.currentBitmap);
        }
    }

    private void hideSomethingBeforeShowingPic() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        Global.isCanRotateOrientation = true;
        pictureContainView.setVisibility(View.VISIBLE);

        if (imageResultType != ImageResultType.Picked) {
            yellowSquareRL.setVisibility(View.INVISIBLE);
            galleryBtn.setVisibility(View.INVISIBLE);
            captureRL.setVisibility(View.INVISIBLE);
        }
        settingBtn.setVisibility(View.INVISIBLE);
        flashBtn.setVisibility(View.INVISIBLE);
        switchBtn.setVisibility(View.INVISIBLE);
    }

    private int counterTime = 0;
    private Handler timerHandler;
    private Runnable timerRunnable;

    public void playTimer() {
        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (counterTime > 300) {
                    captureBtn.setBorderColor(ViewExtras.getColor(getContext(), R.color.colorRed));
                    Global.isRecordVideo = true;
                    cameraPreview.startRecordCamera();
                    timerHandler.removeCallbacks(timerRunnable);
                } else {
                    counterTime += 100;
                    timerHandler.postDelayed(this, 100);
                }
            }
        };
        timerHandler.postDelayed(timerRunnable, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1 && resultCode == Activity.RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                Bitmap currentBitmap = BitmapFactory.decodeFile(imgDecodableString);
                imageResultType = ImageResultType.Picked;
                showPreviewPicture(currentBitmap);
            }
        } catch (Exception e) {
        }
    }

    private Canvas canvasSquare;
    private Matrix matrix;

    private void initOverlay(View view) {
        int squareSize = 300;
        final Bitmap bitmap = Bitmap.createBitmap(squareSize, squareSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);

        Rect rectangle = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.drawRect(rectangle, paint);
        yellowSquareImageView = (ImageView) view.findViewById(R.id.yellowSquareImageView);
        yellowSquareImageView.setImageBitmap(bitmap);
        ImageViewTouchListener imageViewTouchListener = new ImageViewTouchListener();
        yellowSquareImageView.setOnTouchListener(imageViewTouchListener);
        int x = (Global.screenWidth - bitmap.getWidth()) / 2;
        int y = (Global.screenHeight - bitmap.getHeight()) / 2;
        matrix = new Matrix();
        matrix.postTranslate(x, y);
        yellowSquareImageView.setImageMatrix(matrix);
        imageViewTouchListener.matrix = matrix;
        canvasSquare = canvas;
    }

    public void drawPipe(PipeType pipeType) {
        drawingPad.type = pipeType;
    }
}

