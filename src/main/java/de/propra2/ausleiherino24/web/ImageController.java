package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.service.ImageStoreService;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ImageController {

	private ImageStoreService imageStorageService;

	@Autowired
	public ImageController(ImageStoreService imageStorageService) {
		this.imageStorageService = imageStorageService;
	}

	@GetMapping("/imageupload")
	public String fileUpload() {
		return "/imageupload";
	}

	@PostMapping("/imageupload")
	public String handleFileUpload(@RequestParam("file") MultipartFile file) {
		imageStorageService.store(file, null);
		return "redirect:/imageupload";
	}

	/**
	 * TODO Javadoc
	 */
	@GetMapping("/images/{fileName}")
	public void getImage(@PathVariable String fileName, HttpServletResponse response)
			throws IOException {
		File requestedFile = imageStorageService.getFile(fileName, null);

		if (requestedFile == null) {
			response.setStatus(404);
			return;
		}

		response.setContentType(Files.probeContentType(requestedFile.toPath()));
		IOUtils.copy(
				new DataInputStream(new FileInputStream(requestedFile)),
				response.getOutputStream()
		);
	}
}
