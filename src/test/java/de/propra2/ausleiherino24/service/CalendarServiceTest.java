package de.propra2.ausleiherino24.service;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.features.calendar.CalendarEvent;
import de.propra2.ausleiherino24.features.calendar.CalendarEventService;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class CalendarServiceTest {

    private CaseRepository caseRepsitory;
    private ArticleService articleService;
    private CalendarEventService calendarEventService;

    @Before
    public void init() {
        caseRepsitory = mock(CaseRepository.class);
        articleService = mock(ArticleService.class);
        this.calendarEventService = new CalendarEventService(caseRepsitory, articleService);
    }

    @Test
    public void testIfGetAllEventsFromOneArticleReturnsArrayListWithOneCalendarEventItem() {
        Article article = new Article();
        when(articleService.findArticleById(0L)).thenReturn(article);
        Case acase = new Case();
        acase.setArticle(article);
        Date date = new Date();
        acase.setStartTime(date.getTime());
        acase.setEndTime(date.getTime() + 87000000);
        ArrayList<Case> cases = new ArrayList<Case>();
        cases.add(acase);
        when(caseRepsitory.findAllByArticleWhereStatusIsGreater7(article)).thenReturn(cases);
        List<CalendarEvent> cal = calendarEventService.getAllEventsFromOneArticle(0L);
        assertEquals(cal.get(0).getStart(), java.util.Optional.of(date.getTime()).get());
    }
}