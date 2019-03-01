package de.propra2.ausleiherino24.features.calendar;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.service.ArticleService;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This service is needed to deliver the needed calendar events to the FullCalendar.
 */
@Service
public class CalendarEventService {

    private final CaseRepository caseRepository;
    private final ArticleService articleService;

    /**
     * Autowired all needed repositories and services.
     */
    @Autowired
    public CalendarEventService(CaseRepository caseRepository, ArticleService articleService) {
        this.caseRepository = caseRepository;
        this.articleService = articleService;

    }

    /**
     * Search all cases by article, then write them into a new CalendarEvent Object and return as
     * arrayList. End day need to be set to a time greater than 0:0 because the calendar otherwise
     * does not mark the last day.
     */
    public List<CalendarEvent> getAllEventsFromOneArticle(final Long articleId) {
        final Article article = articleService.findArticleById(articleId);
        final List<Case> allCases = caseRepository.findAllByArticleWhereStatusIsGreater7(article);
        final List<CalendarEvent> allCalendarEvents = new ArrayList<>();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        for (final Case calendarCase : allCases) {
            final CalendarEvent calendarEvent = new CalendarEvent();
            Date start = new Date(calendarCase.getStartTime());
            Date end = new Date(calendarCase.getEndTime());
            calendarEvent.setStart(formatter.format(start));
            ;
            calendarEvent.setEnd(formatter.format(end) + "T23:59:59.008");
            allCalendarEvents.add(calendarEvent);
        }
        return allCalendarEvents;
    }

}
