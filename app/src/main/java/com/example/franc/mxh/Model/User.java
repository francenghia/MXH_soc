package com.example.franc.mxh.Model;

public class User {
    private String email,password ,name,phone,image,gender,country,dateofbirth,cover,status,status_location;

    public User() {
    }

    public User(String email, String status_location) {
        this.email = email;
        this.status_location = status_location;
    }

    public User(String email, String password, String name, String phone, String image, String gender, String country, String dateofbirth, String cover, String status) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.image = image;
        this.gender = gender;
        this.country = country;
        this.dateofbirth = dateofbirth;
        this.cover = cover;
        this.status=status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(String dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus_location() {
        return status_location;
    }

    public void setStatus_location(String status_location) {
        this.status_location = status_location;
    }
}
