package com.digitalcranberry.gainsl.model;

import android.location.Location;
import android.net.Uri;

import com.digitalcranberry.gainsl.constants.ReportStatuses;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import org.osmdroid.util.GeoPoint;

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
        //this 'if' copes with legacy reports only.
        if(this.status == null) {
            return ReportStatuses.REPORT_SENT;
        }

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
                "lat='" + latitude + '\'' +
                "long='" + longitude + '\'' +
                "long='" + longitude + '\'' +
                ", date=" + date +
                ", status='" + status + '\'' +
                '}';
    }

    /* Loose report comparison based on id alone */
    public boolean sameReportId(Report otherReport){
        return otherReport.getId().equals(getId());
    }

    /* we don't check for object equality here, just id equality */
    @Override
    public boolean equals(Object o) {
        return sameReportId((Report) o);
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

    public GeoPoint getGeopoint() {
        return new GeoPoint(getLatitude(),getLongitude());
    }
}