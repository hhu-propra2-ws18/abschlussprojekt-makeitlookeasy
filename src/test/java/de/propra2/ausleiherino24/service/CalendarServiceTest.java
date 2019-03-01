package de.propra2.ausleiherino24.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.features.calendar.CalendarEvent;
import de.propra2.ausleiherino24.features.calendar.CalendarEventService;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class CalendarServiceTest {

    private CaseRepository caseRepsitory;
    private ArticleService articleService;
    private CalendarEventService calendarEventService;

    @BeforeEach
    public void init() {
        caseRepsitory = mock(CaseRepository.class);
        articleService = mock(ArticleService.class);
        this.calendarEventService = new CalendarEventService(caseRepsitory, articleService);
    }

    @Test
    public void testIfGetAllEventsFromOneArticleReturnsArrayListWithOneCalendarEventItem() {
        Article article = new Article();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
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
        assertEquals(formatter.format(date), cal.get(0).getStart());
        assertEquals(formatter.format(date.getTime() + 87000000) + "T23:59:59.008",
                cal.get(0).getEnd());
    }
}
