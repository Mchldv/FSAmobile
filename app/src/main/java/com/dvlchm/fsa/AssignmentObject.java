package com.dvlchm.fsa;

/**
 * Created by WINDA on 5/23/2017.
 */

public class AssignmentObject {
    public AssignmentObject(String assignmentId_, String locationName_ , String idLokasi_, String idSurveyor_, int done) {
        this.assignmentId = assignmentId_;
        this.locationName = locationName_;
        this.idLokasi = idLokasi_;
        this.idSurveyor = idSurveyor_;
        this.done= done;
    }

    public AssignmentObject(String assignmentId_, String locationName_ , String idLokasi_, String idSurveyor_, int done, int keb_TM,
                            int keb_AM, int nil_K, String excuse, String address, String image) {
        this.assignmentId = assignmentId_;
        this.locationName = locationName_;
        this.idLokasi = idLokasi_;
        this.idSurveyor = idSurveyor_;
        this.done= done;
        this.keb_AM=keb_AM;
        this.keb_TM=keb_TM;
        this.nil_K=nil_K;
        this.excuse=excuse;
        this.address=address;
        this.image=image;
    }

    private String assignmentId;
    private String locationName;
    private String idLokasi;
    private String idSurveyor;
    private Integer done;

    private Integer keb_TM;
    private Integer keb_AM;
    private Integer nil_K;
    private String address;
    private String excuse;
    private String image;
    private String lat;
    private String longitude;

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

    public Integer getDone(){return this.done;}

    public void setDone(Integer done) {this.done=done;}

    public Integer getKeb_TM() {return this.keb_TM;}

    public void setKeb_TM(Integer keb_TM) {
        this.keb_TM = keb_TM;
    }

    public Integer getKeb_AM() {return this.keb_AM;}

    public void setKeb_AM(Integer keb_AM) {
        this.keb_AM = keb_AM;
    }

    public Integer getNil_K() {return this.nil_K;}

    public void setNil_K(Integer nil_K) {
        this.nil_K = nil_K;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getExcuse() {
        return this.excuse;
    }

    public void setExcuse(String excuse) {
        this.excuse = excuse;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
