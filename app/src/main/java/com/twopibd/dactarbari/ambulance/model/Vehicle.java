package com.twopibd.dactarbari.ambulance.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Vehicle {

@SerializedName("certificate_fitness")
@Expose
private Long certificateFitness;
@SerializedName("registration")
@Expose
private Long registration;
@SerializedName("tax_token")
@Expose
private Long taxToken;

public Long getCertificateFitness() {
return certificateFitness;
}

public void setCertificateFitness(Long certificateFitness) {
this.certificateFitness = certificateFitness;
}

public Long getRegistration() {
return registration;
}

public void setRegistration(Long registration) {
this.registration = registration;
}

public Long getTaxToken() {
return taxToken;
}

public void setTaxToken(Long taxToken) {
this.taxToken = taxToken;
}

}