package com.xebia.xtime.shared.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a type of work. Each work type is identified by its ID and its description.
 */
public class WorkType implements Parcelable {

    public static final Creator<WorkType> CREATOR = new Creator<WorkType>() {
        @Override
        public WorkType createFromParcel(Parcel parcel) {
            return new WorkType(parcel);
        }

        @Override
        public WorkType[] newArray(int size) {
            return new WorkType[size];
        }
    };
    private String mId;
    private String mDescription;

    public WorkType(String id, String description) {
        mId = id;
        mDescription = description;
    }

    protected WorkType(Parcel parcel) {
        mId = parcel.readString();
        mDescription = parcel.readString();
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mId);
        parcel.writeString(mDescription);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof WorkType) {
            return mId.equals(((WorkType) o).getId()) &&
                    mDescription.equals(((WorkType) o).getDescription());
        }
        return super.equals(o);
    }

    @Override
    public String toString() {
        // just returning the work type description makes it possible to create a list of work
        // types without having to build a special adapter
        return mDescription;
    }
}
