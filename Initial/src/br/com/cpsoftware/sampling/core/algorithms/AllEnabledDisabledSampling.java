package br.com.cpsoftware.sampling.core.algorithms;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.cpsoftware.sampling.core.SamplingAlgorithm;

public class AllEnabledDisabledSampling extends SamplingAlgorithm{

	@Override
	public List<List<String>> getSamples(File file) throws Exception {
		List<List<String>> configurations = new ArrayList<>();
		directives = this.getDirectives(file);
		
		if (directives.size() > 0){
		
			// All macros activated..
			List<String> allActive = new ArrayList<String>();
			for (String d : directives){
				allActive.add(d);
			}
			configurations.add(allActive);
		
			
			// All macros activated..
			List<String> allDeactive = new ArrayList<String>();
			for (String d : directives){
				allDeactive.add("!" + d);
			}
			configurations.add(allDeactive);
		
		} else {
			configurations.add(new ArrayList<String>());
		}
		
		return configurations;
	}
	
}
