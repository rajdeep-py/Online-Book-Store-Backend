package com.bookstore.model;

import java.sql.Timestamp;

public class AboutUs {
    private int aboutId;
    private String companyName;
    private String companyTagline;
    private String companyDescription;
    private String directorMessage;
    private String directorName;
    private String mission;
    private String vision;
    private String partnersJson;
    private String phoneNo;
    private String emailId;
    private String address;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public int getAboutId() {
        return aboutId;
    }

    public void setAboutId(int aboutId) {
        this.aboutId = aboutId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyTagline() {
        return companyTagline;
    }

    public void setCompanyTagline(String companyTagline) {
        this.companyTagline = companyTagline;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }

    public String getDirectorMessage() {
        return directorMessage;
    }

    public void setDirectorMessage(String directorMessage) {
        this.directorMessage = directorMessage;
    }

    public String getDirectorName() {
        return directorName;
    }

    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public String getVision() {
        return vision;
    }

    public void setVision(String vision) {
        this.vision = vision;
    }

    public String getPartnersJson() {
        return partnersJson;
    }

    public void setPartnersJson(String partnersJson) {
        this.partnersJson = partnersJson;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
