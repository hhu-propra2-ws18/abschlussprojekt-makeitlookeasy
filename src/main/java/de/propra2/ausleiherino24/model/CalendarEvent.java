package de.propra2.ausleiherino24.model;

import lombok.Data;

/**
 * Class for the calendar on the article view
 */
@Data
public class CalendarEvent {
    protected String title;

    protected Long start;

    protected long end;

    public CalendarEvent(){
        this.title = "Booked";
    }
}
