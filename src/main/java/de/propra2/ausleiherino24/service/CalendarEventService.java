package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.CalendarEvent;
import de.propra2.ausleiherino24.model.Case;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This service is needed to deliver the needed calendar events to the FullCalendar.
 */
@Service
public class CalendarEventService {


    private final ArticleRepository articleRepository;
    private final CaseRepository caseRepsitory;
    private final ArticleService articleService;

    /**
     * Autowired all needed repositorys and services.
     */
    @Autowired
    public CalendarEventService(ArticleRepository articleRepository,
            CaseRepository caseRepository, ArticleService articleService) {
        this.articleRepository = articleRepository;
        this.caseRepsitory = caseRepository;
        this.articleService = articleService;

    }

    /**
     * Search all cases by article, than write them into a new CalendarEvent Object and return as
     * arrayList.
     */
    public List<CalendarEvent> getAllEventsFromOneArticle(final Long articleId) {
        final Article article = articleService.findArticleById(articleId);
        final ArrayList<Case> allCases = caseRepsitory.findAllByArticle(article);
        final ArrayList<CalendarEvent> allCalendarevents = new ArrayList<CalendarEvent>();
        for (final Case calendarCase : allCases) {
            final CalendarEvent calendarEvent = new CalendarEvent();
            calendarEvent.setStart(calendarCase.getStartTime());
            calendarEvent.setEnd(calendarCase.getEndTime());
        }
        return allCalendarevents;
    }

}
