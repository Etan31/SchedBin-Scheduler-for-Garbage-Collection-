package com.schedBin.a1project;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.HashSet;
import java.util.Set;

public class ScheduleDecorator implements DayViewDecorator {

    private final Set<CalendarDay> allScheduledDates;
    private Set<CalendarDay> filteredDates; // Add this variable
    private Context context;

    public ScheduleDecorator(Context context, Set<CalendarDay> scheduledDates) {
        this.context = context;
        this.allScheduledDates = scheduledDates;
        this.filteredDates = new HashSet<>(); // Initialize the set
    }

    // Add this method to update the filtered dates
    public void updateFilteredDates(Set<CalendarDay> newFilteredDates) {
        this.filteredDates.clear();
        this.filteredDates.addAll(newFilteredDates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // Check if the date has a schedule
        return filteredDates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        // Customize the appearance of the decorated date (e.g., add a dot)
        view.addSpan(new DotSpan(5, ContextCompat.getColor(context, R.color.blue)));
    }
}
