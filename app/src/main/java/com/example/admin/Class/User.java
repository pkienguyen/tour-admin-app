package com.example.admin.Class;

public class User {
    private String id;
    private String email;
    private String name;
    private String role;
    private int numberOfBookings;
    private String phone;

    public User() {
    }

    public User(String id, String email, String name, String role, int numberOfBookings, String phone) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
        this.numberOfBookings = numberOfBookings;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getNumberOfBookings() {
        return numberOfBookings;
    }

    public void setNumberOfBookings(int numberOfBookings) {
        this.numberOfBookings = numberOfBookings;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
