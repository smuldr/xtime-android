package com.xebia.xtime.weekoverview;

import android.text.format.DateUtils;
import android.util.SparseArray;

import com.xebia.xtime.shared.model.DayOverview;
import com.xebia.xtime.shared.model.Project;
import com.xebia.xtime.shared.model.TimeCell;
import com.xebia.xtime.shared.model.TimeRegistration;
import com.xebia.xtime.shared.model.TimeSheetRow;
import com.xebia.xtime.shared.model.WeekOverview;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeSheetUtils {

    private static final int[] DAILY_INDEXES = new int[]{Calendar.MONDAY, Calendar.TUESDAY,
            Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY,
            Calendar.SUNDAY};

    public static List<DayOverview> dailyHours(WeekOverview overview, Date startDate) {

        // initialize array of entries for the week, indexed by Calendar.DAY_OF_WEEK
        SparseArray<DayOverview> dailyHoursArray = new SparseArray<DayOverview>();
        for (int i = 0; i < DAILY_INDEXES.length; i++) {
            Date date = new Date(startDate.getTime() + i * DateUtils.DAY_IN_MILLIS);
            dailyHoursArray.put(DAILY_INDEXES[i], new DayOverview(date, 0));
        }

        // fill the array with data from the overview
        for (TimeSheetRow row : overview.getTimeSheetRows()) {

            // find the project that is related to this row
            String projectId = row.getProjectId();
            Project rowProject = null;
            for (Project project : overview.getProjects()) {
                if (null != projectId && projectId.equals(project.getId())) {
                    rowProject = project;
                }
            }

            for (TimeCell timeCell : row.getTimeCells()) {
                // get day overview for this day from array
                Calendar entryCal = Calendar.getInstance();
                entryCal.setTime(timeCell.getEntryDate());
                DayOverview dayOverview = dailyHoursArray.get(entryCal.get(Calendar.DAY_OF_WEEK));

                // add time registration entry
                TimeRegistration timeReg = new TimeRegistration(rowProject, timeCell.getHour());
                dayOverview.getTimeRegistrations().add(timeReg);

                // increment total hours
                dayOverview.setTotalHours(dayOverview.getTotalHours() + timeCell.getHour());

                dailyHoursArray.put(entryCal.get(Calendar.DAY_OF_WEEK), dayOverview);
            }
        }

        // return list of totalHours, ordered by date
        List<DayOverview> result = new ArrayList<DayOverview>();
        for (int day : DAILY_INDEXES) {
            result.add(dailyHoursArray.get(day));
        }

        return result;
    }

}