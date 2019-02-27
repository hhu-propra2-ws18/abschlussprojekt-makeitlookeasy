package de.propra2.ausleiherino24.service;
import static org.mockito.Mockito.*;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class CalendarServiceTest {
    private CaseRepository caseRepsitory;
    private ArticleService articleService;
    private CalendarEventService calendarEventService;

    @Before
    public void init(){
        caseRepsitory = mock(CaseRepository.class);
        articleService = mock(ArticleService.class);
        this.calendarEventService = new CalendarEventService(caseRepsitory,articleService);
    }

    @Test
    public void test(){
        Article article = new Article();
        when(articleService.findArticleById(0L)).thenReturn(article);
        calendarEventService.getAllEventsFromOneArticle(0L);
    }
}
