package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.ConflictRepository;
import de.propra2.ausleiherino24.email.EmailSender;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Conflict;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.propayhandler.ReservationHandler;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ConflictServiceTest {

    private CaseService caseService;
    private EmailSender emailSender;
    private ConflictRepository conflictRepository;
    private ReservationHandler reservationHandler;
    private ConflictService conflictService;

    private User user;
    private User user2;
    private Article art;
    private Case ca;
    private Conflict c1;

    @Before
    public void init() {
        caseService = Mockito.mock(CaseService.class);
        emailSender = Mockito.mock(EmailSender.class);
        conflictRepository = Mockito.mock(ConflictRepository.class);
        reservationHandler = Mockito.mock(ReservationHandler.class);
        conflictService = Mockito
                .spy(new ConflictService(conflictRepository, emailSender, reservationHandler,
                        caseService));

        user = new User();
        user2 = new User();
        art = new Article();
        ca = new Case();
        c1 = new Conflict();

        user2.setUsername("user2");
        user.setUsername("user1");
        art.setOwner(user);
        ca.setArticle(art);
        ca.setReceiver(user2);
        ca.setId(1L);
        c1.setConflictedCase(ca);
        c1.setConflictReporterUsername("user1");
        c1.setConflictDescription("TestDescription");
    }

    @Test(expected = Exception.class)
    public void getConflictShouldThrowExceptionIfCalledWithWrongUser() throws Exception {
        Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.of(c1));

        conflictService.getConflict(1L, new User());
    }


    @Test()
    public void getConflictShouldReturnConflictCorrespondingToIdIfCalledWithArticleReceiver()
            throws Exception {
        Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.of(c1));

        Assertions.assertThat(conflictService.getConflict(1L, user)).isEqualTo(c1);
    }

    @Test()
    public void getConflictShouldReturnConflictCorrespondingToIdIfCalledWithArticleOwner()
            throws Exception {
        Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.of(c1));

        Assertions.assertThat(conflictService.getConflict(1L, user2)).isEqualTo(c1);
    }

    @Test()
    public void getConflictShouldReturnConflictCorrespondingToIdIfCalledWithAdmin()
            throws Exception {
        final User admin = new User();
        admin.setRole("admin");
        Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.of(c1));

        Assertions.assertThat(conflictService.getConflict(1L, admin)).isEqualTo(c1);
    }

    @Test(expected = Exception.class)
    public void getConflictShouldThrowExceptionIfConflictIdNotValid() throws Exception {
        Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.empty());
        conflictService.getConflict(1L, user);
    }

    @Test(expected = Exception.class)
    public void getConflictParticipantsShouldThrowExceptionIfCalledWithNull() throws Exception {
        conflictService.getConflictParticipants(null);
    }

    @Test
    public void getConflictParticipantsShouldReturnAllConflictParticipants() throws Exception {
        final List<User> conflictParticipants = conflictService.getConflictParticipants(c1);

        Assertions.assertThat(conflictParticipants.size()).isEqualTo(2);
        Assertions.assertThat(conflictParticipants).containsAll(Arrays.asList(user2, user));
    }

    @Test(expected = Exception.class)
    public void saveConflictShouldThrowExceptionIfConflictIsNull() throws Exception {
        conflictService.saveConflict(null, user);
    }

    @Test
    public void saveConflictShouldSaveConflictIfConflictIsNotNull() throws Exception {
        conflictService.saveConflict(c1, user);

        Mockito.verify(conflictRepository, Mockito.times(1)).save(c1);
    }

    @Test
    public void sendConflictEmailShouldSendConflictEmail() throws Exception {
        conflictService.sendConflictEmail(c1);

        Mockito.verify(emailSender, Mockito.times(1)).sendConflictEmail(c1);
    }

    @Test(expected = Exception.class)
    public void deactivateConflictShouldNotDeactivateConflictIfUserIsNotAdmin()
            throws Exception {
        c1.setId(1L);
        Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.of(c1));

        conflictService.deactivateConflict(1L, user2);

        Mockito.verify(conflictRepository, Mockito.times(0)).delete(c1);
    }

    @Test
    public void deactivateConflictShouldDeactivateConflictIfUserIsAdmin() throws Exception {
        final User admin = new User();
        admin.setRole("admin");
        c1.setId(1L);
        Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.of(c1));

        conflictService.deactivateConflict(1L, admin);

        Mockito.verify(conflictRepository, Mockito.times(1)).deleteById(c1.getId());
        Mockito.verify(emailSender, Mockito.times(1)).sendConflictEmail(c1);
        Mockito.verify(conflictRepository, Mockito.times(1)).save(c1);
        Assertions.assertThat(ca.getRequestStatus()).isEqualTo(Case.FINISHED);
    }

    @Test(expected = DataAccessException.class)
    public void deactivateConflictShouldThrowExceptionIfConflictNotFound() throws Exception {
        final User user = new User();

        Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.empty());

        conflictService.deactivateConflict(1L, user);
    }

    @Test()
    public void openConflictTest() throws Exception {
        conflictService.openConflict(ca, "TestDescription");
        Mockito.verify(caseService).conflictOpened(1L);
        Mockito.verify(conflictService).saveConflict(c1, user);
        Mockito.verify(conflictService).sendConflictEmail(c1);
    }

    @Test(expected = Exception.class)
    public void solveConflictShouldThrowExceptionIfUserNotAdmin() throws Exception {
        final User user = new User();
        user.setRole("");

        conflictService.solveConflict(null, user, null);
    }

    @Test
    public void solveConflictShouldPunishReservation() throws Exception {
        final User depoReceiver = new User();
        final User admin = new User();
        admin.setRole("admin");
        final Conflict conflict = Mockito.mock(Conflict.class);
        Mockito.when(conflict.getOwner()).thenReturn(depoReceiver);

        conflictService.solveConflict(conflict, admin, depoReceiver);

        Mockito.verify(reservationHandler).punishReservationByCase(null);
        Mockito.verify(reservationHandler, Mockito.times(0)).releaseReservationByCase(null);
    }

    @Test
    public void solveConflictShouldReleaseReservation() throws Exception {
        final User depoReceiver = new User();
        depoReceiver.setUsername("Hans");
        final User admin = new User();
        admin.setRole("admin");
        final Conflict conflict = Mockito.mock(Conflict.class);
        Mockito.when(conflict.getOwner()).thenReturn(new User());

        conflictService.solveConflict(conflict, admin, depoReceiver);

        Mockito.verify(reservationHandler, Mockito.times(0)).punishReservationByCase(null);
        Mockito.verify(reservationHandler).releaseReservationByCase(null);
    }
}
