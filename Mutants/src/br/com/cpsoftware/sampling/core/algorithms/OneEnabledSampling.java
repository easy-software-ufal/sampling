package br.com.cpsoftware.sampling.core.algorithms;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.cpsoftware.sampling.core.SamplingAlgorithm;

public class OneEnabledSampling extends SamplingAlgorithm{

	@Override
	public List<List<String>> getSamples(File file) throws Exception {
		List<List<String>> configurations = new ArrayList<>();
		directives = SamplingAlgorithm.getDirectives(file);
		
		// It activates each macro separately..
		for (int i = 0; i < (directives.size()); i++){
			List<String> configuration = new ArrayList<>();
			configuration.add(directives.get(i));
			configurations.add(configuration);
		}
		
		if (configurations.size() == 0){
			configurations.add(new ArrayList<String>());
		}
		
		// It gets each configuration and adds an #UNDEF for the macros that are not active..
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
