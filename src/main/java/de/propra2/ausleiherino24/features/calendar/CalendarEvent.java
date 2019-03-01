package de.propra2.ausleiherino24.features.calendar;

import lombok.Data;

/**
 * Class for the calendar on the article view.
 */
@Data
public class CalendarEvent {

    protected String title;

    protected String start;

    protected String end;

    public CalendarEvent() {
        this.title = "Booked";
    }
}
