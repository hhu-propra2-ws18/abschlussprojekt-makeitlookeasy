package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.service.ImageStoreService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;

@Controller
public class ImageController {
	private ImageStoreService imageStorageService;

	@Autowired
	public ImageController(ImageStoreService imageStorageService) {
		this.imageStorageService = imageStorageService;
	}

	@GetMapping("/imageupload")
	public String fileUpload() {
		return "/imageUploadSketch";
	}

	@PostMapping("/imageupload")
	public String handleFileUpload(@RequestParam("file") MultipartFile file) {
		imageStorageService.store(file, null);
		return "redirect:/imageupload";
	}

	@GetMapping("/images/{fileName}")
	public void getImage(@PathVariable String fileName, HttpServletResponse response) throws IOException {
		File requestedFile = imageStorageService.getFile(fileName, null);

		if(requestedFile == null) {
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
