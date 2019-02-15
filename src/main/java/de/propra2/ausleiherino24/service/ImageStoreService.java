package de.propra2.ausleiherino24.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageStoreService {

	private String uploadDirectoryPath;
	private static final int NR_OF_BINS = 100;

	@Autowired
	public ImageStoreService(@Value("${uploadDirectoryPath}") String uploadDirectoryPath) {
		this.uploadDirectoryPath = uploadDirectoryPath;
		createUploadDirectoryIfNotExists();
	}

	public String store(MultipartFile file, Long binningId) {
		if(file == null) return null;

		String prefix = ensureBinning(binningId);

		String extension = getFileExtension(file.getOriginalFilename());
		File dest = new File(generateFilePath(prefix, extension));

		try {
			file.transferTo(dest);
		} catch(Exception e) {
			e.printStackTrace();
		}

		return file.getName();
	}

	public File getFile(String fileName, Long binningId) {
		String binName = binningId == null ? "" : resolveBin(binningId).toString();

		File file = new File(buildPath(fileName, binName));

		return file.exists() ? file : null;
	}

	private String generateFilePath(String prefix, String fileEnding) {
		String uniqueFilepath = buildPath(buildFilename(fileEnding), prefix);

		while ( fileExists(uniqueFilepath) ) {
			uniqueFilepath = UUID.randomUUID().toString();
		}

		return uniqueFilepath;
	}

	private boolean fileExists(String path) {
		File f = new File(path);
		return f.exists() && !f.isDirectory();
	}

	private String buildPath(String fileName, String prefix) {
		return Paths.get(getUploadDirectoryPath(), prefix, fileName).toString();
	}

	private String getUploadDirectoryPath() {
		String rootPath = Paths.get(".").toAbsolutePath().normalize().toString();
		return Paths.get(rootPath, this.uploadDirectoryPath).toString();
	}

	private String buildFilename(String fileEnding) {
		StringBuilder builder = new StringBuilder();
		builder.append(UUID.randomUUID());
		builder.append(".");
		builder.append(fileEnding);
		return  builder.toString();
	}


	private String ensureBinning(Long binningId) {
		if(binningId == null) return "";

		String binningDirName = resolveBin(binningId).toString();
		createBinningDirectory(binningDirName);

		return binningDirName;
	}

	private Long resolveBin(Long binningId) {
		return binningId % NR_OF_BINS;
	}

	private void createBinningDirectory(String name) {
		String binPath = Paths.get(getUploadDirectoryPath(), name).toString();

		File binningDir = new File( binPath );

		if(!binningDir.exists()) {
			binningDir.mkdir();
		}
	}

	private void createUploadDirectoryIfNotExists() {
		File uploadDir = new File( getUploadDirectoryPath() );

		if(!uploadDir.exists()) {
			uploadDir.mkdir();
		}
	}

	private String getFileExtension(String fileName) {
		if(fileName == null) return "";

		int i = fileName.lastIndexOf('.');

		return i > 0 ? fileName.substring(i+1) : "";
	}
}
