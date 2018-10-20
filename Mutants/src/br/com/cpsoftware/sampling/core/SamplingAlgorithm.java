package br.com.cpsoftware.sampling.core;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public abstract class SamplingAlgorithm {

	protected List<String> directives;
	
	public abstract List<List<String>> getSamples(File file) throws Exception;
	
	public static boolean isValidJavaIdentifier(String s) {
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
	public static List<String> getDirectives(File file) throws Exception{
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
					if (!directives.contains(directivesStr[i].trim()) && !directivesStr[i].trim().equals("") && isValidJavaIdentifier(directivesStr[i].trim())){
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
	
	public static <T> List<List<T>> powerSet(List<T> originalSet) {
		List<List<T>> sets = new ArrayList<List<T>>();
		if (originalSet.isEmpty()) {
			sets.add(new ArrayList<T>());
			return sets;
		}
		List<T> list = new ArrayList<T>(originalSet);
		T head = list.get(0);
		List<T> rest = new ArrayList<T>(list.subList(1, list.size()));
		for (List<T> set : powerSet(rest)) {
			List<T> newSet = new ArrayList<T>();
			newSet.add(head);
			newSet.addAll(set);
			sets.add(newSet);
			sets.add(set);
		}
		return sets;
	}
}
