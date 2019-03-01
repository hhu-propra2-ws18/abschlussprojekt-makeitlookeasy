package de.propra2.ausleiherino24.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.propra2.ausleiherino24.features.calendar.CalendarEvent;
import de.propra2.ausleiherino24.features.calendar.CalendarEventService;
import de.propra2.ausleiherino24.features.category.Category;
import de.propra2.ausleiherino24.features.imageupload.ImageService;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.CustomerReview;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.CustomerReviewService;
import de.propra2.ausleiherino24.service.UserService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = "test")
@SpringBootTest
@AutoConfigureMockMvc
class ArticleControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private ArticleService articleService;
    @MockBean
    private UserService userService;
    @MockBean
    private CustomerReviewService customerReviewService;
    @MockBean
    private CalendarEventService calendarEventService;
    @MockBean
    private ImageService imageService;
    private User user;
    private Article article;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUsername("user");
        user.setRole("user");
        user.setPerson(mock(Person.class));

        article = new Article();
        article.setId(1L);
        article.setOwner(user);
    }

    @Test
    @WithMockUser(roles = "user")
    void successfullyDisplayArticleTest() throws Exception {
        final List<Category> allCategories = Category.getAllCategories();
        final List<CustomerReview> allReviews = new ArrayList<>();

        when(userService.findUserByPrincipal(any(Principal.class))).thenReturn(user);
        when(articleService.findArticleById(1L)).thenReturn(article);
        when(customerReviewService.findAllReviews()).thenReturn(allReviews);

        mvc.perform(MockMvcRequestBuilders.get("/article?id=1"))
                .andExpect(MockMvcResultMatchers
                        .status().isOk())
                .andExpect(MockMvcResultMatchers
                        .view().name("/shop/item"))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("user", user))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("article", article))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("categories", allCategories))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("review", allReviews));
    }

    @Test
    @WithMockUser(roles = "user")
    void createNewArticleShouldCreateNewArticle() throws Exception {
        final List<Category> allCategories = Category.getAllCategories();

        when(userService.findUserByPrincipal(any(Principal.class))).thenReturn(user);

        mvc.perform(MockMvcRequestBuilders.get("/article/create"))
                .andExpect(MockMvcResultMatchers
                        .status().isOk())
                .andExpect(MockMvcResultMatchers
                        .view().name("/shop/newItem"))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("user", user))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("article", new Article()))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("categories", allCategories));
    }

    @Test
    @WithMockUser(roles = "user")
    void saveNewArticleShouldSaveArticleAndImageIfImageIsNotNull() throws Exception {
        Article expected = new Article();
        expected.setId(1L);
        expected.setOwner(user);
        expected.setActive(true);
        expected.setForRental(true);
        expected.setForSale(false);
        expected.setImage("test");
        String name = "image";
        byte[] data = new byte[1];
        MockMultipartFile imageMock = new MockMultipartFile(name, data);

        when(imageService.store(imageMock, null)).thenReturn("test");
        when(userService.findUserByPrincipal(any(Principal.class))).thenReturn(user);
        mvc.perform(MockMvcRequestBuilders.multipart("/article/saveNew")
                .file(imageMock)
                .flashAttr("article", article))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));
        Mockito.verify(articleService, Mockito.times(1)).saveArticle(expected, "Created");
    }

    @Test
    @WithMockUser(roles = "user")
    void saveUpdatedShouldSaveUpdatedArticle() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put("/article/saveUpdated")
                .flashAttr("article", article))
                .andExpect(MockMvcResultMatchers
                        .status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers
                        .redirectedUrl("/myOverview?articles&updatedArticle"));
        Mockito.verify(articleService, Mockito.times(1)).saveArticle(article, "Updated");
    }

    @Test
    @WithMockUser(roles = "user")
    void deactivateArticleShouldDeactivateArticleIfAllArticleCasesAreClosed() throws Exception {
        when(articleService.deactivateArticle(1L)).thenReturn(true);

        mvc.perform(MockMvcRequestBuilders.put("/article/deactivate?id=1"))
                .andExpect(MockMvcResultMatchers
                        .status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers
                        .redirectedUrl("/myOverview?articles&deactivatedArticle"));
        Mockito.verify(articleService, Mockito.times(1)).deactivateArticle(1L);
    }

    @Test
    @WithMockUser(roles = "user")
    void deactivateArticleShouldNotDeactivateArticleIfSomeArticleCasesAreNotClosed()
            throws Exception {
        when(articleService.deactivateArticle(1L)).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders.put("/article/deactivate?id=1"))
                .andExpect(MockMvcResultMatchers
                        .status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers
                        .redirectedUrl("/myOverview?articles&deactivationFailed"));
        Mockito.verify(articleService, Mockito.times(1)).deactivateArticle(1L);
    }

    @Test
    @WithMockUser(roles = "user")
    void getEventsForCalendarShouldReturnAllEventsFromOneArticle() throws Exception {
        CalendarEvent event1 = new CalendarEvent();
        event1.setEnd("1998/02/18");
        event1.setTitle("title1");
        event1.setStart("1998/02/16");
        CalendarEvent event2 = new CalendarEvent();
        event2.setEnd("1998/02/20");
        event2.setTitle("title2");
        event2.setStart("1998/02/19");
        Mockito.when(calendarEventService.getAllEventsFromOneArticle(1L))
                .thenReturn(Arrays.asList(event1, event2));
        mvc.perform(MockMvcRequestBuilders.get("/article/events?id=1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].end").value("1998/02/18"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].title").value("title1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].start").value("1998/02/16"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].end").value("1998/02/20"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].title").value("title2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].start").value("1998/02/19"));

    }

}
