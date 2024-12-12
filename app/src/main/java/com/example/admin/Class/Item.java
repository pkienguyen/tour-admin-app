package com.example.admin.Class;

import java.io.Serializable;

public class Item implements Serializable {
    private int id;
    private String title;
    private String address;
    private String description;
    private String pic;
    private String duration;
    private int price;
    private int bed;
    private String distance;
    private double score;
    private int categoryId;
    private int isPopular;
    private int isRecommended;
    private int numberOfBookings;

    public Item() {
    }

    public Item(int id, String title, String address, String description, String pic, String duration, int price, int bed, String distance, double score, int categoryId, int isPopular, int isRecommended, int numberOfBookings) {
        this.id = id;
        this.title = title;
        this.address = address;
        this.description = description;
        this.pic = pic;
        this.duration = duration;
        this.price = price;
        this.bed = bed;
        this.distance = distance;
        this.score = score;
        this.categoryId = categoryId;
        this.isPopular = isPopular;
        this.isRecommended = isRecommended;
        this.numberOfBookings = numberOfBookings;
    }

    public int getNumberOfBookings() {
        return numberOfBookings;
    }

    public void setNumberOfBookings(int numberOfBookings) {
        this.numberOfBookings = numberOfBookings;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getBed() {
        return bed;
    }

    public void setBed(int bed) {
        this.bed = bed;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getIsPopular() {
        return isPopular;
    }

    public void setIsPopular(int isPopular) {
        this.isPopular = isPopular;
    }

    public int getIsRecommended() {
        return isRecommended;
    }

    public void setIsRecommended(int isRecommended) {
        this.isRecommended = isRecommended;
    }
}
