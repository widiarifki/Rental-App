package com.widiarifki.outdoorrent.model;

/**
 * Created by Widia Rifkianti on 15/06/2016.
 */
public class Product {
    private int mId;
    private String mName;
    private double mCharge;
    private int mChargeBase;
    private String mDescription;
    private String mTerms;
    private ProductCategory mProductCategory;
    private String mImageUrl;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public double getCharge() {
        return mCharge;
    }

    public void setCharge(double charge) {
        this.mCharge = charge;
    }

    public int getChargeBase() {
        return mChargeBase;
    }

    public void setChargeBase(int chargeBase) {
        this.mChargeBase = chargeBase;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getTerms() {
        return mTerms;
    }

    public void setTerms(String terms) {
        this.mTerms = terms;
    }

    public ProductCategory getProductCategory() {
        return mProductCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.mProductCategory = productCategory;
    }

    public String getImageUrl() {
        //return mImageUrl;
        return "http://api.learn2crack.com/android/images/marshmallow.png";
    }

    public void setImageUrl(String imageUrl) {
        this.mImageUrl = imageUrl;
    }
}
