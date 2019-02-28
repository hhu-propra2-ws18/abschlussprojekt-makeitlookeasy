package de.propra2.ausleiherino24.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.propra2.ausleiherino24.category.Category;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.CustomerReview;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.CustomerReviewService;
import de.propra2.ausleiherino24.service.UserService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

    private ArticleService articleService;
    private UserService userService;
    private CustomerReviewService customerReviewService;
    private User user;
    private Article article;

    @BeforeEach
    void setup() {
        articleService = mock(ArticleService.class);
        customerReviewService = mock(CustomerReviewService.class);
        userService = mock(UserService.class);

        user = new User();
        user.setUsername("user");
        user.setRole("user");
        user.setPerson(mock(Person.class));

        article = new Article();
        article.setId(1L);
        article.setOwner(user);
    }

    @Disabled
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

}
