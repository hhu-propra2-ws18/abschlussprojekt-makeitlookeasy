package de.propra2.ausleiherino24.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

    private static final int NR_OF_BINS = 100;
    private String uploadDirectoryPath;

    @Autowired
    public ImageService(@Value("${uploadDirectoryPath}") String uploadDirectoryPath) {
        this.uploadDirectoryPath = uploadDirectoryPath;
        createUploadDirectoryIfNotExists();
    }

    public String store(MultipartFile file, Long binningId) {
        if (file == null) {
            return null;
        }

        String prefix = ensureBinning(binningId);

        String extension = getFileExtension(file.getOriginalFilename());
        File dest = new File(generateFilePath(prefix, extension));

        try {
            file.transferTo(dest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dest.getName();
    }

    public String storeFile(File inputFile, Long binningId) {
        if (inputFile == null) {
            return null;
        }

        String prefix = ensureBinning(binningId);

        String extension = getFileExtension(inputFile.getName());
        File destinationFile = new File(generateFilePath(prefix, extension));

        try {
            FileInputStream inputStream = new FileInputStream(inputFile);
            FileOutputStream outputStream = new FileOutputStream(destinationFile);

            byte[] buffer = new byte[1024];

            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return destinationFile.getName();
    }

    /**
     * TODO JavaDoc
     *
     * @param fileName Description
     * @param binningId Description
     * @return Description
     */
    public File getFile(String fileName, Long binningId) {
        String binName = binningId == null ? "" : resolveBin(binningId).toString();

        File file = new File(buildPath(fileName, binName));

        return file.exists() ? file : null;
    }

    public String generateFilePath(String prefix, String fileEnding) {
        String uniqueFilepath = buildPath(buildFilename(fileEnding), prefix);

        while (fileExists(uniqueFilepath)) {
            uniqueFilepath = UUID.randomUUID().toString();
        }

        return uniqueFilepath;
    }

    /**
     * TODO JavaDoc
     * @param path Description
     * @return Description
     */
    public boolean fileExists(String path) {
        File f = new File(path);
        return f.exists() && !f.isDirectory();
    }

    public String buildPath(String fileName, String prefix) {
        return Paths.get(getUploadDirectoryPath(), prefix, fileName).toString();
    }

    public String getUploadDirectoryPath() {
        String rootPath = Paths.get(".").toAbsolutePath().normalize().toString();
        return Paths.get(rootPath, this.uploadDirectoryPath).toString();
    }

    private String buildFilename(String fileEnding) {
        StringBuilder builder = new StringBuilder();
        builder.append(UUID.randomUUID());
        builder.append(".");
        builder.append(fileEnding);
        return builder.toString();
    }

    /**
     * TODO Javadoc
     * @param binningId Description
     * @return Description
     */
    public String ensureBinning(Long binningId) {
        if (binningId == null) {
            return "";
        }

        String binningDirName = resolveBin(binningId).toString();
        createBinningDirectory(binningDirName);

        return binningDirName;
    }

    private Long resolveBin(Long binningId) {
        return binningId % NR_OF_BINS;
    }

    /**
     * TODO Javadoc
     * @param name Description
     */
    public void createBinningDirectory(String name) {
        String binPath = Paths.get(getUploadDirectoryPath(), name).toString();

        File binningDir = new File(binPath);

        if (!binningDir.exists()) {
            binningDir.mkdir();
        }
    }

    public void createUploadDirectoryIfNotExists() {
        File uploadDir = new File(getUploadDirectoryPath());

        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
    }

    /**
     * TODO Javadoc.
     * @param fileName Description
     * @return Description
     */
    public String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }

        int i = fileName.lastIndexOf('.');

        return i > 0 ? fileName.substring(i + 1) : "";
    }
}
