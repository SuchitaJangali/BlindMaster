package com.example.myapplication.model;

public class Relative {
    String name ,phone,url;

    Relative(){}
    public Relative(String name, String phone, String url){
this.name=name;
this.phone=phone;
this.url=url;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
