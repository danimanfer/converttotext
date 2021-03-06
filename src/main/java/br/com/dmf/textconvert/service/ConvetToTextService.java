package br.com.dmf.textconvert.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

import br.com.dmf.textconvert.utils.CopyDirectoryUtils;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@Service
public class ConvetToTextService {

	public String gerararquivo(String arquivo, String diretorio) {

		String dirUser = System.getProperty("user.dir");

		String tessdata = dirUser + "/tessdata";
		
		Path path = Paths.get(tessdata);
		
		if (!Files.isDirectory(path)) {
			
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String data1 = "/eng.traineddata";
			InputStream in=	getClass().getResourceAsStream("/tessdata"+data1);
			CopyDirectoryUtils.copy(in, tessdata+ data1);
			
			String data2 = "/osd.traineddata";
			InputStream in2 =	getClass().getResourceAsStream("/tessdata"+data2);
			CopyDirectoryUtils.copy(in2, tessdata+ data2);
			
			String data3 = "/por.traineddata";
			InputStream in3=	getClass().getResourceAsStream("/tessdata"+data3);
			CopyDirectoryUtils.copy(in3, tessdata+ data3);
		}
		
		File image = new File(arquivo);

		System.out.println(tessdata);
		ITesseract tesseract = new Tesseract();
		tesseract.setDatapath(tessdata);

		tesseract.setLanguage("por");
		tesseract.setPageSegMode(1);
//		tesseract.setOcrEngineMode(1);

		try {
			String result = tesseract.doOCR(image);
//			System.out.println(result);
//			try {
//				Files.write(Paths.get(diretorio + "\\result.txt"), result.getBytes());
//			} catch (IOException e) {
//				e.printStackTrace();
//			}

//			return Paths.get(diretorio + "\\result.txt").toString();
			
			return result;

		} catch (TesseractException e) {
			System.err.println(e.getMessage());
		}

		return null;
	}

}
