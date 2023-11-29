package com.example.a1project;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.HashSet;

public class ScheduleDecorator implements DayViewDecorator {

    private HashSet<CalendarDay> scheduledDates;
    private Context context;

    public ScheduleDecorator(Context context, HashSet<CalendarDay> scheduledDates) {
        this.context = context;
        this.scheduledDates = scheduledDates;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // Check if the date has a schedule
        return scheduledDates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        // Customize the appearance of the decorated date (e.g., add a dot)
        view.addSpan(new DotSpan(5, ContextCompat.getColor(context, R.color.green)));
    }
}
