package com.visualogyx.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.visualogyx.R;
import com.visualogyx.Utils.DrawingPad;
import com.visualogyx.Utils.Utils;
import com.visualogyx.apirequest.ApiRequest;
import com.visualogyx.apirequest.account.AccountSuccessResponseModel;
import com.visualogyx.apirequest.training.TrainingApiRequest;
import com.visualogyx.apirequest.training.TrainingModel;
import com.visualogyx.manager.Global;
import com.visualogyx.manager.ViewExtras;
import com.visualogyx.model.BackupShape;
import com.visualogyx.model.PipeType;

import java.util.ArrayList;

public class TrainingFragment extends Fragment {
    private final int SELECT_PICTURE = 100;
    private final int DURATION_SHOW_ANIMATON = 150;
    private final int DURATION_HIDE_ANIMATON = 100;
    private int HEIGHT_BUTTON = 78;
    private final int MARGIN_TOP = 30;
    private int LENGTH_EDGE_SQUARE = 100;
    private int LENGTH_EDGE_TRIANGLE = 100;
    private int LENGTH_EDGE_CIRCLE = 100;
    private final int SPEED_MOVING = 1;
    private boolean imageLoaded = false;
    ArrayList<BackupShape> listShape = new ArrayList<>();
    String typeShape = "Square";
    int colorShape = Color.BLUE;
    boolean shapesToolsShowing = false, brushToolsShowing = false;
    int checkMove = 0;
    ImageView ivPhoto, ivPhotoTemp, conCac;
    TextView tvTools, tvAddImage, tvEditor;
    EditText etEditor;
    ScrollView scrollView;
    Button btnChoosePhoto, btnAddShape, btnBrush, btnSquare, btnTriangle, btnCircle, btnBlue, btnGreen, btnRed, btnText, btnCrop, btnDone;
    RelativeLayout rlSquare, rlAddShape, rlBrush, rlTriangle, rlCircle, rlBlue, rlGreen, rlRed;

