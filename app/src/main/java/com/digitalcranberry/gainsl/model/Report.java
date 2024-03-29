package com.digitalcranberry.gainsl.model;

import android.location.Location;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import static com.digitalcranberry.gainsl.constants.ReportStatuses.*;
import static com.digitalcranberry.gainsl.constants.Constants.DEBUGTAG;

import com.digitalcranberry.gainsl.settings.SettingsManager;
import com.google.gson.annotations.SerializedName;

import org.osmdroid.util.GeoPoint;

import java.util.Calendar;
import java.util.Date;

public class Report implements Parcelable {

    private String content;
    private Date date; //the report indexes in this in the Google DataStore, so re-naming to something useful breaks it. Grr.
    private Date dateFirstCaptured;
    private Date lastUpdated;
    private String sendStatus;      //this is for tracking if it's sent or not
    private String userStatus;      //this tracks the user's status. Initially, it's set to new.
    private Double latitude;
    private Double longitude;

    @SerializedName("reportid")
    private String id;
    private Uri image;
    private String orgName;
    private String reporter;

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    private String lastUpdatedBy;
    private String assignee;


    public Report() {
        //if no date explicitly stated, we'll assume it's a brand new report
        this.dateFirstCaptured = new Date();
        this.userStatus = REPORT_NEW;
        this.orgName = "OU";
    }

    public Report(String theContent) {
        super();
        this.content = theContent;
        this.date = new Date();
        this.orgName = "OU";
    }

    public Report(String id, String content, String sendStatus, String userStatus, Double latitude, Double longitude, Uri image, Date dateCaptured, String lastUpdatedBy, String assignee, String reporter) {
        this.content = content;
        this.dateFirstCaptured = dateCaptured;
        this.lastUpdated = new Date();
        this.sendStatus = sendStatus;
        this.userStatus = userStatus;
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
        this.image = image;
        this.orgName = "OU";
        this.lastUpdatedBy = lastUpdatedBy;
        this.assignee = assignee;
        this.reporter = reporter;
    }

    public Report(String id, String content, String sendStatus, String userStatus, Double latitude, Double longitude, Uri image, Date dateCaptured, Date lastUpdated, String lastUpdatedBy, String assignee, String reporter) {
        this(id, content, sendStatus, userStatus, latitude, longitude, image, dateCaptured, lastUpdatedBy, assignee, reporter);
        this.lastUpdated = lastUpdated;
        this.orgName = "OU";
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

    public String getContent() {
        return content;
    }

    public void setContent(String theContent) {
        this.content = theContent;
    }

    public String getSendStatus() {
        //this 'if' copes with legacy reports only.
        if(this.sendStatus == null) {
            return REPORT_SENT;
        }

        return sendStatus;
    }

    public void setSendStatus(String aStatus) {
        this.sendStatus = aStatus;
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
        sb.append("&reporter=" + reporter);
        sb.append("&assignee=" + assignee);
        sb.append("&lastUpdatedBy=" + lastUpdatedBy);
        sb.append("&status=" + userStatus); //send status is internal and need never be transmitted

        Calendar c = Calendar.getInstance();
        c.setTime(dateFirstCaptured);
        long time = c.getTimeInMillis();
        sb.append("&dateFirstCaptured=" + time);

        c.setTime(lastUpdated);
        time = c.getTimeInMillis();
        sb.append("&lastUpdated=" + time);

        Log.i(DEBUGTAG, sb.toString());

        return sb.toString();
    }

    @Override
    public String toString() {
        return "Report: " + content + '\n' +
                "Location:" + latitude + ", " +
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
        if (sendStatus != null ? !sendStatus.equals(report.sendStatus) : report.sendStatus != null) return false;
        if (userStatus != null ? !userStatus.equals(report.userStatus) : report.userStatus != null) return false;
        if (latitude != null ? !latitude.equals(report.latitude) : report.latitude != null)
            return false;
        if (longitude != null ? !longitude.equals(report.longitude) : report.longitude != null)
            return false;
        if (id != null ? !id.equals(report.id) : report.id != null) return false;
        if (image != null ? !image.equals(report.image) : report.image != null) return false;
        if (orgName != null ? !orgName.equals(report.orgName) : report.orgName != null)
            return false;
        if (assignee != null ? !assignee.equals(report.assignee) : report.assignee != null)
            return false;
        if (lastUpdated != null ? !lastUpdated.equals(report.lastUpdated) : report.lastUpdated != null)
            return false;
        if (lastUpdatedBy != null ? !lastUpdatedBy.equals(report.lastUpdatedBy) : report.lastUpdatedBy != null)
            return false;
        return !(reporter != null ? !reporter.equals(report.reporter) : report.reporter != null);
    }


    @Override
    public int hashCode() {
        int result = content != null ? content.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (sendStatus != null ? sendStatus.hashCode() : 0);
        result = 31 * result + (userStatus != null ? sendStatus.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (orgName != null ? orgName.hashCode() : 0);
        result = 31 * result + (assignee != null ? assignee.hashCode() : 0);
        result = 31 * result + (lastUpdatedBy != null ? lastUpdatedBy.hashCode() : 0);
        result = 31 * result + (lastUpdated != null ? lastUpdated.hashCode() : 0);
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
        sendStatus = in.readString();
        userStatus = in.readString();
        latitude = in.readByte() == 0x00 ? null : in.readDouble();
        longitude = in.readByte() == 0x00 ? null : in.readDouble();
        id = in.readString();
        image = (Uri) in.readValue(Uri.class.getClassLoader());
        orgName = in.readString();
        assignee = in.readString();
        lastUpdatedBy = in.readString();
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
        dest.writeString(sendStatus);
        dest.writeString(userStatus);
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
        dest.writeString(assignee);
        dest.writeString(lastUpdatedBy);
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

    public String getUserStatus() {
        return userStatus;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }
}