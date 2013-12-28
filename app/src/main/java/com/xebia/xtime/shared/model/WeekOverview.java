package com.xebia.xtime.shared.model;


import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeekOverview implements Parcelable {

    public static final Creator<WeekOverview> CREATOR = new Creator<WeekOverview>() {

        @Override
        public WeekOverview createFromParcel(Parcel parcel) {
            return new WeekOverview(parcel);
        }

        @Override
        public WeekOverview[] newArray(int size) {
            return new WeekOverview[size];
        }
    };

    private Date mLastTransferredDate;
    private int mMonthDaysCount;
    private boolean mMonthlyDataApproved;
    private boolean mMonthlyDataTransferred;
    private List<Project> mProjects;
    private List<TimeSheetRow> mTimeSheetRows;
    private String mUsername;

    public WeekOverview(Date lastTransferredDate, int monthDaysCount,
                        boolean monthlyDataApproved, boolean monthlyDataTransferred,
                        List<Project> projects, List<TimeSheetRow> timeSheetRows, String username) {
        setLastTransferredDate(lastTransferredDate);
        setMonthDaysCount(monthDaysCount);
        setMonthlyDataApproved(monthlyDataApproved);
        setMonthlyDataTransferred(monthlyDataTransferred);
        setProjects(projects);
        setTimeSheetRows(timeSheetRows);
        setUsername(username);
    }

    protected WeekOverview(Parcel parcel) {
        long lastTransferredMillis = parcel.readLong();
        mLastTransferredDate = new Date(lastTransferredMillis);
        mMonthDaysCount = parcel.readInt();
        mMonthlyDataApproved = parcel.readInt() > 0;
        mMonthlyDataTransferred = parcel.readInt() > 0;
        mProjects = new ArrayList<Project>();
        parcel.readTypedList(mProjects, Project.CREATOR);
        mTimeSheetRows = new ArrayList<TimeSheetRow>();
        parcel.readTypedList(mTimeSheetRows, TimeSheetRow.CREATOR);
        mUsername = parcel.readString();
    }

    public List<Project> getProjects() {
        return mProjects;
    }

    public void setProjects(List<Project> projects) {
        mProjects = projects;
    }

    public List<TimeSheetRow> getTimeSheetRows() {
        return mTimeSheetRows;
    }

    public void setTimeSheetRows(List<TimeSheetRow> timeSheetRows) {
        this.mTimeSheetRows = timeSheetRows;
    }

    public Date getLastTransferredDate() {
        return mLastTransferredDate;
    }

    public void setLastTransferredDate(Date lastTransferredDate) {
        this.mLastTransferredDate = lastTransferredDate;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        this.mUsername = username;
    }

    public int getMonthDaysCount() {
        return mMonthDaysCount;
    }

    public void setMonthDaysCount(int monthDaysCount) {
        this.mMonthDaysCount = monthDaysCount;
    }

    public boolean isMonthlyDataApproved() {
        return mMonthlyDataApproved;
    }

    public void setMonthlyDataApproved(boolean monthlyDataApproved) {
        this.mMonthlyDataApproved = monthlyDataApproved;
    }

    public boolean isMonthlyDataTransferred() {
        return mMonthlyDataTransferred;
    }

    public void setMonthlyDataTransferred(boolean monthlyDataTransferred) {
        this.mMonthlyDataTransferred = monthlyDataTransferred;
    }

    public JSONObject toJson() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("lastTransferredDate", getLastTransferredDate().getTime());
        map.put("monthDaysCount", getMonthDaysCount());
        map.put("monthlyDataApproved", isMonthlyDataApproved());
        map.put("monthlyDataTransferred", isMonthlyDataTransferred());
        map.put("username", getUsername());

        JSONArray projects = new JSONArray();
        for (Project p : getProjects()) {
            projects.put(p.toJson());
        }
        map.put("projects", projects);

        JSONArray timeSheetRows = new JSONArray();
        for (TimeSheetRow t : getTimeSheetRows()) {
            timeSheetRows.put(t.toJson());
        }
        map.put("timeSheetRows", timeSheetRows);

        return new JSONObject(map);
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mLastTransferredDate.getTime());
        parcel.writeInt(mMonthDaysCount);
        parcel.writeInt(mMonthlyDataApproved ? 1 : 0);
        parcel.writeInt(mMonthlyDataTransferred ? 1 : 0);
        parcel.writeTypedList(mProjects);
        parcel.writeTypedList(mTimeSheetRows);
        parcel.writeString(mUsername);
    }
}