package com.visualogyx.apirequest.processimage;

public class ProcessImageSuccessResponseModel {

    public String id;
    public String centre_x;
    public String centre_y;
    public String radius;

    public ProcessImageSuccessResponseModel() {

    }

    public ProcessImageSuccessResponseModel(String id, String centre_x, String centre_y, String radius) {
        this.id = id;
        this.centre_x = centre_x;
        this.centre_y = centre_y;
        this.radius = radius;
    }
}