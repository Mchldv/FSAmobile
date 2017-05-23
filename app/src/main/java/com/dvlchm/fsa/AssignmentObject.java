package com.dvlchm.fsa;

/**
 * Created by WINDA on 5/23/2017.
 */

public class AssignmentObject {
    public AssignmentObject(String assignmentId_, String locationName_ ) {
        this.assignmentId = assignmentId_;
        this.locationName = locationName_;
    }

    private String assignmentId;
    private String locationName;

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
}
