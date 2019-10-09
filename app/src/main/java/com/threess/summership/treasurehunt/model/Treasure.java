package com.threess.summership.treasurehunt.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@Entity(tableName = "treasure_table")
public class Treasure {


    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    public String mID;
    @SerializedName("username")
    public String mUsername;
    @SerializedName("passcode")
    public String mPasscode;
    @SerializedName("title")
    public String mTitle;
    @SerializedName("description")
    public String mDescription;
    @SerializedName("photo_clue")
    public String mPhotoClue;
    @SerializedName("location_lat")
    public double mLocationLat;
    @SerializedName("location_lon")
    public double mLocationLon;
    @SerializedName("prize_points")
    public long mPrizePoints;
    @SerializedName("claimed")
    public boolean mClaimed;
    @SerializedName("claimed_by")
    public String mClaimedBy;
    @SerializedName("claimedAt")
    public String mClaimedAt;

    public boolean mIsFavorite;

    public Treasure(@NonNull String mID, String mUsername, String mPasscode, String mTitle,
                    String mDescription, String mPhotoClue, double mLocationLat,
                    double mLocationLon, long mPrizePoints, boolean mClaimed, String mClaimedBy,
                    String mClaimedAt, boolean isFavorite) {
        this.mID = mID;
        this.mUsername = mUsername;
        this.mPasscode = mPasscode;
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mPhotoClue = mPhotoClue;
        this.mLocationLat = mLocationLat;
        this.mLocationLon = mLocationLon;
        this.mPrizePoints = mPrizePoints;
        this.mClaimed = mClaimed;
        this.mClaimedBy = mClaimedBy;
        this.mClaimedAt = mClaimedAt;
        this.mIsFavorite = isFavorite;
    }

    @Ignore
    public Treasure(String mUsername, String mPasscode, String mTitle,
                    String mDescription, String mPhotoClue, double mLocationLat,
                    double mLocationLon, long mPrizePoints, boolean mClaimed, String mClaimedBy,
                    String mClaimedAt) {
        this.mUsername = mUsername;
        this.mPasscode = mPasscode;
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mPhotoClue = mPhotoClue;
        this.mLocationLat = mLocationLat;
        this.mLocationLon = mLocationLon;
        this.mPrizePoints = mPrizePoints;
        this.mClaimed = mClaimed;
        this.mClaimedBy = mClaimedBy;
        this.mClaimedAt = mClaimedAt;
    }

    @Ignore
    public Treasure() {
    }

    @NonNull
    public String getmID() {
        return mID;
    }

    public boolean isFavorite() {
        return mIsFavorite;
    }

    public void setmIsFavorite(boolean mIsFavorite) {
        this.mIsFavorite = mIsFavorite;
    }

    public String getClaimedAt() {
        return mClaimedAt;
    }
    public String getUsername() {
        return mUsername;
    }

    public String getPasscode() {
        return mPasscode;
    }

    public void setPasscode(String passcode) {
        this.mPasscode = passcode;
    }


    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getPhotoClue() {
        return mPhotoClue;
    }

    public void setPhotoClue(String photoClue) {
        this.mPhotoClue = photoClue;
    }

    public double getLocationLat() {
        return mLocationLat;
    }

    public void setLocationLat(double locationLat) {
        this.mLocationLat = locationLat;
    }

    public double getLocationLon() {
        return mLocationLon;
    }

    public void setLocationLon(double locationLon) {
        this.mLocationLon = locationLon;
    }

    public long getPrizePoints() {
        return mPrizePoints;
    }

    public void setPrizePoints(long prizePoints) {
        this.mPrizePoints = prizePoints;
    }

    public boolean isClaimed() {
        return mClaimed;
    }

    public void setClaimed(boolean claimed) {
        this.mClaimed = claimed;
    }

    public String getClaimedBy() {
        return mClaimedBy;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }
}
