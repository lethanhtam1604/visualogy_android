package com.visualogyx.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.visualogyx.R;
import com.visualogyx.manager.Global;

import org.jcodec.common.model.Point;

public class Pipe {

    private int id;
    private float x;
    private float y;
    private float radius;
    private Point center;
    private Rect frame;
    private PipeType pipeType = null;
    private Bitmap image = null;

    public Pipe() {

    }

    public Pipe(float radius, Point center, PipeType pipeType) {
        this.radius = radius;
        this.center = center;
        this.pipeType = pipeType;
    }

    public float getX() {
        return this.center.getX();
    }

    public float getY() {
        return this.center.getY();
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public Rect getFrame() {
        int left = (int) (getX() - getRadius());
        int top = (int) (getY() - getRadius());
        this.frame = new Rect(left, top, left + (int) getRadius() * 2, top + (int) getRadius() * 2);
        return this.frame;
    }

    public PipeType getPipeType() {
        return pipeType;
    }

    public void setPipeType(PipeType pipeType) {
        this.pipeType = pipeType;

        if (pipeType == PipeType.Added)
            setImage(BitmapFactory.decodeResource(Global.context.getResources(), R.drawable.add_1));
        else if (pipeType == PipeType.Removed)
            setImage(BitmapFactory.decodeResource(Global.context.getResources(), R.drawable.subtract_1));
        else if (pipeType == PipeType.Detected)
            setImage(BitmapFactory.decodeResource(Global.context.getResources(), R.drawable.detect));
        else if (pipeType == PipeType.Subtracted)
            setImage(BitmapFactory.decodeResource(Global.context.getResources(), R.drawable.subtract_1));
        else if (pipeType == PipeType.Circle)
            setImage(BitmapFactory.decodeResource(Global.context.getResources(), R.drawable.circle));
        else if (pipeType == PipeType.Square)
            setImage(BitmapFactory.decodeResource(Global.context.getResources(), R.drawable.square));
        else if (pipeType == PipeType.Triangle)
            setImage(BitmapFactory.decodeResource(Global.context.getResources(), R.drawable.triangle));
        else
            setImage(null);
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public float getRadius() {
        return radius;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
