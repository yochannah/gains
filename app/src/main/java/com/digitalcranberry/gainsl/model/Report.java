package com.digitalcranberry.gainsl.model;

import android.location.Location;
import android.net.Uri;

import java.util.Date;

public class Report {

    private String content;
    private Date date;
    private String status;
    private Double latitude;
    private Double longitude;
    private String id;
    private Uri image;
    private String orgName;
    private String reporter;


    public Report() {}

    public Report(String id, String content, Date date, String status, Double latitude, Double longitude, Uri image) {
        this.content = content;
        this.date = date;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
        this.image = image;
        this.orgName = "OU";
        this.reporter = "April";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }


    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }


    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLocation(Location location) {
        setLatitude(location.getLatitude());
        setLongitude(location.getLongitude());
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Report(String theContent) {
        this.content = theContent;
        this.date = new Date();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String theContent) {
        this.content = theContent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String aStatus) {
        this.status = aStatus;
    }

    public Date getDateCreated() {
        return date;
    }

    public void setDateCreated(Date date) {
        this.date = date;
    }

    public String toQueryParam() {
        StringBuilder sb = new StringBuilder();
        sb.append("orgName=" + orgName);
        sb.append("&content=" + content);
        sb.append("&latitude=" + latitude);
        sb.append("&longitude=" + longitude);
        sb.append("&reportid=" + id);
        return sb.toString();
    }


}