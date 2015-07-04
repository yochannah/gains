package com.digitalcranberry.gainsl.model;

import android.location.Location;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.Date;

public class Report {

    private String content;
    private Date date;
    private String status;
    private Double latitude;
    private Double longitude;

    @SerializedName("reportid")
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

    @Override
    public String toString() {
        return "Report{" +
                "content='" + content + '\'' +
                ", date=" + date +
                ", status='" + status + '\'' +
                '}';
    }

    /* Loose report comparison based on id alone */
    public boolean sameReportId(Report otherReport){
        return otherReport.getId().equals(getId());
    }

    /* checks if reports are identical */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Report report = (Report) o;

        if (content != null ? !content.equals(report.content) : report.content != null)
            return false;
        if (date != null ? !date.equals(report.date) : report.date != null) return false;
        if (status != null ? !status.equals(report.status) : report.status != null) return false;
        if (latitude != null ? !latitude.equals(report.latitude) : report.latitude != null)
            return false;
        if (longitude != null ? !longitude.equals(report.longitude) : report.longitude != null)
            return false;
        if (id != null ? !id.equals(report.id) : report.id != null) return false;
        if (image != null ? !image.equals(report.image) : report.image != null) return false;
        if (orgName != null ? !orgName.equals(report.orgName) : report.orgName != null)
            return false;
        return !(reporter != null ? !reporter.equals(report.reporter) : report.reporter != null);

    }

    @Override
    public int hashCode() {
        int result = content != null ? content.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (orgName != null ? orgName.hashCode() : 0);
        result = 31 * result + (reporter != null ? reporter.hashCode() : 0);
        return result;
    }
}