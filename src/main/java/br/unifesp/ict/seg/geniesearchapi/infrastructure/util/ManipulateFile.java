package br.unifesp.ict.seg.geniesearchapi.infrastructure.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ManipulateFile {

	/**
	 * 
	 * Exemplo para extrair recursivamente os arquivos de um zip.
	 * 
	 * @param zipFile   Nome do arquivo a ser extraído os arquivos
	 * @param outputDir Diretório para onde deve ser copiado os arquivos extraídos
	 */
	public static void extract(String zipFile, String outputDir) {

		byte[] buffer = new byte[1024];

		try {

			// create output directory is not exists
			File folder = new File(outputDir);
			if (!folder.exists()) {
				folder.mkdir();
			}

			// get the zip file content
			ZipInputStream zipStream = new ZipInputStream(new FileInputStream(zipFile));

			// get the zipped file list entry
			ZipEntry entry = zipStream.getNextEntry();

			while (entry != null) {

				String fileName = entry.getName();
				File newFile = new File(outputDir + File.separator + fileName);

				// create all non exists folders
				// else you will hit FileNotFoundException for compressed folder
				new File(newFile.getParent()).mkdirs();

				FileOutputStream fos = new FileOutputStream(newFile);

				int len;
				while ((len = zipStream.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();
				entry = zipStream.getNextEntry();
			}

			zipStream.closeEntry();
			zipStream.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
