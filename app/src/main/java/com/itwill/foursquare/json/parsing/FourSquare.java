package com.itwill.foursquare.json.parsing;

import android.graphics.Bitmap;

import android.os.Parcel;
import android.os.Parcelable;

class FourSquare implements Parcelable{
    private String name;
    private String url;
    private String address;
    private String lat;
    private String lng;
    private Bitmap bitmap;
    private String imageLink;
    private String tips;

    public FourSquare(String name, String url, String address, String lat, String lng, Bitmap bitmap, String tips) {
        this.name = name;
        this.url = url;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.bitmap = bitmap;
        this.tips = tips;
    }


    public FourSquare(Parcel in) {
        name = in.readString();
        url = in.readString();
        address = in.readString();
        lat = in.readString();
        lng = in.readString();
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
        imageLink = in.readString();
        tips = in.readString();
    }


    public static final Creator<FourSquare> CREATOR = new Creator<FourSquare>() {
        @Override
        public FourSquare createFromParcel(Parcel in) {
            return new FourSquare(in);
        }

        @Override
        public FourSquare[] newArray(int size) {
            return new FourSquare[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
        dest.writeString(address);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeParcelable(bitmap, flags);
        dest.writeString(imageLink);
        dest.writeString(tips);
    }
}
