package br.com.dmf.textconvert.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class CopyDirectoryUtils {

	static public String ExportResource(String resourceName) throws Exception {
		InputStream stream = null;
		OutputStream resStreamOut = null;
		String jarFolder;
		try {
			stream = CopyDirectoryUtils.class.getResourceAsStream(resourceName);// note that each / is a directory down
																				// in the "jar tree" been the jar the
																				// root of the tree
			if (stream == null) {
				throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
			}

			int readBytes;
			byte[] buffer = new byte[4096];
			jarFolder = new File(
					CopyDirectoryUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())
							.getParentFile().getPath().replace('\\', '/');
			resStreamOut = new FileOutputStream(jarFolder + resourceName);
			while ((readBytes = stream.read(buffer)) > 0) {
				resStreamOut.write(buffer, 0, readBytes);
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			stream.close();
			resStreamOut.close();
		}

		return jarFolder + resourceName;
	}

	public static boolean copy(InputStream source, String destination) {
		boolean succeess = true;
		System.out.println("Copying ->" + source + "\n\tto ->" + destination);

		try {
			Files.copy(source, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
			ex.printStackTrace();
			succeess = false;
		}

		return succeess;

	}

}
