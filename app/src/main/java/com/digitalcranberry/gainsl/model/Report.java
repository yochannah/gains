package com.digitalcranberry.gainsl.model;

import android.location.Location;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.digitalcranberry.gainsl.constants.ReportStatuses;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import org.osmdroid.util.GeoPoint;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;

public class Report implements Parcelable {

    private String content;
    private Date date; //the report indexes in this in the Google DataStore, so re-naming to something useful breaks it. Grr.
    private Date dateFirstCaptured;
    private Date lastUpdated;
    private String status;
    private Double latitude;
    private Double longitude;

    @SerializedName("reportid")
    private String id;
    private Uri image;
    private String orgName;
    private String reporter;


    public Report() {
        //if no date explicitly stated, we'll assume it's a brand new report
        this.dateFirstCaptured = new Date();
    }

    public Report(String id, String content, Date dateCaptured, String status, Double latitude, Double longitude, Uri image) {
        this.content = content;
        this.dateFirstCaptured = dateCaptured;
        this.lastUpdated = new Date();
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
        this.image = image;
        this.orgName = "OU";
        this.reporter = "April";
    }

    public Report(String id, String content, Date dateCaptured, String status, Double latitude, Double longitude, Uri image, Date lastUpdated) {
        this(id, content, dateCaptured, status, latitude, longitude, image);
        this.lastUpdated = lastUpdated;
    }

    public Date getDateFirstCaptured() {
        return dateFirstCaptured;
    }

    public void setDateFirstCaptured(Date dateCaptured) {
        this.dateFirstCaptured = dateCaptured;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    public String toQueryParam() {
        StringBuilder sb = new StringBuilder();
        sb.append("orgName=" + orgName);
        sb.append("&content=" + content);
        sb.append("&latitude=" + latitude);
        sb.append("&longitude=" + longitude);
        sb.append("&reportid=" + id);

        Calendar c = Calendar.getInstance();
        c.setTime(dateFirstCaptured);
        long time = c.getTimeInMillis();
        sb.append("&dateFirstCaptured=" + time);

        c.setTime(lastUpdated);
        time = c.getTimeInMillis();
        sb.append("&lastUpdated=" + time);
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Report: " + content + '\n' +
                "Location=" + latitude + ", " +
                longitude;
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

    /*
    Reports are considered the same report - equal - if they have the same id,
    but they may have changed, with different data inside. This checks if there
    has been any change.
     */
    public boolean hasChanged(Report o) {
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

    public GeoPoint getGeopoint() {
        return new GeoPoint(getLatitude(),getLongitude());
    }

    protected Report(Parcel in) {
        content = in.readString();
        long tmpDate = in.readLong();
        date = tmpDate != -1 ? new Date(tmpDate) : null;
        long tmpDateCap = in.readLong();
        dateFirstCaptured = tmpDateCap != -1 ? new Date(tmpDateCap) : null;
        status = in.readString();
        latitude = in.readByte() == 0x00 ? null : in.readDouble();
        longitude = in.readByte() == 0x00 ? null : in.readDouble();
        id = in.readString();
        image = (Uri) in.readValue(Uri.class.getClassLoader());
        orgName = in.readString();
        reporter = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
        dest.writeLong(date != null ? date.getTime() : -1L);
        dest.writeLong(dateFirstCaptured != null ? dateFirstCaptured.getTime() : -1L);
        dest.writeString(status);
        if (latitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(longitude);
        }
        dest.writeString(id);
        dest.writeValue(image);
        dest.writeString(orgName);
        dest.writeString(reporter);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Report> CREATOR = new Parcelable.Creator<Report>() {
        @Override
        public Report createFromParcel(Parcel in) {
            return new Report(in);
        }

        @Override
        public Report[] newArray(int size) {
            return new Report[size];
        }
    };
}