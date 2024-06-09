package com.example.finaltask;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
public class Pharm implements Parcelable {
    String name;
    Bitmap imageUrl;
    boolean box;
    public Pharm(String name, Bitmap imageUrl, boolean box) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.box = box;
    }
    protected Pharm(Parcel in) {
        name = in.readString();
        imageUrl = in.readParcelable(Bitmap.class.getClassLoader());
        box = in.readByte() != 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(imageUrl, flags);
        dest.writeByte((byte) (box ? 1 : 0));
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<Pharm> CREATOR = new Creator<Pharm>() {
        @Override
        public Pharm createFromParcel(Parcel in) {
            return new Pharm(in);
        }

        @Override
        public Pharm[] newArray(int size) {
            return new Pharm[size];
        }
    };
    public Object getName() {
        return name;
    }
}