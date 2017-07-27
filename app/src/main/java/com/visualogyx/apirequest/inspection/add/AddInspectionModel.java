package com.visualogyx.apirequest.inspection.add;

public class AddInspectionModel {
    public String name;
    public String manager_name;
    public String inspector_name;
    public String location;
    public String start_date;
    public String end_date;
    public String reference_image_file_name;
    public String reference_image_file_data;

    public AddInspectionModel(String name, String manager_name, String inspector_name, String location, String start_date, String end_date, String reference_image_file_name, String reference_image_file_data) {
        this.name = name;
        this.manager_name = manager_name;
        this.inspector_name = inspector_name;
        this.location = location;
        this.start_date = start_date;
        this.end_date = end_date;
        this.reference_image_file_name = reference_image_file_name;
        this.reference_image_file_data = reference_image_file_data;
    }
}
