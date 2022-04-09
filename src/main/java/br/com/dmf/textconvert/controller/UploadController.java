package br.com.dmf.textconvert.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.dmf.textconvert.service.ConvetToTextService;

@Controller
public class UploadController {

	@Autowired
	private ConvetToTextService convertTextService;

	@GetMapping("/")
	public String homepage() {
		return "index";
	}

	@PostMapping("/upload")
	public ResponseEntity<Resource> uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes)
			throws IOException {

		// check if file is empty
		if (file.isEmpty()) {
			attributes.addFlashAttribute("message", "Please select a file to upload.");
//			return "redirect:/";

			return ResponseEntity.noContent().build();
		}

		// normalize the file path
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		String dirUser = System.getProperty("user.dir");

		String prefix = "TempDirectory";
		Path dir = (Path) Paths.get(dirUser);

		String fileResponse = "";
		// save the file on the local file system

		Path tempDir = null;
		try {

			tempDir = Files.createTempDirectory(dir, prefix);

			Path path = Paths.get(tempDir + "/" + fileName);
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			fileResponse = convertTextService.gerararquivo(path.toString(), tempDir.toString());

		} catch (Exception e) {
			e.printStackTrace();
			Files.walk(tempDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			throw e;
		}

		// return success response
		attributes.addFlashAttribute("message", "Successfully Process " + fileName + '!');

		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());

		String nomeArquivo = fileName.substring(0, fileName.indexOf("."));
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=texto_" + nomeArquivo + "_" + currentDateTime + ".txt";

		
		InputStream targetStream = null;
		InputStreamResource resource = null;
		try {
			targetStream = new ByteArrayInputStream(fileResponse.getBytes());
			resource = new InputStreamResource(targetStream);

			Files.walk(tempDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);

			return ResponseEntity.ok().header(headerKey, headerValue).contentLength(fileResponse.length())
					.contentType(MediaType.TEXT_PLAIN).body(resource);

	
		} catch (Exception e) {
			e.printStackTrace();
			Files.walk(tempDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			throw e;

		} finally {
			if(targetStream!= null)
			targetStream.close();
		}

//		return ResponseEntity.badRequest().build();
	}

}
