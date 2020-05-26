package br.unifesp.ict.seg.geniesearchapi.services.reponotes.infrastructure.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tools.ant.util.StringUtils;

import br.unifesp.ict.seg.geniesearchapi.infrastructure.util.GenieSearchAPIConfig;

public class RepoNotes {

	private static Properties properties = null;

	private RepoNotes() {
	}

	private static void loadProperties() throws Exception {

		properties = new Properties();
		
		File excelFile = GenieSearchAPIConfig.getRepoNotesPath().toFile();
	    FileInputStream fis = new FileInputStream(excelFile);

	    XSSFWorkbook workbook = new XSSFWorkbook(fis);
	    XSSFSheet sheet = workbook.getSheet("Check");
	    
	    Iterator<Row> rowIt = sheet.iterator();

	    while(rowIt.hasNext()) {
	      Row row = rowIt.next();
	      
	      String key = row.getCell(0).getStringCellValue();
	      String value = "";
	      try {
	    	  value = row.getCell(1).getStringCellValue();
	      } catch (IllegalStateException e) {
	    	  value = StringUtils.removeSuffix(row.getCell(1).getNumericCellValue() + "",".0");
		}
	      
	      System.out.println(key + " - " + value);
	      
	      properties.setProperty(key, value);
	    }

	    workbook.close();
	    fis.close();
	}
	
	public static Properties getProperties() throws Exception {
		
		if(properties == null)
			loadProperties();
		
		return properties; 
	}

}
