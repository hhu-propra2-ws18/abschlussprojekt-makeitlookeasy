package de.propra2.ausleiherino24.service;

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

import de.propra2.ausleiherino24.data.ConflictRepository;
import de.propra2.ausleiherino24.email.EmailSender;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Conflict;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.propayhandler.ReservationHandler;

@RunWith(SpringRunner.class)
public class ConflictServiceTest {
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
		emailSender = Mockito.mock(EmailSender.class);
		conflictRepository = Mockito.mock(ConflictRepository.class);
		reservationHandler = Mockito.mock(ReservationHandler.class);
		conflictService = new ConflictService(conflictRepository, emailSender, reservationHandler);

		user = new User();
		user2 = new User();
		art = new Article();
		ca = new Case();
		c1 = new Conflict();

		user2.setUsername("user2");
		user.setUsername("user1");
		art.setOwner(user2);
		ca.setArticle(art);
		ca.setReceiver(user);
		c1.setConflictedCase(ca);
		c1.setConflictReporterUsername("user1");

	}

	@Test(expected=Exception.class)
	public void isConflictedArticleOwnerShouldThrowExceptionIfUserIsNull() throws Exception {
		user = null;
		c1 = new Conflict();

		conflictService.isConflictedArticleOwner(c1, user);
	}

	@Test
	public void isConflictedArticleOwnerShouldReturnTrueIfUserIsOwnerOfConflictedArticle() throws Exception {
		art.setOwner(user);

		Assertions.assertThat(conflictService.isConflictedArticleOwner(c1, user)).isTrue();
	}

	@Test
	public void isConflictedArticleOwnerShouldReturnFalseIfUserIsOwnerOfConflictedArticle() throws Exception {
		art.setOwner(user);

		Assertions.assertThat(conflictService.isConflictedArticleOwner(c1, user2)).isFalse();
	}

	@Test(expected=Exception.class)
	public void getConflictShouldThrowExceptionIfCalledWithWrongUser() throws Exception {
		Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.of(c1));

		conflictService.getConflict(1L, new User());
	}


	@Test()
	public void getConflictShouldReturnConflictCorrespondingToIdIfCalledWithArticleReceiver() throws Exception {
		Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.of(c1));

		Assertions.assertThat(conflictService.getConflict(1L, user)).isEqualTo(c1);
	}

	@Test()
	public void getConflictShouldReturnConflictCorrespondingToIdIfCalledWithArticleOwner() throws Exception {
		Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.of(c1));

		Assertions.assertThat(conflictService.getConflict(1L, user2)).isEqualTo(c1);
	}

	@Test()
	public void getConflictShouldReturnConflictCorrespondingToIdIfCalledWithAdmin() throws Exception {
		User admin = new User();
		admin.setRole("admin");
		Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.of(c1));

		Assertions.assertThat(conflictService.getConflict(1L, admin)).isEqualTo(c1);
	}

	@Test(expected=Exception.class)
	public void getConflictShouldThrowExceptionIfConflictIdNotValid() throws Exception {
		Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.empty());
		conflictService.getConflict(1L, user);
	}

	@Test(expected=Exception.class)
	public void getConflictParticipantsShouldThrowExceptionIfCalledWithNull() throws Exception {
		conflictService.getConflictParticipants(null);
	}

	@Test
	public void getConflictParticipantsShouldReturnAllConflictParticipants() throws Exception {
		List<User> conflictParticipants = conflictService.getConflictParticipants(c1);

		Assertions.assertThat(conflictParticipants.size()).isEqualTo(2);
		Assertions.assertThat(conflictParticipants).containsAll(Arrays.asList(user2, user));
	}

	@Test
	public void getAllConflictsByUserShouldReturnListOfAllCurrentConflictsByUser() throws Exception {
		Article art2 = new Article();
		art2.setOwner(new User());
		Case ca2 = new Case();
		ca2.setArticle(art2);
		ca2.setReceiver(user2);
		Conflict c2 = new Conflict();
		c2.setConflictedCase(ca2);

		Mockito.when(conflictRepository.findAllByArticleOwner(user2)).thenReturn(Arrays.asList(c1));
		Mockito.when(conflictRepository.findAllByReceiver(user2)).thenReturn(Arrays.asList(c2));

		List<Conflict> allConflictsByUser = conflictService.getAllConflictsByUser(user2);

		Assertions.assertThat(allConflictsByUser).containsAll(Arrays.asList(c2,c1));
	}

	@Test(expected=Exception.class)
	public void saveConflictShouldThrowExceptionIfConflictIsNull() throws Exception {
		User user = new User();
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

		Mockito.verify(emailSender, Mockito.times(1)).sendEmail(c1);
	}

	@Test
	public void deactivateConflictShouldDeactivateConflictIfUserIsConflictReporter() throws Exception {
		c1.setId(1L);
		Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.of(c1));

		conflictService.deactivateConflict(1L, user);

		Mockito.verify(conflictRepository, Mockito.times(1)).delete(c1);
	}

	@Test(expected=Exception.class)
	public void deactivateConflictShouldNotDeactivateConflictIfUserIsNotConflictReporter() throws Exception {
		c1.setId(1L);
		Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.of(c1));

		conflictService.deactivateConflict(1L, user2);

		Mockito.verify(conflictRepository, Mockito.times(0)).delete(c1);
	}

	@Test
	public void deactivateConflictShouldDeactivateConflictIfUserIsAdmin() throws Exception {
		User admin = new User();
		admin.setRole("admin");
		c1.setId(1L);
		Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.of(c1));

		conflictService.deactivateConflict(1L, admin);

		Mockito.verify(conflictRepository, Mockito.times(1)).delete(c1);
	}

	@Test(expected=DataAccessException.class)
	public void deactivateConflictShouldThrowExceptionIfConflictNotFound() throws Exception {
		User user = new User();

		Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.empty());

		conflictService.deactivateConflict(1L, user);
	}
}
