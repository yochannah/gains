package com.digitalcranberry.gainsl.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yo on 04/07/15.
 */
public class PropertyMap {
    @SerializedName("propertyMap")
    private Report report;

    public Report getReport() {
        return report;
    }

    public PropertyMap(){
    }

    public PropertyMap(Report report){
        this.report = report;
    }

    @Override
    public String toString() {
        return "PropertyMap{" +
                "report=" + report.getContent() +
                '}';
    }

    public static List<Report> getReportList(List<PropertyMap> propertyMapList) {
        List<Report> theList = new ArrayList<>();
        for (PropertyMap pm :propertyMapList) {
            theList.add(pm.getReport());
        }
        return theList;
    }
}
