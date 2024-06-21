package com.example.myapplication.model;

public class Database {
    String name="fu",password,phone;
    Database(){}
    public Database(String name, String password, String phone){
        this.name=name;
        this.password=password;
        this.phone=phone;
    }
    public String getName()
    {
        return name;
    }
    public String getPassword(){
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password ) {
        this.password= password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }
}
