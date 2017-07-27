package com.visualogyx.apirequest.inspection.update;

public class UpdateInspectionModel {

    public String request_id;

    public String status;

    public String count;

    public String location;

    public String processed_image_file_name;

    public String processed_image_file_data;

    public UpdateInspectionModel(String request_id, String status, String count, String location, String processed_image_file_name, String processed_image_file_data) {
        this.request_id = request_id;
        this.status = status;
        this.count = count;
        this.location = location;
        this.processed_image_file_name = processed_image_file_name;
        this.processed_image_file_data = processed_image_file_data;
    }
}
