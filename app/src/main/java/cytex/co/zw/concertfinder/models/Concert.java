package cytex.co.zw.concertfinder.models;

import java.util.Date;

public class Concert {
    private String id;
    private String category;
    private String description;
    private String pictureUrl;
    private String   date;
    private String   startTime;
    private String locatonName;
    private boolean hasMap;
    private double latitude;
    private double longitude;
    private String adderEmail;

    public Concert() {
    }

    public Concert(String description, double latitude, double longitude) {
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getLocatonName() {
        return locatonName;
    }

    public void setLocatonName(String locatonName) {
        this.locatonName = locatonName;
    }

    public boolean getHasMap() {
        return hasMap;
    }

    public void setHasMap(boolean hasMap) {
        this.hasMap = hasMap;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }



    public String getAdderEmail() {
        return adderEmail;
    }

    public void setAdderEmail(String adderEmail) {
        this.adderEmail = adderEmail;
    }

    @Override
    public String toString() {
        return "Concert{" +
                "id='" + id + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", pictureUrl='" + pictureUrl + '\'' +
                ", date='" + date + '\'' +
                ", startTime='" + startTime + '\'' +
                ", locatonName='" + locatonName + '\'' +
                ", hasMap=" + hasMap +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", adderEmail='" + adderEmail + '\'' +
                '}';
    }
}
