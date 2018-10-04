package br.com.cpsoftware.sampling.core.algorithms;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import br.com.cpsoftware.sampling.core.SamplingAlgorithm;

public class StmtCoverageSampling extends SamplingAlgorithm {

	@Override
	public List<List<String>> getSamples(File file) throws Exception {
		List<List<String>> configurations = new ArrayList<>();
		directives = this.getDirectives(file);
		
		new ProcessBuilder("undertaker", "-j", "coverage", file.getAbsolutePath()).start();
		
		int count = 1;
		
		File config = new File(file.getAbsolutePath() + ".config" + count);
		while(config.exists()){
			try {
				FileInputStream fstream = new FileInputStream(config);
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;
				List<String> configuration = new ArrayList<>();
				while ((strLine = br.readLine()) != null) {
					strLine = strLine.replace("# ", "");
					if (strLine.contains("=y")){
						strLine = strLine.replace("=y", "");
						configuration.add(strLine);
					}
				}
				configurations.add(configuration);
				in.close();
				
				count++;
				config = new File(file.getAbsolutePath() + ".config" + count);
			} catch (FileNotFoundException e){
				System.err.println(file.getName());
			}
		}

		// It gets each configurations and add #UNDEF for the macros that are not active..
		for (List<String> configuration : configurations){
			for (String directive : directives){
				if (!configuration.contains(directive) && !configuration.contains("!" + directive)){
					configuration.add("!" + directive);
				}
			}
		}
		
		return configurations;
	}

}
