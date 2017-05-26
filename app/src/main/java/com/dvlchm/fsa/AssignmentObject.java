package com.dvlchm.fsa;

/**
 * Created by WINDA on 5/23/2017.
 */

public class AssignmentObject {
    public AssignmentObject(String assignmentId_, String locationName_ , String idLokasi_, String idSurveyor_) {
        this.assignmentId = assignmentId_;
        this.locationName = locationName_;
        this.idLokasi = idLokasi_;
        this.idSurveyor = idSurveyor_;
    }

    private String assignmentId;
    private String locationName;
    private String idLokasi;
    private String idSurveyor;

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getIdLokasi() {
        return idLokasi;
    }

    public void setIdLokasi(String idLokasi) {
        this.idLokasi = idLokasi;
    }

    public String getIdSurveyor() {
        return idSurveyor;
    }

    public void setIdSurveyor(String idSurveyor) {
        this.idSurveyor = idSurveyor;
    }
}
