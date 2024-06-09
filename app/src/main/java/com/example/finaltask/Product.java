package com.example.finaltask;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
public class Product implements Parcelable {
    String name;
    int ind, price;
    Bitmap imageUrl;
    boolean box;
    public Product(String name, int ind, int price, Bitmap imageUrl, boolean box) {
        this.name = name;
        this.ind = ind;
        this.price = price;
        this.imageUrl = imageUrl;
        this.box = box;
    }
    protected Product(Parcel in) {
        name = in.readString();
        ind = in.readInt();
        price = in.readInt();
        imageUrl = in.readParcelable(Bitmap.class.getClassLoader());
        box = in.readByte() != 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(ind);
        dest.writeInt(price);
        dest.writeParcelable(imageUrl, flags);
        dest.writeByte((byte) (box ? 1 : 0));
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }
        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
    public String getName() {
        return name;
    }
}