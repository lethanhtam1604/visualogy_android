package com.visualogyx.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.visualogyx.Utils.Utils;
import com.visualogyx.interfaceUI.FinishTakePhoto;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.Size previewSize;
    private FinishTakePhoto finishTakePhoto;

    //record video
    private MediaRecorder mMediaRecorder;

    public CameraPreview(Context context) {
        super(context);

        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        if (!Global.isCanRotateOrientation)
            initCamera();
    }

    public void setFinishTakePhotoInterface(FinishTakePhoto finishTakePhoto) {
        this.finishTakePhoto = finishTakePhoto;
    }

    private void initCamera() {
        mCamera = openCamera();
        setParametersCamera();
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (IOException e) {
        }
    }

    private void initRecordCamera() {
        mCamera.unlock();

//        if (mMediaRecorder == null)
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setCamera(mCamera);

//        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
//        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

//        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
//        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
        mMediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());

//        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//        mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
//        mMediaRecorder.setVideoFrameRate(30);
//        mMediaRecorder.setVideoSize(1280, 720);

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "VISUALOGYX/VISUALOGYX" + Global.getCurrentDateAndTime() + ".mp4";
        Global.currentPathVideo = path;
        mMediaRecorder.setOutputFile(path);
        if (Global.settingsManager.getTypeCamera() == Camera.CameraInfo.CAMERA_FACING_BACK)
            mMediaRecorder.setOrientationHint(90);
        else
            mMediaRecorder.setOrientationHint(270);

        try {
            mMediaRecorder.prepare();
        } catch (Exception e) {
        }

    }

    public void startRecordCamera() {
        initRecordCamera();
        mMediaRecorder.start();
    }

    public void stopRecordCamera() {
        try {
            if (mMediaRecorder != null) {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
            }
        } catch (Exception e) {
        }
    }

    private void shutDownRecordCamera() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    private Camera openCamera() {
        int cameraID = Global.settingsManager.getTypeCamera();
        return Camera.open(cameraID);
    }

    private void setParametersCamera() {
        Camera.Parameters params = mCamera.getParameters();
        previewSize = getBestPreviewSize(Global.screenHeight, Global.screenWidth, params);
        params.setPreviewSize(previewSize.width, previewSize.height);

        Camera.Size pictureSize = getSmallestPictureSize(params);
        params.setPictureSize(pictureSize.width, pictureSize.height);

        setFocusable(true);
        setFocusableInTouchMode(true);

        if (Global.settingsManager.getTypeCamera() == Camera.CameraInfo.CAMERA_FACING_BACK)
            params.setRotation(0);
        else
            params.setRotation(270);

        mCamera.setDisplayOrientation(90);
        try {
            mCamera.setParameters(params);
        } catch (Exception e) {
        }
    }

    public void switchCamera() {
        if (Global.settingsManager.getTypeCamera() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            Global.settingsManager.setTypeCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        } else {
            Global.settingsManager.setTypeCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }

        resetCamera();
    }

    public void resetCamera() {
        releaseCamera();
        initCamera();

        try {

            mCamera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    public void focusCamera() {
        if (mCamera != null)
            mCamera.autoFocus(null);
    }

    SurfaceHolder surfaceHolder;

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            if (mCamera == null) {
                initCamera();
            }

            this.surfaceHolder = surfaceHolder;
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();

        } catch (Exception e) {
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        if (!Global.isRecordVideo) {
            if (mHolder.getSurface() == null)
                return;

            try {
                mCamera.stopPreview();
            } catch (Exception e) {
            }

            try {
                this.surfaceHolder = surfaceHolder;
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (IOException e) {
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        destroyCamera();
    }

    public void destroyCamera() {
        releaseCamera();
        shutDownRecordCamera();
    }

    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }

        return (result);
    }

    private Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
        Camera.Size result = null;
        List<Camera.Size> sizeList = parameters.getSupportedPictureSizes();



        for (Camera.Size size : parameters.getSupportedPictureSizes()) {
            if (result == null) {
                result = size;
            } else {
                int resultArea = result.width * result.height;
                int newArea = size.width * size.height;

//                if(resultArea < newArea)
//                    return sizeList.get(2);
//                else
                    return sizeList.get(sizeList.size()/2 - 1);

//                if (newArea < resultArea) {
//                    result = size;
//                }
            }
        }

        return (result);
    }

    private Camera.Size getBestPictureSize(Camera.Parameters params) {
        List<Camera.Size> sizeList = params.getSupportedPictureSizes();
        Camera.Size result = sizeList.get(0);
        for (Camera.Size size : sizeList) {
            float ratio = (float) size.height / size.width;
            if (ratio == Global.screenRatio) {
                if (result.width < size.width)
                    result = size;
            }
        }
        return result;
    }

    private Camera.Size getBestPreviewSize(Camera.Parameters params) {

        List<Camera.Size> sizeList = params.getSupportedPreviewSizes();
        int i = 0;
        List<Camera.Size> sizeList2 = new ArrayList<>();
        for (int j = 0; j < sizeList.size(); j++) {
            Camera.Size size = sizeList.get(j);
            float ratio = (float) size.height / size.width;
            if (ratio == Global.screenRatio) {
                sizeList2.add(size);
            }
        }
        if (sizeList2.size() > 1)
            return sizeList2.get(1);
        else
            return sizeList2.get(0);
    }

    public void take_photo() {
        AudioManager mgr = (AudioManager) Global.context.getSystemService(Context.AUDIO_SERVICE);
        mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);

    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
        }
    };

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            resetCamera();
            Bitmap bitmap = getBitmapFromSurface(data);
            if (finishTakePhoto != null)
                finishTakePhoto.finish(bitmap);
        }
    };

    public Bitmap getBitmapFromSurface(byte[] data) {
        /*Bitmap bmp = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        if (Global.settingsManager.getTypeCamera() == Camera.CameraInfo.CAMERA_FACING_BACK)
            options.inSampleSize = 2;
        bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        Matrix matrix = new Matrix();

        if (bmp.getWidth() > bmp.getHeight()) {
            if (Global.settingsManager.getTypeCamera() == Camera.CameraInfo.CAMERA_FACING_BACK) {
                matrix.postRotate(90);
                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            } else {
                matrix.setScale(-1, 1);
                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                matrix = new Matrix();
                matrix.postRotate(90);
                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            }
        }

        if ((Global.settingsManager.getTypeCamera() == Camera.CameraInfo.CAMERA_FACING_FRONT)) {
            matrix.setScale(-1, 1);
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        }

        return bmp;*/

        try {
            Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length);

            switch (Global.mOrientation) {
                case Global.ORIENTATION_PORTRAIT_NORMAL:
                    if (Global.settingsManager.getTypeCamera() == Camera.CameraInfo.CAMERA_FACING_FRONT)
                        realImage = rotate(realImage, -90);
                    else
                        realImage = rotate(realImage, 90);
                    break;
                case Global.ORIENTATION_LANDSCAPE_NORMAL:
                    if (Global.settingsManager.getTypeCamera() == Camera.CameraInfo.CAMERA_FACING_FRONT)
                        realImage = rotate(realImage, 0);
                    else
                        realImage = rotate(realImage, 0);
                    break;
                case Global.ORIENTATION_PORTRAIT_INVERTED:
                    if (Global.settingsManager.getTypeCamera() == Camera.CameraInfo.CAMERA_FACING_FRONT)
                        realImage = rotate(realImage, -270);
                    else
                        realImage = rotate(realImage, 270);
                    break;
                case Global.ORIENTATION_LANDSCAPE_INVERTED:
                    if (Global.settingsManager.getTypeCamera() == Camera.CameraInfo.CAMERA_FACING_FRONT)
                        realImage = rotate(realImage, -180);
                    else
                        realImage = rotate(realImage, 180);
                    break;
            }

            if ((Global.settingsManager.getTypeCamera() == Camera.CameraInfo.CAMERA_FACING_FRONT)) {
                Matrix matrix = new Matrix();
                matrix.setScale(-1, 1);
                realImage = Bitmap.createBitmap(realImage, 0, 0, realImage.getWidth(), realImage.getHeight(), matrix, true);
            }

            realImage = Utils.cropImage(realImage, Global.screenWidth, Global.screenHeight);

            realImage = Utils.resize(realImage, 1024, 768);


//            try {
//
//                Global.currentPicturePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "VISUALOGYX/VISUALOGYX" + Global.getCurrentDateAndTime() + ".png";
//                File pictureFile = new File(Global.currentPicturePath);
//                FileOutputStream outStream = new FileOutputStream(pictureFile);
//                realImage.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
////                realImage.recycle();
//                outStream.flush();
//                outStream.close();
//                Toast.makeText(Global.context, Global.currentPicturePath + " saved", Toast.LENGTH_LONG).show();
//
//            } catch (Exception e) {
//            }

            return realImage;

        } catch (Exception e) {

        }
        return null;
    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.setRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    public void setFlashMode(String m) {
        if (mCamera == null)
            return;

        Camera.Parameters params = mCamera.getParameters();
        switch (m) {
            case "on":
                params.setFlashMode("on");
                break;
            case "off":
                params.setFlashMode("off");
                break;
            case "auto":
                params.setFlashMode("auto");
                break;
        }
        mCamera.setParameters(params);
    }

    public boolean hasFlash() {
        if (mCamera == null)
            return false;
        Camera.Parameters params = mCamera.getParameters();
        List<String> flashModes = params.getSupportedFlashModes();
        if (flashModes == null) {
            return false;
        }

        for (String flashMode : flashModes) {
            if (Camera.Parameters.FLASH_MODE_ON.equals(flashMode)) {
                return true;
            }
        }
        return false;
    }
}