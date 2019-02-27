package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.CalendarEvent;
import de.propra2.ausleiherino24.model.Case;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This service is needed to deliver the needed calendar events to the FullCalendar.
 */
@Service
public class CalendarEventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleService.class);


    private final CaseRepository caseRepsitory;
    private final ArticleService articleService;

    /**
     * Autowired all needed repositorys and services.
     */
    @Autowired
    public CalendarEventService(CaseRepository caseRepository, ArticleService articleService) {
        this.caseRepsitory = caseRepository;
        this.articleService = articleService;

    }

    /**
     * Search all cases by article, than write them into a new CalendarEvent Object and return as
     * arrayList.
     */
    public ArrayList<CalendarEvent> getAllEventsFromOneArticle(final Long articleId) {
        final Article article = articleService.findArticleById(articleId);
        ArrayList<Case> allCases = caseRepsitory.findAllByArticle(article);
        ArrayList<CalendarEvent> allCalendarevents = new ArrayList<CalendarEvent>();
        for (Case c : allCases) {
            CalendarEvent calendarEvent = new CalendarEvent();
            calendarEvent.setStart(c.getStartTime());
            calendarEvent.setEnd(c.getEndTime());
        }
        return allCalendarevents;
    }

}
