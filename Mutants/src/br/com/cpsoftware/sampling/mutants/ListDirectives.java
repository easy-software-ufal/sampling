package br.com.cpsoftware.sampling.mutants;

import java.io.File;
import java.util.List;

import br.com.cpsoftware.sampling.core.SamplingAlgorithm;

public class ListDirectives {

	public static void main(String[] args) throws Exception {
		File folder = new File("bugs");
		new ListDirectives().listFilesForFolder(folder);
	}
	
	public void listFilesForFolder(final File folder) throws Exception {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	        	if (!fileEntry.getName().startsWith(".") && fileEntry.getName().endsWith(".c")) {
	        		List<String> directives = SamplingAlgorithm.getDirectives(fileEntry);
		        	System.out.println(fileEntry.getName() + " (" + directives.size() + ")");
		        	for (int i = 0; i < directives.size(); i++) {
		        		System.out.print(directives.get(i) + ", ");
		        	}
		        	System.out.println();
	        	}
	        }
	    }
	}
	
}
