package com.twopibd.dactarbari.ambulance.model;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AmbulanceModel {

    private String DriverID=null;
    @SerializedName("UserID")
    @Expose
    private String userID;
    @SerializedName("addtional_feature")
    @Expose
    private AddtionalFeature addtionalFeature;
    @SerializedName("app_oparation")
    @Expose
    private Long appOparation;
    @SerializedName("bearer")
    @Expose
    private Long bearer;
    @SerializedName("car_base_charge")
    @Expose
    private Long carBaseCharge;
    @SerializedName("car_current_lat")
    @Expose
    private Double carCurrentLat;
    @SerializedName("car_current_lng")
    @Expose
    private Double carCurrentLng;
    @SerializedName("car_driver_name")
    @Expose
    private String carDriverName;
    @SerializedName("car_fair_per_km")
    @Expose
    private Long carFairPerKm;
    @SerializedName("car_fare_per_minute")
    @Expose
    private Long carFarePerMinute;
    @SerializedName("car_image")
    @Expose
    private String carImage;
    @SerializedName("car_is_on_trip")
    @Expose
    private Boolean carIsOnTrip;
    @SerializedName("car_mobile")
    @Expose
    private String carMobile;
    @SerializedName("car_model")
    @Expose
    private String carModel;
    @SerializedName("car_type")
    @Expose
    private List<String> carType = null;
    @SerializedName("last_seen")
    @Expose
    private String lastSeen;
    @SerializedName("license_number")
    @Expose
    private String licenseNumber;
    @SerializedName("make")
    @Expose
    private String make;
    @SerializedName("mode")
    @Expose
    private String mode;
    @SerializedName("threshold_amount")
    @Expose
    private Long thresholdAmount;
    @SerializedName("vehicle")
    @Expose
    private Vehicle vehicle;
    @SerializedName("year")
    @Expose
    private Long year;

    public String getDriverID() {
        return DriverID;
    }

    public void setDriverID(String driverID) {
        DriverID = driverID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public AddtionalFeature getAddtionalFeature() {
        return addtionalFeature;
    }

    public void setAddtionalFeature(AddtionalFeature addtionalFeature) {
        this.addtionalFeature = addtionalFeature;
    }

    public Long getAppOparation() {
        return appOparation;
    }

    public void setAppOparation(Long appOparation) {
        this.appOparation = appOparation;
    }

    public Long getBearer() {
        return bearer;
    }

    public void setBearer(Long bearer) {
        this.bearer = bearer;
    }

    public Long getCarBaseCharge() {
        return carBaseCharge;
    }

    public void setCarBaseCharge(Long carBaseCharge) {
        this.carBaseCharge = carBaseCharge;
    }

    public Double getCarCurrentLat() {
        return carCurrentLat;
    }

    public void setCarCurrentLat(Double carCurrentLat) {
        this.carCurrentLat = carCurrentLat;
    }

    public Double getCarCurrentLng() {
        return carCurrentLng;
    }

    public void setCarCurrentLng(Double carCurrentLng) {
        this.carCurrentLng = carCurrentLng;
    }

    public String getCarDriverName() {
        return carDriverName;
    }

    public void setCarDriverName(String carDriverName) {
        this.carDriverName = carDriverName;
    }

    public Long getCarFairPerKm() {
        return carFairPerKm;
    }

    public void setCarFairPerKm(Long carFairPerKm) {
        this.carFairPerKm = carFairPerKm;
    }

    public Long getCarFarePerMinute() {
        return carFarePerMinute;
    }

    public void setCarFarePerMinute(Long carFarePerMinute) {
        this.carFarePerMinute = carFarePerMinute;
    }

    public String getCarImage() {
        return carImage;
    }

    public void setCarImage(String carImage) {
        this.carImage = carImage;
    }

    public Boolean getCarIsOnTrip() {
        return carIsOnTrip;
    }

    public void setCarIsOnTrip(Boolean carIsOnTrip) {
        this.carIsOnTrip = carIsOnTrip;
    }

    public String getCarMobile() {
        return carMobile;
    }

    public void setCarMobile(String carMobile) {
        this.carMobile = carMobile;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public List<String> getCarType() {
        return carType;
    }

    public void setCarType(List<String> carType) {
        this.carType = carType;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Long getThresholdAmount() {
        return thresholdAmount;
    }

    public void setThresholdAmount(Long thresholdAmount) {
        this.thresholdAmount = thresholdAmount;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

}