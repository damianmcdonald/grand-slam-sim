package com.github.damianmcdonald.grandslamsim.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.github.damianmcdonald.grandslamsim.domain.Player;

@Service
public class ExcelReaderService {

	public List<Player> readPlayersFromExcel(File file) throws InvalidFormatException, IOException {
		final List<Player> players = new ArrayList<Player>();
		XSSFWorkbook wb = null;
		try {
			wb = new XSSFWorkbook(new FileInputStream(file));
		    final XSSFSheet sheet = wb.getSheetAt(0);
		    XSSFRow row;
		    final int rows = sheet.getPhysicalNumberOfRows(); // No of rows
		    int cols = 0; // No of columns
		    int tmp = 0;
		    // This trick ensures that we get the data properly even if it doesn't start from first few rows
		    for(int i = 0; i < 10 || i < rows; i++) {
		        row = sheet.getRow(i);
		        if(row != null) {
		            tmp = sheet.getRow(i).getPhysicalNumberOfCells();
		            if(tmp > cols) cols = tmp;
		        }
		    }
		    for(int r = 0; r < rows; r++) {
		        row = sheet.getRow(r);
		        if(row != null) {
		        	final String firstName = row.getCell(COLUMN_TYPE.FIRSTNAME.ordinal()).getStringCellValue();
		        	final String surname = row.getCell(COLUMN_TYPE.SURNAME.ordinal()).getStringCellValue();
		        	final String represents = row.getCell(COLUMN_TYPE.REPRESENTS.ordinal()).getStringCellValue();
		        	final String image = row.getCell(COLUMN_TYPE.IMAGE.ordinal()).getStringCellValue();
		        	players.add(new Player(firstName, surname, represents, image));
		        }
		    } 
		    return players;
		} finally {
			wb.close();
		}     	
	}
	
	public enum COLUMN_TYPE {
	    FIRSTNAME,
	    SURNAME,
	    REPRESENTS,
	    IMAGE;
	}

}
