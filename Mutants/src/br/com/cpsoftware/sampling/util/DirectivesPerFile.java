package br.com.cpsoftware.sampling.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DirectivesPerFile {

	public static List<Integer> directivesPerFile = new ArrayList<>();
	public static int files = 47;
	
	public static void main(String[] args) throws Exception {
		/*new DirectivesPerFile().listFiles(new File("bugs"));
		for (Integer i : DirectivesPerFile.directivesPerFile){
			System.out.println(i);
		}*/
		DirectivesPerFile.listAllFiles(new File("code"));
		System.out.println(DirectivesPerFile.files);
	}
	
	public static void listAllFiles(File path) throws Exception{
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
	
	public void listFiles(File path) throws Exception{
		if (path.isDirectory()){
			for (File file : path.listFiles()){
				this.listFiles(file);
			}
		} else {
			if (path.getName().endsWith(".c")){
				directivesPerFile.add(this.getDirectives(path).size());
			}
		}
	}
	
	public boolean isValidJavaIdentifier(String s) {
		// An empty or null string cannot be a valid identifier.
	    if (s == null || s.length() == 0){
	    	return false;
	   	}

	    char[] c = s.toCharArray();
	    if (!Character.isJavaIdentifierStart(c[0])){
	    	return false;
	    }

	    for (int i = 1; i < c.length; i++){
	        if (!Character.isJavaIdentifierPart(c[i])){
	           return false;
	        }
	    }

	    return true;
	}
	
	// It sets the number of configurations..
	public List<String> getDirectives(File file) throws Exception{
		List<String> directives = new ArrayList<>();
		
		FileInputStream fstream = new FileInputStream(file);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;

		while ((strLine = br.readLine()) != null)   {
			
			strLine = strLine.trim();
			
			if (strLine.startsWith("#")){
				strLine = strLine.replaceAll("#(\\s)+", "#");
				strLine = strLine.replaceAll("#(\\t)+", "#");
			}
			
			if (strLine.trim().startsWith("#if") || strLine.trim().startsWith("#elif")){
				
				strLine = strLine.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)","");
				
				String directive = strLine.replace("#ifdef", "").replace("#ifndef", "").replace("#if", "");
				directive = directive.replace("defined", "").replace("(", "").replace(")", "");
				directive = directive.replace("||", "").replace("&&", "").replace("!", "").replace("<", "").replace(">", "").replace("=", "");
				
				String[] directivesStr = directive.split(" ");
				
				for (int i = 0; i < directivesStr.length; i++){
					if (!directives.contains(directivesStr[i].trim()) && !directivesStr[i].trim().equals("") && this.isValidJavaIdentifier(directivesStr[i].trim())){
						directives.add(directivesStr[i].trim());
					}
				}
			}
			
			if (strLine.startsWith("#")){
				strLine = strLine.replaceAll("#(\\s)+", "#");
				strLine = strLine.replaceAll("#(\\t)+", "#");
				if (strLine.startsWith("#define")){
					String[] parts = strLine.split(" ");
					if (parts.length == 3){
						parts[1] = parts[1].trim();
						directives.remove(parts[1]);
					}
				}
			}
			
		}
		
		String fileName = file.getName().toUpperCase() + "_H";
		directives.remove(fileName);
		
		in.close();
		return directives;
	}
	
}
