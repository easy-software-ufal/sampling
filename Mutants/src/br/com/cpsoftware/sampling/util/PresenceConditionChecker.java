package br.com.cpsoftware.sampling.util;

import java.io.File;
import java.io.IOException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class PresenceConditionChecker {

	public static void main(String[] args) throws BiffException, IOException {
		File inputWorkbook = new File("bugs.xls");
		Workbook w = Workbook.getWorkbook(inputWorkbook);
		Sheet sheet = w.getSheet(0);
		
		String /*project, path,*/ presenceCondition = null;
		
		for (int i = 0; i < sheet.getRows(); i++) {
			
			//project = sheet.getCell(0, i).getContents();
			//path = sheet.getCell(3, i).getContents();
			presenceCondition = sheet.getCell(4, i).getContents();
			
			presenceCondition = presenceCondition.replaceAll("\\s", "");
			String[] options = presenceCondition.split("\\)\\|\\|\\(");
			
			String pc = null;
			for (String option : options){
				if (pc == null){
					pc = option;
				} else {
					
					int countPC = pc.split("&&").length;
					int countOpt = option.split("&&").length;
					
					if (countOpt < countPC){
						pc = option;
					}
					
				}
			}
			
			String[] macros = pc.split("&&");
			int enabled = 0;
			int disabled = 0;
			
			for (String macro : macros){
				macro = macro.replace("(", "").replace(")", "").trim();
				if (macro.startsWith("!")){
					disabled++;
				} else {
					enabled++;
				}
			}
			
			System.out.println(enabled + " | " + disabled);
			
		}
	}
	
}
