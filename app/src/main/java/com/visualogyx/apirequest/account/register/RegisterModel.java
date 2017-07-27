package com.visualogyx.apirequest.account.register;

public class RegisterModel {

    public String name;
    public String email;
    public String phoneNumber;
    public String password;
    public String confirmPassword;

    public RegisterModel() {

    }

    public RegisterModel(String name, String email, String phoneNumber, String password, String confirmPassword) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
}
