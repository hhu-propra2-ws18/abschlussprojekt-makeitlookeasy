package de.propra2.ausleiherino24.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.*;

public class ImageStoreServiceTest {
	private ImageStoreService imageStoreService;

	@Before
	public void init(){
		imageStoreService = new ImageStoreService("/");
	}
}
