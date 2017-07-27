package com.visualogyx.Utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.visualogyx.model.Pipe;
import com.visualogyx.model.PipeType;

import org.jcodec.common.model.Point;

import java.util.ArrayList;

public class DrawingPad implements View.OnTouchListener {

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private static final int MOVE = 3;

    public ScrollView scrollView;
    private int mode = NONE;

    public boolean isTraining = false;
    public Bitmap backgroundImage;
    public int color;
    public boolean isColor = false;
    public PipeType type;
    private float oldDist = 1f;

    public ArrayList<Pipe> pipes = new ArrayList<>();

    private Pipe pendingPipe;
    private float pendingRadius;
    private boolean pendingDrag;
    private Point pendingCenter;
    public ImageView overlayImageView;
    public ImageView originImageView;
    private PointF start = new PointF();
    private int moveCount = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView imageView = (ImageView) v;
        overlayImageView = imageView;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                start.set(event.getX(), event.getY());
                if (type != null && type != PipeType.Removed) {
                    if (pendingPipe != null) {
                        Point imagePoint = getImagePoint(event, imageView);
                        pendingDrag = pendingPipe.getFrame().contains(imagePoint.getX(), imagePoint.getY());
                        pendingCenter = pendingPipe.getCenter();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (scrollView != null)
                    scrollView.requestDisallowInterceptTouchEvent(false);
                if (mode != DRAG && mode != ZOOM && mode != MOVE)
                    overlayTapped(imageView, event);
                mode = NONE;
                pendingDrag = false;
                moveCount = 0;
                if (scrollView != null)
                    scrollView.requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    if (type != null && type != PipeType.Removed) {
                        if (pendingPipe != null) {
                            pendingRadius = pendingPipe.getRadius();
                            oldDist = spacing(event);
                            mode = ZOOM;
                            if (scrollView != null)
                                scrollView.requestDisallowInterceptTouchEvent(true);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (scrollView != null)
                    scrollView.requestDisallowInterceptTouchEvent(false);
                moveCount = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                moveCount++;
                if (moveCount <= 10)
                    break;

                if (pendingDrag && mode != ZOOM) {
                    if (scrollView != null)
                        scrollView.requestDisallowInterceptTouchEvent(true);
                    Point rawImagePoint = getImagePoint(event, imageView);
                    Point newPoint = new Point(rawImagePoint.getX() - pendingCenter.getX(), rawImagePoint.getY() - pendingCenter.getY());
                    Point newCenter = new Point(pendingCenter.getX() + newPoint.getX(), pendingCenter.getY() + newPoint.getY());
                    pendingPipe.setCenter(newCenter);
                    drawOverlay();
                    mode = DRAG;
                } else if (mode == ZOOM) {
                    if (scrollView != null)
                        scrollView.requestDisallowInterceptTouchEvent(true);
                    float newDist = spacing(event);
                    if (newDist == -9999999)
                        break;
                    if (newDist > 10f) {
                        float scale = newDist / oldDist;
                        pendingPipe.setRadius(pendingRadius * scale);
                        drawOverlay();
                    }
                } else
                    mode = MOVE;

                break;
        }
        return true;
    }

    private void overlayTapped(ImageView imageView, MotionEvent event) {
        if (backgroundImage == null) {
            return;
        }

        Point imagePoint = getImagePoint(event, imageView);

        if (type != null && type != PipeType.Removed) {
            pendingPipe = new Pipe();
            pendingPipe.setPipeType(type);
            pendingPipe.setRadius(100 * backgroundImage.getWidth() / imageView.getWidth());
            pendingPipe.setCenter(imagePoint);
            pendingPipe.setId(createNextId());
            pipes.add(pendingPipe);
        }

        if (type != null && type == PipeType.Removed) {
            int k = -1;
            for (int i = pipes.size() - 1; i >= 0; i--) {
                Pipe pipe = pipes.get(i);
                Rect frame = pipe.getFrame();
                if (frame.contains(imagePoint.getX(), imagePoint.getY())) {
                    if (pipe.getPipeType() == PipeType.Detected || pipe.getPipeType() == PipeType.Subtracted)
                        pipe.setPipeType(PipeType.Subtracted);
                    else
                        pipes.remove(i);
                    k = i;
                    break;
                }
            }

            if (k != -1) {
                for (int i = k; i < pipes.size(); i++) {
                    Pipe pipe = pipes.get(i);
                    pipe.setId(pipe.getId() - 1);
                }
            }

        }
        drawOverlay();
    }

    private Bitmap currentBitmap;

    private void drawPipesOnImage() {
        if (backgroundImage == null) {
            return;
        }

        if (currentBitmap != null)
            currentBitmap.recycle();

        currentBitmap = null;

        try {
            currentBitmap = Bitmap.createBitmap(backgroundImage.getWidth(), backgroundImage.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(currentBitmap);

            for (int i = 0; i < pipes.size(); i++) {
                Pipe pipe = pipes.get(i);
                Rect frame = pipe.getFrame();

                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                if (isColor) {
                    ColorFilter filter = new LightingColorFilter(color, 0);
                    paint.setColorFilter(filter);
                }
                if (pipe.getPipeType() != PipeType.Circle && pipe.getPipeType() != PipeType.Square && pipe.getPipeType() != PipeType.Triangle)
                    paint.setAlpha(100);
                canvas.drawBitmap(pipe.getImage(), null, frame, paint);
                if (pipe.getPipeType() == PipeType.Added || pipe.getPipeType() == PipeType.Detected) {
                    paint.setARGB(255, 0, 0, 0);
                    paint.setTextSize(16);
                    paint.setAlpha(255);
                    paint.setColor(Color.WHITE);
                    paint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(String.valueOf(pipe.getId()), pipe.getX() - 3, pipe.getY() + 10, paint);
                }

            }
        } catch (Exception e) {

        }
    }

    public void drawOverlay() {
        drawPipesOnImage();
        overlayImageView.setImageBitmap(currentBitmap);
    }

    public Bitmap drawPipesImageProcessing() {
        if (currentBitmap != null)
            currentBitmap.recycle();

        currentBitmap = null;

        try {
            currentBitmap = Bitmap.createBitmap(backgroundImage.getWidth(), backgroundImage.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(currentBitmap);
            for (int i = 0; i < pipes.size(); i++) {
                Pipe pipe = pipes.get(i);
                Rect frame = pipe.getFrame();

                Paint paint = new Paint();
                ;
                if (isColor) {
                    ColorFilter filter = new LightingColorFilter(color, 0);
                    paint.setColorFilter(filter);
                }
                if (pipe.getPipeType() != PipeType.Subtracted) {
                    paint.setAlpha(100);
                    canvas.drawBitmap(pipe.getImage(), null, frame, paint);
                }

                if (pipe.getPipeType() == PipeType.Added || pipe.getPipeType() == PipeType.Detected) {
                    paint.setTextSize(16);
                    paint.setAlpha(255);
                    paint.setColor(Color.WHITE);
                    paint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(String.valueOf(pipe.getId()), pipe.getX() - 3, pipe.getY() + 10, paint);
                }
            }
        } catch (Exception e) {

        }
        overlayImageView.setImageBitmap(currentBitmap);

        Bitmap bitmapFull = Bitmap.createBitmap(backgroundImage.getWidth(),
                backgroundImage.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapFull);
        canvas.drawARGB(0x00, 0, 0, 0);
        canvas.drawBitmap(backgroundImage, 0, 0, null);

        Bitmap bitmapTemp = ((BitmapDrawable) overlayImageView.getDrawable()).getBitmap();
        Paint alphaPaint = new Paint();
        alphaPaint.setAlpha(154);
        canvas.drawBitmap(bitmapTemp, 0, 0, alphaPaint);

        BitmapDrawable dr = new BitmapDrawable(bitmapFull);
        dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
        return dr.getBitmap();
    }

    public int getCountPipes() {
        int count = 0;
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);

            if (pipe.getPipeType() != PipeType.Subtracted)
                count++;
        }
        return count;
    }

    public Bitmap drawPipesTraining() {
        drawOverlay();
        Bitmap bitmapFull = Bitmap.createBitmap(backgroundImage.getWidth(),
                backgroundImage.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapFull);
        canvas.drawARGB(0x00, 0, 0, 0);
        canvas.drawBitmap(backgroundImage, 0, 0, null);

        Bitmap bitmapTemp = ((BitmapDrawable) overlayImageView.getDrawable()).getBitmap();
        Paint alphaPaint = new Paint();
        alphaPaint.setAlpha(154);
        canvas.drawBitmap(bitmapTemp, 0, 0, alphaPaint);

        BitmapDrawable dr = new BitmapDrawable(bitmapFull);
        dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
        return dr.getBitmap();
    }

    private Point getImagePoint(MotionEvent event, ImageView imageView) {

        float scaleX = (float) backgroundImage.getWidth() / imageView.getWidth();
        float scaleY = (float) backgroundImage.getHeight() / imageView.getHeight();

        float scale;
        if (!isTraining)
            scale = Math.max(scaleX, scaleY);
        else
            scale = Math.min(scaleX, scaleY);
        float deltaX = (imageView.getWidth() * scale - backgroundImage.getWidth()) / 2;
        float deltaY = (imageView.getHeight() * scale - backgroundImage.getHeight()) / 2;
        float realX = event.getX() * scale - deltaX;
        float realY = event.getY() * scale - deltaY;
        Point imagePoint = new Point((int) realX, (int) realY);
        return imagePoint;
    }

    private float spacing(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        } else return -9999999;
    }

    public void createPipes(int centerX, int centerY, int radius, int id) {
        Point imagePoint = new Point(centerX, centerY);
        pendingPipe = new Pipe();
        pendingPipe.setId(id);
        pendingPipe.setPipeType(type);
        pendingPipe.setRadius(radius);
        pendingPipe.setCenter(imagePoint);
        pipes.add(pendingPipe);
    }

    private int createNextId() {
        int maxId = 0;
        for (int i = 0; i < pipes.size(); i++) {
            if (pipes.get(i).getId() > maxId) {
                maxId = pipes.get(i).getId();
            }
        }
        maxId++;
        return maxId;
    }
}
