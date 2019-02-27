package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.service.ImageService;
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

    private static final String URL = "/imageupload";
    private ImageService imageService;

    @Autowired
    public ImageController(final ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping(URL)
    public String fileUpload() {
        return URL;
    }

    @PostMapping(URL)
    public String handleFileUpload(final @RequestParam("file") MultipartFile file) {
        imageService.store(file, null);
        return "redirect:" + URL;
    }

    /**
     * Provides a method to receive images stored by the ImageService. Those are responded using
     * HttpServletResponse.
     */
    @GetMapping("/images/{fileName}")
    public void getImage(final @PathVariable String fileName, final HttpServletResponse response)
            throws IOException {
        final File requestedFile = imageService.getFile(fileName, null);

        if (requestedFile == null) {
            response.setStatus(404);
            return;
        }

        response.setContentType(Files.probeContentType(requestedFile.toPath()));

        try (DataInputStream dataInputStream = new DataInputStream(
                new FileInputStream(requestedFile))) {
            IOUtils.copy(dataInputStream, response.getOutputStream());
        }

    }
}
