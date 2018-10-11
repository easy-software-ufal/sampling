package br.com.cpsoftware.sampling.core.algorithms;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.cpsoftware.sampling.core.SamplingAlgorithm;

public class TwiseSampling extends SamplingAlgorithm {

	private int t;
	
	public TwiseSampling(int t) {
		this.t = t;
	}
	
	@Override
	public List<List<String>> getSamples(File file) throws Exception {
		List<List<String>> configurations = new ArrayList<>();
		directives = SamplingAlgorithm.getDirectives(file);
		Collections.sort(directives);
		
		FileInputStream fstream = new FileInputStream("tables/" + t + "x2/ca." + t + ".2^" + directives.size() + ".txt");
		
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		String strLine;
		// Ignore first line..
		strLine = br.readLine();
		
		// Read File Line By Line
		while ((strLine = br.readLine()) != null) {
			String[] configsTable = strLine.trim().split(" ");
			List<String> configuration = new ArrayList<>();
			
			for (int i = 0; i < configsTable.length; i++){
				if (configsTable[i].equals("1")){
					configuration.add(directives.get(i));
				}
			}
			
			configurations.add(configuration);
		}
		// Close the input stream
		in.close();

		if (directives.size() == 0){
			configurations.add(new ArrayList<String>());
		}
		
		// It gets each configuration and add #UNDEF for the macros that are not active..
		for (List<String> configuration : configurations) {
			for (String directive : directives) {
				if (!configuration.contains(directive)) {
					configuration.add("!" + directive);
				}
			}
		}

		return configurations;
	}

}
