package br.com.cpsoftware.sampling.util;

import java.io.File;

public class NumberOfFiles {

	public static int files = 0;
	
	public static void main(String[] args) {
		NumberOfFiles.listAllFiles(new File("code"));
		System.out.println(NumberOfFiles.files + 47);
	}
	
	public static void listAllFiles(File path) {
		if (path.isDirectory()){
			for (File file : path.listFiles()){
				listAllFiles(file);
			}
		} else {
			if (path.getName().endsWith(".c")){
				files++;
			}
		}
	}
	
	
}
