package de.propra2.ausleiherino24.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.*;

import java.io.File;

public class ImageStoreServiceTest {
	private ImageStoreService imageStoreService;

	@Before
	public void init(){
		imageStoreService = new ImageStoreService("/test");
	}

	@Test
	public void uploadDirectoryDontExists(){
		imageStoreService.createUploadDirectoryIfNotExists();

		assertTrue(new File("/").exists());
		assertEquals(1, new File("/").listFiles().length);
	}
}
