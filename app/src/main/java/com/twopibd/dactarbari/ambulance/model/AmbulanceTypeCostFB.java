package com.twopibd.dactarbari.ambulance.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AmbulanceTypeCostFB {

@SerializedName("basefee")
@Expose
private Integer basefee;
@SerializedName("icon")
@Expose
private String icon;
@SerializedName("per_km")
@Expose
private Long perKm;
@SerializedName("status")
@Expose
private String status;

public Integer getBasefee() {
return basefee;
}

public void setBasefee(Integer basefee) {
this.basefee = basefee;
}

public String getIcon() {
return icon;
}

public void setIcon(String icon) {
this.icon = icon;
}

public Long getPerKm() {
return perKm;
}

public void setPerKm(Long perKm) {
this.perKm = perKm;
}

public String getStatus() {
return status;
}

public void setStatus(String status) {
this.status = status;
}

    public AmbulanceTypeCostFB() {
    }
}