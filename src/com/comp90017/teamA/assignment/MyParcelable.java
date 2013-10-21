package com.comp90017.teamA.assignment;

import com.DistanceEstimator;

import android.os.Parcel;
import android.os.Parcelable;

public class MyParcelable extends DistanceEstimator implements Parcelable{
	
	public MyParcelable(Parcel in) {
		// TODO Auto-generated constructor stub
		if ( in != null ){
			dl1l2 = in.readDouble();
			dl1l3 = in.readDouble();
			dl2l3 = in.readDouble();
			r1 = in.readDouble();
			r2 = in.readDouble();
			r3 = in.readDouble();
		}
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeDouble(dl1l2);
		arg0.writeDouble(dl1l3);
		arg0.writeDouble(dl2l3);
		arg0.writeDouble(r1);
		arg0.writeDouble(r2);
		arg0.writeDouble(r3);
		
	}

	 // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<MyParcelable> CREATOR = new Parcelable.Creator<MyParcelable>() {
        public MyParcelable createFromParcel(Parcel in) {
            return new MyParcelable(in);
        }

        public MyParcelable[] newArray(int size) {
            return new MyParcelable[size];
        }
    };
    
    
}
