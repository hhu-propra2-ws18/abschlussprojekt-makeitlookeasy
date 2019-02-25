package de.propra2.ausleiherino24.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

    private static final int NR_OF_BINS = 100;
    private String uploadDirectoryPath;

    @Autowired
    public ImageService(final @Value("${uploadDirectoryPath}") String uploadDirectoryPath) {
        this.uploadDirectoryPath = uploadDirectoryPath;
        createUploadDirectoryIfNotExists();
    }

    public String store(final MultipartFile file, final Long binningId) {
        if (file == null) {
            return null;
        }

        final String prefix = ensureBinning(binningId);

        final String extension = getFileExtension(file.getOriginalFilename());
        final File dest = new File(generateFilePath(prefix, extension));

        try {
            file.transferTo(dest);
        } catch (Exception e) {
            LOGGER.warn("Couldn't move file {} to desired destination '{}'.", file.getName(),
                    dest.getAbsolutePath());
        }

        return dest.getName();
    }

    public String storeFile(final File inputFile, final Long binningId) {
        if (inputFile == null) {
            return null;
        }

        final String prefix = ensureBinning(binningId);

        final String extension = getFileExtension(inputFile.getName());
        final File destinationFile = new File(generateFilePath(prefix, extension));

        try (FileOutputStream outputStream = new FileOutputStream(destinationFile)) {
            try (FileInputStream inputStream = new FileInputStream(inputFile)) {

                final byte[] buffer = new byte[1024];

                int length = inputStream.read(buffer);

                while (length > 0) {
                    outputStream.write(buffer, 0, length);
                    length = inputStream.read(buffer);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Couldn't store uploaded file in database.", e);
        }

        return destinationFile.getName();
    }

    public File getFile(final String fileName, final Long binningId) {
        final String binName = binningId == null ? "" : resolveBin(binningId).toString();

        final File file = new File(buildPath(fileName, binName));

        return file.exists() ? file : null;
    }

    String generateFilePath(final String prefix, final String fileEnding) {
        String uniqueFilepath = buildPath(buildFilename(fileEnding), prefix);

        while (fileExists(uniqueFilepath)) {
            uniqueFilepath = UUID.randomUUID().toString();
        }

        return uniqueFilepath;
    }

    boolean fileExists(final String path) {
        final File f = new File(path);
        return f.exists() && !f.isDirectory();
    }

    String buildPath(final String fileName, final String prefix) {
        return Paths.get(getUploadDirectoryPath(), prefix, fileName).toString();
    }

    String getUploadDirectoryPath() {
        final String rootPath = Paths.get(".").toAbsolutePath().normalize().toString();
        return Paths.get(rootPath, this.uploadDirectoryPath).toString();
    }

    private String buildFilename(final String fileEnding) {
        return UUID.randomUUID() + "." + fileEnding;
    }

    String ensureBinning(final Long binningId) {
        if (binningId == null) {
            return "";
        }

        final String binningDirName = resolveBin(binningId).toString();
        createBinningDirectory(binningDirName);

        return binningDirName;
    }

    private Long resolveBin(final Long binningId) {
        return binningId % NR_OF_BINS;
    }

    void createBinningDirectory(final String name) {
        final String binPath = Paths.get(getUploadDirectoryPath(), name).toString();

        final File binningDir = new File(binPath);

        if (!binningDir.exists()) {
            binningDir.mkdir();
        }
    }

    void createUploadDirectoryIfNotExists() {
        final File uploadDir = new File(getUploadDirectoryPath());

        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
    }

    String getFileExtension(final String fileName) {
        if (fileName == null) {
            return "";
        }

        final int i = fileName.lastIndexOf('.');

        return i > 0 ? fileName.substring(i + 1) : "";
    }
}
