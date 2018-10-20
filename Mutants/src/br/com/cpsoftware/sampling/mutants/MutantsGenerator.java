package br.com.cpsoftware.sampling.mutants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.com.cpsoftware.sampling.core.SamplingAlgorithm;

public class MutantsGenerator {

	public static String project;
	public static String file;
	public static int count;
	
	public static void main(String[] args) throws Exception {
		File folder = new File("bugs");
		count = 0;
		new MutantsGenerator().listFilesForFolder(folder);
	}
	
	public void listFilesForFolder(final File folder) throws Exception {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	        	if (!fileEntry.getName().startsWith(".") && fileEntry.getName().endsWith(".c")) {
	        		List<String> directives = SamplingAlgorithm.getDirectives(fileEntry);
	        		
	        		int max = 0;
	        		
	        		while (directives.size() > 0) {
	        		
		        		Random r = new Random();
		        		//int macros = directives.size();
		        		
		        		int low = 0;
		        		int high = directives.size();
		        		int result = r.nextInt(high-low) + low;

		        		count++;
		        		
		        		String fileName = fileEntry.getAbsolutePath().replace("/Users/flavio/Desktop/GitHub/Sampling/Mutants/bugs/", "");
		        		String projectName = fileName.split("/")[0];
		        		fileName = fileName.replaceFirst(projectName + "/", "");
		        		
		        		project = projectName;
		        		file = fileEntry.getName();
		        		
		        		List<String> directivesTemp = new ArrayList<>();
		        		for(int i = 0; i < result; i++) {
		        			directivesTemp.add(directives.get(i));
		        		}
		        		
		        		for(int i = result - 1; i > 0 ; i--) {
		        			directives.remove(directivesTemp.get(i));
		        		}
		        		
		        		System.out.print(projectName + "-" + file + "-" + count + "," + projectName + "," + fileName + ",");
		        		
		        		for(int i = 0; i < directivesTemp.size(); i++) {
		        			if (r.nextBoolean()) {
		        				if (i == directivesTemp.size()-1) {
		        					System.out.print(directivesTemp.get(i) + " ");
		        				} else {
		        					System.out.print(directivesTemp.get(i) + " && ");
		        				}
		        			} else {
		        				if (i == directivesTemp.size()-1) {
		        					System.out.print("!" + directivesTemp.get(i) + " ");
		        				} else {
		        					System.out.print("!" + directivesTemp.get(i) + " && ");
		        				}
		        			}
		        		}
		        		System.out.println();
		       
		        		count++;
		        		max++;
		        		
		        		if (max == 5) {
		        			break;
		        		}
		        		
	        		
	        		}
	        	}
	        }
	    }
	}
	
}
