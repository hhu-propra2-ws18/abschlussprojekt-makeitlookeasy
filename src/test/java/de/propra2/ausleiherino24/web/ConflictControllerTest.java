package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Conflict;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.CaseService;
import de.propra2.ausleiherino24.service.ConflictService;
import de.propra2.ausleiherino24.service.UserService;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class ConflictControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CaseService caseService;
    @MockBean
    private ConflictService conflictService;
    @MockBean
    private UserService userService;

    private User user;
    private User admin;
    private Case ca;
    private Conflict c1;
    private User user2;

    @BeforeEach
    void init() {
        user = new User();
        user2 = new User();
        admin = new User();
        Article art = new Article();
        ca = new Case();
        c1 = new Conflict();

        user2.setUsername("user2");
        user2.setRole("user");
        user.setUsername("user1");
        user.setRole("user");
        admin.setUsername("admin");
        admin.setRole("admin");
        art.setOwner(user);
        ca.setArticle(art);
        ca.setReceiver(user2);
        ca.setConflict(c1);
        ca.setStartTime(1234453521L);
        ca.setEndTime(143436432124L);
        ca.setPrice(200.0);
        c1.setId(2L);
        c1.setConflictedCase(ca);
        c1.setConflictReporterUsername("user1");
        c1.setConflictDescription("TestDescription");
    }

    @Test
    @WithMockUser(roles = "user")
    void sendConflictShouldSendConflictIfCorrespondingCaseIdIsValid() throws Exception {
        Mockito.when(caseService.findCaseById(1L)).thenReturn(ca);
        Mockito.when(caseService.isValidCase(1L)).thenReturn(true);

        mvc.perform(MockMvcRequestBuilders.post("/openconflict?id=1")
                .param("conflictDescription", "TestDescription"))
                .andExpect(
                        MockMvcResultMatchers.redirectedUrl("/myOverview?returned&openedConflict"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
        Mockito.verify(conflictService, Mockito.times(1)).openConflict(ca, "TestDescription");
    }

    @Test
    @WithMockUser(roles = "user")
    void sendConflictShouldNotSendConflictIfCorrespondingCaseIdIsNotValid()
            throws Exception {
        Mockito.when(caseService.isValidCase(1L)).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders.post("/openconflict?id=1")
                .param("conflictDescription", "TestDescription"))
                .andExpect(
                        MockMvcResultMatchers.redirectedUrl("/myOverview?returned&conflictFailed"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
        Mockito.verify(caseService, Mockito.times(0)).findCaseById(1L);
        Mockito.verify(conflictService, Mockito.times(0)).openConflict(ca, "TestDescription");
    }

    @Test
    @WithMockUser(roles = "user")
    void sendConflictShouldNotSendConflictIfExceptionIsThrown() throws Exception {
        Mockito.when(caseService.isValidCase(1L)).thenReturn(true);
        Mockito.when(caseService.findCaseById(1L)).thenReturn(ca);
        Mockito.doThrow(new AccessDeniedException("")).when(conflictService)
                .openConflict(ca, "TestDescription");

        mvc.perform(MockMvcRequestBuilders.post("/openconflict?id=1")
                .param("conflictDescription", "TestDescription"))
                .andExpect(
                        MockMvcResultMatchers.redirectedUrl("/myOverview?returned&conflictFailed"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
        Mockito.verify(caseService, Mockito.times(1)).findCaseById(1L);
        Mockito.verify(conflictService, Mockito.times(1)).openConflict(ca, "TestDescription");
    }

    @Test
    @WithMockUser(roles = "admin")
    void solveConflictOwnerShouldSolveConflictForOwnerIfPropayIsAvailable() throws Exception {
        Mockito.when(userService.findUserByPrincipal(Mockito.any(Principal.class)))
                .thenReturn(admin);
        Mockito.when(caseService.findCaseById(1L)).thenReturn(ca);
        Mockito.when(conflictService.getConflict(2L, admin)).thenReturn(c1);
        Mockito.when(conflictService.solveConflict(c1, admin, user)).thenReturn(true);

        mvc.perform(MockMvcRequestBuilders.post("/decideforowner?id=1"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers
                        .redirectedUrl("/conflicts"));
        Mockito.verify(conflictService, Mockito.times(1)).solveConflict(c1, admin, user);
        Mockito.verify(conflictService, Mockito.times(1)).deactivateConflict(2L, admin);
    }

    @Test
    @WithMockUser(roles = "admin")
    void solveConflictOwnerShouldNotSolveConflictForOwnerIfProparyIsUnavailable() throws Exception {
        Mockito.when(userService.findUserByPrincipal(Mockito.any(Principal.class)))
                .thenReturn(admin);
        Mockito.when(caseService.findCaseById(1L)).thenReturn(ca);
        Mockito.when(conflictService.getConflict(2L, admin)).thenReturn(c1);
        Mockito.when(conflictService.solveConflict(c1, admin, user)).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders.post("/decideforowner?id=1"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers
                        .redirectedUrl("/conflicts?propayUnavailable"));
        Mockito.verify(conflictService, Mockito.times(1)).solveConflict(c1, admin, user);
        Mockito.verify(conflictService, Mockito.times(0)).deactivateConflict(2L, admin);
    }

    @Test
    @WithMockUser(roles = "admin")
    void solveConflictReceiverShouldSolveConflictForReceiverIfPropayIsAvailable() throws Exception {
        Mockito.when(userService.findUserByPrincipal(Mockito.any(Principal.class)))
                .thenReturn(admin);
        Mockito.when(caseService.findCaseById(1L)).thenReturn(ca);
        Mockito.when(conflictService.getConflict(2L, admin)).thenReturn(c1);
        Mockito.when(conflictService.solveConflict(c1, admin, user2)).thenReturn(true);

        mvc.perform(MockMvcRequestBuilders.post("/decideforreceiver?id=1"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers
                        .redirectedUrl("/conflicts"));
        Mockito.verify(conflictService, Mockito.times(1)).solveConflict(c1, admin, user2);
        Mockito.verify(conflictService, Mockito.times(1)).deactivateConflict(2L, admin);
    }

    @Test
    @WithMockUser(roles = "admin")
    void solveConflictReceiverShouldNotSolveConflictForReceiverIfProparyIsUnavailable()
            throws Exception {
        Mockito.when(userService.findUserByPrincipal(Mockito.any(Principal.class)))
                .thenReturn(admin);
        Mockito.when(caseService.findCaseById(1L)).thenReturn(ca);
        Mockito.when(conflictService.getConflict(2L, admin)).thenReturn(c1);
        Mockito.when(conflictService.solveConflict(c1, admin, user2)).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders.post("/decideforreceiver?id=1"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers
                        .redirectedUrl("/conflicts?propayUnavailable"));
        Mockito.verify(conflictService, Mockito.times(1)).solveConflict(c1, admin, user2);
        Mockito.verify(conflictService, Mockito.times(0)).deactivateConflict(2L, admin);
    }

    @Test
    @WithMockUser(roles = "admin")
    void solveConflicts() throws Exception {
        Mockito.when(userService.findUserByPrincipal(Mockito.any(Principal.class)))
                .thenReturn(admin);
        Mockito.when(caseService.findAllCasesWithOpenConflicts()).thenReturn(Arrays.asList(ca));

        mvc.perform(MockMvcRequestBuilders.get("/conflicts"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model()
                        .attribute("user", Matchers.is(Matchers.equalTo(admin))))
                .andExpect(MockMvcResultMatchers.model()
                        .attribute("conflicts", Matchers.is(Matchers.equalTo(Arrays.asList(ca)))))
                .andExpect(MockMvcResultMatchers.view().name("/admin/conflict"));
    }
}