    Bitmap oldBitmap, originBitmap;
    Point oldPoint = new Point(), downPoint = new Point(), oldPointSaved = new Point();
    private DrawingPad drawingPad = new DrawingPad();
    Bitmap currentBitmap;
    private boolean isChooseDescription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training, container, false);

        Global.isCanRotateOrientation = true;

        initToolBar();
        initHome(view);
        return view;
    }

    private void initToolBar() {
        Toolbar toolbar_home = (Toolbar) getActivity().findViewById(R.id.toolbar_home);
        toolbar_home.setVisibility(View.GONE);
        Toolbar toolbar_main = (Toolbar) getActivity().findViewById(R.id.toolbar_main);
        toolbar_main.setVisibility(View.VISIBLE);

        TextView title_text = (TextView) toolbar_main.findViewById(R.id.title_text);
        title_text.setText("Training Mode");

        ImageView plusBtn = (ImageView) toolbar_main.findViewById(R.id.plusBtn);
        plusBtn.setVisibility(View.GONE);
    }

    private void initHome(View view) {
        LENGTH_EDGE_SQUARE = dpToPx(LENGTH_EDGE_SQUARE);
        LENGTH_EDGE_TRIANGLE = dpToPx(LENGTH_EDGE_TRIANGLE);
        LENGTH_EDGE_CIRCLE = dpToPx(LENGTH_EDGE_CIRCLE);
        tvTools = (TextView) view.findViewById(R.id.tvTools);
        tvAddImage = (TextView) view.findViewById(R.id.tvAddImage);
        btnChoosePhoto = (Button) view.findViewById(R.id.btnChoosePhoto);
        btnAddShape = (Button) view.findViewById(R.id.btnAddShape);
        btnBrush = (Button) view.findViewById(R.id.btnBrush);
        btnSquare = (Button) view.findViewById(R.id.btnSquare);
        btnTriangle = (Button) view.findViewById(R.id.btnTriangle);
        btnCircle = (Button) view.findViewById(R.id.btnCircle);
        btnBlue = (Button) view.findViewById(R.id.btnBlue);
        btnGreen = (Button) view.findViewById(R.id.btnGreen);
        btnRed = (Button) view.findViewById(R.id.btnRed);
        btnText = (Button) view.findViewById(R.id.btnText);
        btnCrop = (Button) view.findViewById(R.id.btnCrop);
        rlSquare = (RelativeLayout) view.findViewById(R.id.rlSquare);
        rlAddShape = (RelativeLayout) view.findViewById(R.id.rlAddShape);
        rlBrush = (RelativeLayout) view.findViewById(R.id.rlBrush);
        rlTriangle = (RelativeLayout) view.findViewById(R.id.rlTriangle);
        rlCircle = (RelativeLayout) view.findViewById(R.id.rlCircle);
        rlBlue = (RelativeLayout) view.findViewById(R.id.rlBlue);
        rlGreen = (RelativeLayout) view.findViewById(R.id.rlGreen);
        rlRed = (RelativeLayout) view.findViewById(R.id.rlRed);
        btnDone = (Button) view.findViewById(R.id.btnDone);
        ivPhoto = (ImageView) view.findViewById(R.id.ivPhoto);
        ivPhotoTemp = (ImageView) view.findViewById(R.id.ivPhotoTemp);
        ivPhotoTemp.setOnTouchListener(drawingPad);

        etEditor = (EditText) view.findViewById(R.id.etEditor);
        tvEditor = (TextView) view.findViewById(R.id.tvEditor);
        originBitmap = ((BitmapDrawable) ivPhotoTemp.getDrawable()).getBitmap();
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        conCac = (ImageView) view.findViewById(R.id.conCac);

        if (Global.sendForTraining) {
            Global.sendForTraining = false;
            ivPhoto.setImageBitmap(Global.currentBitmap);
            btnChoosePhoto.setVisibility(View.INVISIBLE);
            tvAddImage.setVisibility(View.INVISIBLE);
            HEIGHT_BUTTON = pxToDp(conCac.getHeight());
            imageLoaded = true;
            drawingPad.backgroundImage = Global.currentBitmap;
            currentBitmap = Global.currentBitmap;
        }
        drawingPad.scrollView = scrollView;
        drawingPad.type = PipeType.Square;
        drawingPad.isTraining = true;
        drawingPad.isColor = true;
        drawingPad.color = Color.GRAY;
        drawingPad.overlayImageView = ivPhotoTemp;

        btnChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 1);
            }
        });
        btnAddShape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageLoaded == false)
                    return;
                if (brushToolsShowing) {
                    brushToolHide();
                    brushToolsShowing = false;
                }
                if (shapesToolsShowing == false) {
                    shapesToolShow();
                    shapesToolsShowing = true;
                } else {
                    shapesToolHide();
                    shapesToolsShowing = false;
                }
            }
        });

        btnBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageLoaded == false)
                    return;
                if (shapesToolsShowing) {
                    shapesToolHide();
                    shapesToolsShowing = false;
                }
                if (brushToolsShowing == false) {
                    brushToolShow();
                    brushToolsShowing = true;
                } else {
                    brushToolHide();
                    brushToolsShowing = false;
                }
            }
        });
        tvTools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideTools();
            }
        });
        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageLoaded == false || isChooseDescription)
                    return;
                if (!isChooseDescription) {
                    isChooseDescription = true;
                    etEditor.setText("");
                    btnText.setAlpha(0.7f);
                }
                hideTools();
                etEditor.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                imm.showSoftInput(etEditor, InputMethodManager.SHOW_IMPLICIT);
                tvEditor.setVisibility(View.INVISIBLE);
            }
        });
        btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageLoaded == false)
                    return;
                hideTools();
            }
        });

        btnSquare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeShape = "Square";
                drawingPad.type = PipeType.Square;
                hideTools();
            }
        });
        btnTriangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeShape = "Triangle";
                drawingPad.type = PipeType.Triangle;
                hideTools();
            }
        });
        btnCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeShape = "Circle";
                drawingPad.type = PipeType.Circle;
                hideTools();
            }
        });
        btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorShape = Color.BLUE;
                drawingPad.color = Color.BLUE;
                drawingPad.drawOverlay();
                hideTools();
            }
        });
        btnGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorShape = Color.GREEN;
                drawingPad.color = Color.GREEN;
                drawingPad.drawOverlay();
                hideTools();
            }
        });
        btnRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorShape = Color.RED;
                drawingPad.color = Color.RED;
                drawingPad.drawOverlay();
                hideTools();
            }
        });


        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isChooseDescription || etEditor.getText().toString().compareTo("") == 0) {
                    new MaterialDialog.Builder(getContext())
                            .title("Error")
                            .titleColor(ViewExtras.getColor(getContext(), R.color.colorMain))
                            .content("Please input description text!")
                            .positiveText(R.string.ok)
                            .positiveColor(ViewExtras.getColor(getContext(), R.color.main_color))
                            .show();
                    return;
                }

                if (imageLoaded == false) {
                    new MaterialDialog.Builder(getContext())
                            .title("Error")
                            .titleColor(ViewExtras.getColor(getContext(), R.color.colorMain))
                            .content("Please select an image first!")
                            .positiveText(R.string.ok)
                            .positiveColor(ViewExtras.getColor(getContext(), R.color.main_color))
                            .show();
                    return;
                }

                Utils.showHubNoTitle(getContext());
                ApiRequest apiRequest = new TrainingApiRequest(getContext(), new SendTrainingRequestResponse());
                AccountSuccessResponseModel accountSuccessResponseModel = Global.settingsManager.getRegisterUser();
                TrainingModel trainingModel = new TrainingModel(accountSuccessResponseModel.email, etEditor.getText().toString(), "testfile.jpg", Utils.base64FromBitMap(drawingPad.drawPipesTraining()));
                apiRequest.setPostObject(trainingModel);
                apiRequest.execute();
            }
        });
    }

    public class SendTrainingRequestResponse implements ApiRequest.RequestResponse {
        @Override
        public void requestComplete(boolean status, Object responseObject) {
            if (status) {
                if (Global.backToCamera != null)
                    Global.backToCamera.back();
            } else {
                Utils.showDisconnectNetworkMessage(getContext(), "Cannot upload image. Please try again!");
            }
            Utils.hideHub();
        }
    }

    private void drawShape(int moveX, int moveY, Bitmap myBitmap) {
        switch (typeShape) {
            case "Square":
                drawSquare(moveX, moveY, myBitmap);
                break;
            case "Triangle":
                drawTriangle(moveX, moveY, myBitmap);
                break;
            case "Circle":
                drawCircle(moveX, moveY, myBitmap);
                break;
        }
        listShape.add(new BackupShape(moveX, moveY, typeShape));
    }

    private void changeColor() {
        drawBackupShape(listShape.get(0).x, listShape.get(0).y, originBitmap, listShape.get(0).name);
        Bitmap myBitmap = ((BitmapDrawable) ivPhotoTemp.getDrawable()).getBitmap();
        for (int i = 1; i < listShape.size(); i++) {
            drawBackupShape(listShape.get(i).x, listShape.get(i).y, myBitmap, listShape.get(i).name);
            myBitmap = ((BitmapDrawable) ivPhotoTemp.getDrawable()).getBitmap();
        }
    }

    private void drawBackupShape(int moveX, int moveY, Bitmap myBitmap, String shape) {
        switch (shape) {
            case "Square":
                drawSquare(moveX, moveY, myBitmap);
                break;
            case "Triangle":
                drawTriangle(moveX, moveY, myBitmap);
                break;
            case "Circle":
                drawCircle(moveX, moveY, myBitmap);
                break;
        }
    }

    private void drawSquare(int eventX, int eventY, Bitmap myBitmap) {
        int x = pxToDp(eventX);
        int y = pxToDp(eventY);
        oldPoint.x = eventX;
        oldPoint.y = eventY;
        oldBitmap = myBitmap;
        Paint myPaint = new Paint();
        myPaint.setAntiAlias(true);
        myPaint.setColor(colorShape);
        int x1 = dpToPx(x) - LENGTH_EDGE_SQUARE / 2;
        int y1 = dpToPx(y) - LENGTH_EDGE_SQUARE / 2;
        int x2 = x1 + LENGTH_EDGE_SQUARE;
        int y2 = y1 + LENGTH_EDGE_SQUARE;
        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(myBitmap, 0, 0, null);
        tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myPaint);
        ivPhotoTemp.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
    }

    private void drawTriangle(int eventX, int eventY, Bitmap myBitmap) {
        int x = pxToDp(eventX);
        int y = pxToDp(eventY) + dpToPx(138);
        oldPoint.x = eventX;
        oldPoint.y = eventY;
        oldBitmap = myBitmap;
        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(tempBitmap);
        Paint paint = new Paint();
        paint.setColor(colorShape);
        Point a = new Point(x, y - (int) (LENGTH_EDGE_TRIANGLE * 1.732 / 3));
        Point b = new Point(x - LENGTH_EDGE_TRIANGLE / 2, y + (int) (LENGTH_EDGE_TRIANGLE * 1.732 / 6));
        Point c = new Point(x + LENGTH_EDGE_TRIANGLE / 2, y + (int) (LENGTH_EDGE_TRIANGLE * 1.732 / 6));

        Path path = new Path();
        path.moveTo(a.x, a.y);
        path.lineTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.lineTo(a.x, a.y);
        path.close();
        canvas.drawBitmap(myBitmap, 0, 0, null);
        canvas.drawPath(path, paint);

        ivPhotoTemp.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
    }

    private void drawCircle(int eventX, int eventY, Bitmap myBitmap) {
        int x = pxToDp(eventX);
        int y = pxToDp(eventY);
        oldPoint.x = eventX;
        oldPoint.y = eventY;
        oldBitmap = myBitmap;

        Paint myPaint = new Paint();
        myPaint.setAntiAlias(true);
        myPaint.setColor(colorShape);
        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(myBitmap, 0, 0, null);
        tempCanvas.drawCircle(x, y + dpToPx(138), LENGTH_EDGE_CIRCLE / 2, myPaint);

        ivPhotoTemp.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
    }

    private void shapesToolShow() {
        if (HEIGHT_BUTTON == 0)
            HEIGHT_BUTTON = pxToDp(conCac.getHeight());

        int w = rlAddShape.getWidth();
        ViewGroup.LayoutParams lp = rlSquare.getLayoutParams();
        lp.width = w;
        rlSquare.setLayoutParams(lp);
        rlCircle.setLayoutParams(lp);
        rlTriangle.setLayoutParams(lp);

        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -dpToPx(HEIGHT_BUTTON));
        animation.setDuration(DURATION_SHOW_ANIMATON);
        animation.setFillAfter(false);
        animation.setAnimationListener(new AnimationShowLevelOne1());
        rlSquare.startAnimation(animation);
    }

    private void shapesToolHide() {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, dpToPx(HEIGHT_BUTTON));
        animation.setDuration(DURATION_HIDE_ANIMATON);
        animation.setFillAfter(false);
        animation.setAnimationListener(new AnimationHideLevelOne1());
        rlCircle.startAnimation(animation);
    }

    private void brushToolShow() {
        if (HEIGHT_BUTTON == 0)
            HEIGHT_BUTTON = pxToDp(conCac.getHeight());

        int w = rlBrush.getWidth();
        ViewGroup.LayoutParams lp = rlBlue.getLayoutParams();
        lp.width = w;
        rlBlue.setLayoutParams(lp);
        lp = rlGreen.getLayoutParams();
        lp.width = w;
        rlGreen.setLayoutParams(lp);
        lp = rlRed.getLayoutParams();
        lp.width = w;
        rlRed.setLayoutParams(lp);
        //rlGreen.setLayoutParams(lp);
        //rlRed.setLayoutParams(lp);


        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -dpToPx(HEIGHT_BUTTON));
        animation.setDuration(DURATION_SHOW_ANIMATON);
        animation.setFillAfter(false);
        animation.setAnimationListener(new AnimationShowLevelOne2());
        rlBlue.startAnimation(animation);
    }

    private void brushToolHide() {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, dpToPx(HEIGHT_BUTTON));
        animation.setDuration(DURATION_HIDE_ANIMATON);
        animation.setFillAfter(false);
        animation.setAnimationListener(new AnimationHideLevelOne2());
        rlRed.startAnimation(animation);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1 && resultCode == Activity.RESULT_OK && null != data) {
                btnChoosePhoto.setVisibility(View.INVISIBLE);
                tvAddImage.setVisibility(View.INVISIBLE);

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                Bitmap realBitmap = BitmapFactory.decodeFile(imgDecodableString);
                currentBitmap = realBitmap;
                ivPhoto.setImageBitmap(currentBitmap);
                drawingPad.backgroundImage = currentBitmap;
                HEIGHT_BUTTON = pxToDp(conCac.getHeight());
                imageLoaded = true;
            }
        } catch (Exception e) {
        }
    }

    private class AnimationShowLevelOne1 implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            rlSquare.clearAnimation();
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(rlSquare.getWidth(), rlSquare.getHeight());
            lp.setMargins(0, dpToPx(HEIGHT_BUTTON * 2 + MARGIN_TOP), 0, 0);
            rlSquare.setLayoutParams(lp);
            rlTriangle.setLayoutParams(lp);
            rlCircle.setLayoutParams(lp);

            TranslateAnimation animation2 = new TranslateAnimation(0, 0, 0, -dpToPx(HEIGHT_BUTTON));
            animation2.setDuration(DURATION_SHOW_ANIMATON);
            animation2.setFillAfter(false);
            animation2.setAnimationListener(new AnimationShowLevelTwo1());
            rlTriangle.startAnimation(animation2);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }
    }

    private class AnimationShowLevelTwo1 implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            rlTriangle.clearAnimation();
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(rlTriangle.getWidth(), rlTriangle.getHeight());
            lp.setMargins(0, dpToPx(HEIGHT_BUTTON + MARGIN_TOP), 0, 0);
            rlTriangle.setLayoutParams(lp);
            rlCircle.setLayoutParams(lp);

            TranslateAnimation animation2 = new TranslateAnimation(0, 0, 0, -dpToPx(HEIGHT_BUTTON));
            animation2.setDuration(DURATION_SHOW_ANIMATON);
            animation2.setFillAfter(false);
            animation2.setAnimationListener(new AnimationShowLevelThree1());
            rlCircle.startAnimation(animation2);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }
    }

    private class AnimationShowLevelThree1 implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            rlCircle.clearAnimation();
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(rlCircle.getWidth(), rlCircle.getHeight());
            lp.setMargins(0, dpToPx(MARGIN_TOP), 0, 0);
            rlCircle.setLayoutParams(lp);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }
    }

    private class AnimationHideLevelOne1 implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            rlCircle.clearAnimation();
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(rlCircle.getWidth(), rlCircle.getHeight());
            lp.setMargins(0, dpToPx(HEIGHT_BUTTON * 3 + MARGIN_TOP), 0, 0);
            rlCircle.setLayoutParams(lp);
            //rlTriangle.setLayoutParams(lp);
            //rlSquare.setLayoutParams(lp);

            TranslateAnimation animation2 = new TranslateAnimation(0, 0, 0, dpToPx(HEIGHT_BUTTON));
            animation2.setDuration(DURATION_HIDE_ANIMATON);
            animation2.setFillAfter(false);
            animation2.setAnimationListener(new AnimationHideLevelTwo1());
            rlTriangle.startAnimation(animation2);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }
    }

    private class AnimationHideLevelTwo1 implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            rlTriangle.clearAnimation();
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(rlTriangle.getWidth(), rlTriangle.getHeight());
            lp.setMargins(0, dpToPx(HEIGHT_BUTTON * 3 + MARGIN_TOP), 0, 0);
            rlTriangle.setLayoutParams(lp);
            //rlSquare.setLayoutParams(lp);

            TranslateAnimation animation2 = new TranslateAnimation(0, 0, 0, dpToPx(HEIGHT_BUTTON));
            animation2.setDuration(DURATION_HIDE_ANIMATON);
            animation2.setFillAfter(false);
            animation2.setAnimationListener(new AnimationHideLevelThree1());
            rlSquare.startAnimation(animation2);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }
    }

    private class AnimationHideLevelThree1 implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            rlSquare.clearAnimation();
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(rlSquare.getWidth(), rlSquare.getHeight());
            lp.setMargins(0, dpToPx(HEIGHT_BUTTON * 3 + MARGIN_TOP), 0, 0);
            rlSquare.setLayoutParams(lp);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }
    }

    private class AnimationShowLevelOne2 implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            rlBlue.clearAnimation();
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(rlBlue.getWidth(), rlBlue.getHeight());
            lp.setMargins(0, dpToPx(HEIGHT_BUTTON * 2 + MARGIN_TOP), 0, 0);
            rlBlue.setLayoutParams(lp);
            rlGreen.setLayoutParams(lp);
            rlRed.setLayoutParams(lp);

            TranslateAnimation animation2 = new TranslateAnimation(0, 0, 0, -dpToPx(HEIGHT_BUTTON));
            animation2.setDuration(DURATION_SHOW_ANIMATON);
            animation2.setFillAfter(false);
            animation2.setAnimationListener(new AnimationShowLevelTwo2());
            rlGreen.startAnimation(animation2);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }
    }

    private class AnimationShowLevelTwo2 implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            rlGreen.clearAnimation();
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(rlTriangle.getWidth(), rlGreen.getHeight());
            lp.setMargins(0, dpToPx(HEIGHT_BUTTON + MARGIN_TOP), 0, 0);
            rlGreen.setLayoutParams(lp);
            rlRed.setLayoutParams(lp);

            TranslateAnimation animation2 = new TranslateAnimation(0, 0, 0, -dpToPx(HEIGHT_BUTTON));
            animation2.setDuration(DURATION_SHOW_ANIMATON);
            animation2.setFillAfter(false);
            animation2.setAnimationListener(new AnimationShowLevelThree2());
            rlRed.startAnimation(animation2);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }
    }

    private class AnimationShowLevelThree2 implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            rlRed.clearAnimation();
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(rlRed.getWidth(), rlRed.getHeight());
            lp.setMargins(0, dpToPx(MARGIN_TOP), 0, 0);
            rlRed.setLayoutParams(lp);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }
    }

    private class AnimationHideLevelOne2 implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            rlRed.clearAnimation();
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(rlRed.getWidth(), rlRed.getHeight());
            lp.setMargins(0, dpToPx(HEIGHT_BUTTON * 3 + MARGIN_TOP), 0, 0);
            rlRed.setLayoutParams(lp);
            //rlTriangle.setLayoutParams(lp);
            //rlSquare.setLayoutParams(lp);

            TranslateAnimation animation2 = new TranslateAnimation(0, 0, 0, dpToPx(HEIGHT_BUTTON));
            animation2.setDuration(DURATION_HIDE_ANIMATON);
            animation2.setFillAfter(false);
            animation2.setAnimationListener(new AnimationHideLevelTwo2());
            rlGreen.startAnimation(animation2);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }
    }

    private class AnimationHideLevelTwo2 implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            rlGreen.clearAnimation();
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(rlGreen.getWidth(), rlGreen.getHeight());
            lp.setMargins(0, dpToPx(HEIGHT_BUTTON * 3 + MARGIN_TOP), 0, 0);
            rlGreen.setLayoutParams(lp);
            //rlSquare.setLayoutParams(lp);

            TranslateAnimation animation2 = new TranslateAnimation(0, 0, 0, dpToPx(HEIGHT_BUTTON));
            animation2.setDuration(DURATION_HIDE_ANIMATON);
            animation2.setFillAfter(false);
            animation2.setAnimationListener(new AnimationHideLevelThree2());
            rlBlue.startAnimation(animation2);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }
    }

    private class AnimationHideLevelThree2 implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            rlBlue.clearAnimation();
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(rlBlue.getWidth(), rlBlue.getHeight());
            lp.setMargins(0, dpToPx(HEIGHT_BUTTON * 3 + MARGIN_TOP), 0, 0);
            rlBlue.setLayoutParams(lp);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }
    }

    private void hideTools() {
        if (shapesToolsShowing == true) {
            shapesToolHide();
            shapesToolsShowing = false;
        }
        if (brushToolsShowing == true) {
            brushToolHide();
            brushToolsShowing = false;
        }
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }
}
