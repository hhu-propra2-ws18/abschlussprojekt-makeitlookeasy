package de.propra2.ausleiherino24.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import de.propra2.ausleiherino24.data.ConflictRepository;
import de.propra2.ausleiherino24.features.email.EmailSender;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Conflict;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.propayhandler.data.ReservationHandler;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class ConflictServiceTest {

    private CaseService caseService;
    private EmailSender emailSender;
    private ConflictRepository conflictRepository;
    private ReservationHandler reservationHandler;
    private ConflictService conflictService;

    private User user;
    private User user2;
    private Case ca;
    private Conflict c1;

    @BeforeEach
    void init() {
        caseService = Mockito.mock(CaseService.class);
        emailSender = Mockito.mock(EmailSender.class);
        conflictRepository = Mockito.mock(ConflictRepository.class);
        reservationHandler = Mockito.mock(ReservationHandler.class);
        conflictService = Mockito
                .spy(new ConflictService(conflictRepository, emailSender, reservationHandler,
                        caseService));

        user = new User();
        user2 = new User();
        Article art = new Article();
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

        Mockito.when(reservationHandler.checkAvailability()).thenReturn(true);
    }

    @Test
    void getConflictShouldThrowExceptionIfCalledWithWrongUser() {

        assertThrows(AccessDeniedException.class, () -> {
            Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.of(c1));
            conflictService.getConflict(1L, new User());
        });


    }

    @Test
    void getConflictShouldReturnConflictCorrespondingToIdIfCalledWithArticleReceiver()
            throws Exception {
        Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.of(c1));

        Assertions.assertThat(conflictService.getConflict(1L, user)).isEqualTo(c1);
    }

    @Test
    void getConflictShouldReturnConflictCorrespondingToIdIfCalledWithArticleOwner()
            throws Exception {
        Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.of(c1));

        Assertions.assertThat(conflictService.getConflict(1L, user2)).isEqualTo(c1);
    }

    @Test
    void getConflictShouldReturnConflictCorrespondingToIdIfCalledWithAdmin() throws Exception {
        final User admin = new User();
        admin.setRole("admin");
        Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.of(c1));

        Assertions.assertThat(conflictService.getConflict(1L, admin)).isEqualTo(c1);
    }

    @Test
    void getConflictShouldThrowExceptionIfConflictIdNotValid() {

        assertThrows(DataAccessException.class, () -> {
            Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.empty());
            conflictService.getConflict(1L, user);
        });

    }

    @Test
    void getConflictParticipantsShouldThrowExceptionIfCalledWithNull() {

        assertThrows(NullPointerException.class,
                () -> conflictService.getConflictParticipants(null));


    }

    @Test
    void getConflictParticipantsShouldReturnAllConflictParticipants() {
        final List<User> conflictParticipants = conflictService.getConflictParticipants(c1);

        Assertions.assertThat(conflictParticipants.size()).isEqualTo(2);
        Assertions.assertThat(conflictParticipants).containsAll(Arrays.asList(user2, user));
    }

    @Test
    void saveConflictShouldThrowExceptionIfConflictIsNull() {
        assertThrows(NullPointerException.class, () -> conflictService.saveConflict(null, user));

    }

    @Test
    void saveConflictShouldSaveConflictIfConflictIsNotNull() throws Exception {
        conflictService.saveConflict(c1, user);

        Mockito.verify(conflictRepository, Mockito.times(1)).save(c1);
    }

    @Test
    void sendConflictEmailShouldSendConflictEmail() {
        conflictService.sendConflictEmail(c1);

        Mockito.verify(emailSender, Mockito.times(1)).sendConflictEmail(c1);
    }

    @Test
    void deactivateConflictShouldNotDeactivateConflictIfUserIsNotAdmin() {

        assertThrows(DataAccessException.class, () -> {
            c1.setId(1L);
            Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.of(c1));
            conflictService.deactivateConflict(1L, user2);
            Mockito.verify(conflictRepository, Mockito.times(0)).delete(c1);
        });

    }

    @Test
    void deactivateConflictShouldDeactivateConflictIfUserIsAdmin() {
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

    @Test
    void deactivateConflictShouldThrowExceptionIfConflictNotFound() {

        assertThrows(DataAccessException.class, () -> {
            final User user = new User();

            Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.empty());

            conflictService.deactivateConflict(1L, user);
        });

    }

    @Test
    void openConflictTest() throws Exception {
        conflictService.openConflict(ca, "TestDescription");
        Mockito.verify(caseService).conflictOpened(1L);
        Mockito.verify(conflictService).saveConflict(c1, user);
        Mockito.verify(conflictService).sendConflictEmail(c1);
    }

    @Test
    void solveConflictShouldThrowExceptionIfUserNotAdmin() {

        assertThrows(AccessDeniedException.class, () -> {
            final User user = new User();
            user.setRole("");

            conflictService.solveConflict(null, user, null);
        });

    }

    @Test
    void solveConflictShouldPunishReservation() throws Exception {
        final User depositReceiver = new User();
        final User admin = new User();
        admin.setRole("admin");
        final Conflict conflict = Mockito.mock(Conflict.class);
        Mockito.when(conflict.getOwner()).thenReturn(depositReceiver);

        conflictService.solveConflict(conflict, admin, depositReceiver);

        Mockito.verify(reservationHandler).punishReservationByCase(null);
        Mockito.verify(reservationHandler, Mockito.times(0)).releaseReservationByCase(null);
    }

    @Test
    void solveConflictShouldReleaseReservation() throws Exception {
        final User depositReceiver = new User();
        depositReceiver.setUsername("Hans");
        final User admin = new User();
        admin.setRole("admin");
        final Conflict conflict = Mockito.mock(Conflict.class);
        Mockito.when(conflict.getOwner()).thenReturn(new User());

        conflictService.solveConflict(conflict, admin, depositReceiver);

        Mockito.verify(reservationHandler, Mockito.times(0)).punishReservationByCase(null);
        Mockito.verify(reservationHandler).releaseReservationByCase(null);
    }
}
