package com.visualogyx.apirequest.training;

public class TrainingModel {

    public String email;

    public String tag_description;

    public String training_image_file_name;

    public String training_image_file_data;

    public TrainingModel(String email, String tag_description, String training_image_file_name, String training_image_file_data) {
        this.email = email;
        this.tag_description = tag_description;
        this.training_image_file_name = training_image_file_name;
        this.training_image_file_data = training_image_file_data;
    }
}
