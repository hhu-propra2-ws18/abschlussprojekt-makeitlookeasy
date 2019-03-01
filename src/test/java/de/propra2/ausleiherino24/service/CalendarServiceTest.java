package de.propra2.ausleiherino24.service;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.features.calendar.CalendarEvent;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

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
        when(caseRepsitory.findAllByArticle(article)).thenReturn(cases);
        ArrayList<CalendarEvent> cal = calendarEventService.getAllEventsFromOneArticle(0L);
        assertEquals(cal.get(0).getStart(), java.util.Optional.of(date.getTime()).get());
    }
}
