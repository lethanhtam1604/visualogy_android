package com.visualogyx.apirequest.inspection;

import java.io.Serializable;

public class InspectionSuccessResponseModel implements Serializable{
    public String request_id;
    public String name;
    public Integer count;
    public String image_data;
    public String image_thumb_data;
    public String inspector_name;
    public String manager_name;
    public String location;
    public String proc_image_data;
    public String proc_image_thumb_data;
    public String start_date;
    public String end_date;
    public String timestamp;
    public String description;
    public String address;
}
