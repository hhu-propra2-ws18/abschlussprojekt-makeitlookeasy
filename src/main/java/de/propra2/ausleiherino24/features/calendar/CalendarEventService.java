package de.propra2.ausleiherino24.features.calendar;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.service.ArticleService;
import java.util.ArrayList;
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
     * Search all cases by article, than write them into a new CalendarEvent Object and return as
     * arrayList.
     */
    public List<CalendarEvent> getAllEventsFromOneArticle(final Long articleId) {
        final Article article = articleService.findArticleById(articleId);
        final List<Case> allCases = caseRepository.findAllByArticle(article);
        final List<CalendarEvent> allCalendarEvents = new ArrayList<>();
        for (final Case calendarCase : allCases) {
            final CalendarEvent calendarEvent = new CalendarEvent();
            calendarEvent.setStart(calendarCase.getStartTime());
            calendarEvent.setEnd(calendarCase.getEndTime());
        }
        return allCalendarEvents;
    }

}
