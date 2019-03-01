package de.propra2.ausleiherino24.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.CaseService;
import de.propra2.ausleiherino24.service.CustomerReviewService;
import java.security.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
class CaseControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CustomerReviewService customerReviewService;

    @MockBean
    private CaseService caseService;


    private User user;
    private Article article;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUsername("user");
        user.setRole("user");
        user.setPerson(mock(Person.class));

        article = mock(Article.class);
        article.setId(1L);
    }

    @Test
    @WithMockUser(roles = "user")
    void successfulRedirectionBookArticle() throws Exception {
        Mockito.when(caseService.requestArticle(1L, 1L, 2L,
                "user")).thenReturn(true);

        mvc.perform(MockMvcRequestBuilders.post("/bookArticle").param("id", "1")
                .param("startDate", "1", "endDate", "2"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "user")
    void successfulRedirectionBuyArticle() throws Exception {
        Mockito.when(caseService.sellArticle(ArgumentMatchers.eq(1L), any(Principal.class)))
                .thenReturn(true);

        mvc.perform(MockMvcRequestBuilders.post("/buyArticle").param("articleId", "1"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "user")
    void successfulRedirectionAcceptCase() throws Exception {
        Mockito.when(caseService.acceptArticleRequest(ArgumentMatchers.eq(1L))).thenReturn(1);

        mvc.perform(MockMvcRequestBuilders.post("/acceptCase").param("id", "1"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }


    @Test
    @WithMockUser(roles = "user")
    void successfulRedirectionDeclineCase() throws Exception {
        Mockito.when(caseService.declineArticleRequest(ArgumentMatchers.eq(1L))).thenReturn(true);

        mvc.perform(MockMvcRequestBuilders.post("/declineCase").param("id", "1"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }


    @Test
    @WithMockUser(roles = "user")
    void successfulRedirectionAcceptCaseReturn() throws Exception {
        Mockito.when(caseService.acceptCaseReturn(ArgumentMatchers.eq(1L))).thenReturn(true);

        mvc.perform(MockMvcRequestBuilders.post("/acceptCaseReturn").param("id", "1"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }


    @Test
    @WithMockUser(roles = "user")
    void successfulRedirectionWriteReview() throws Exception {
        Mockito.when(caseService.findCaseById(ArgumentMatchers.eq(1L))).thenReturn(new Case());

        mvc.perform(MockMvcRequestBuilders.post("/acceptCase").param("id", "1"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }


}
