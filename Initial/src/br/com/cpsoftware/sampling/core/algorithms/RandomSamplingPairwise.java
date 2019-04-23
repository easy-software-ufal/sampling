package br.com.cpsoftware.sampling.core.algorithms;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.com.cpsoftware.sampling.core.SamplingAlgorithm;

public class RandomSamplingPairwise extends SamplingAlgorithm {

	public static int NUMBER_CONFIGS = 0;

	public static int popSize[] = {4, 6, 6, 4, 11, 4, 6, 10, 4, 10, 10, 11, 14, 
		    11, 11, 8, 4, 6, 4, 8, 10, 4, 11, 11, 4, 6, 4, 4, 6, 10, 
		    6, 4, 2, 8, 6, 2, 4, 4, 4, 6, 8, 6, 6, 2, 2, 6, 4, 4, 6, 
		    8, 11, 6, 6, 8, 4, 11, 6, 4, 2, 2, 2, 2, 11, 11, 11, 10, 
		    4, 8, 6, 6, 4, 6, 6, 6, 6, 6, 6, 4, 4, 4, 6, 6, 6, 8, 10, 
		    4, 4, 4, 6, 8, 8, 6, 4, 6, 6, 4, 6, 6, 4, 6, 4, 8, 4, 4, 
		    6, 6, 4, 4, 10, 10, 8, 4, 4, 6, 10, 4, 6, 4, 6, 10, 6, 12, 
		    10, 8, 10, 14, 6, 15, 15
		};
	
	@Override
	public List<List<String>> getSamples(File file) throws Exception {
		List<List<String>> configurations = new ArrayList<>();
		directives = SamplingAlgorithm.getDirectives(file);

		if (directives.size() > 0
				&& NUMBER_CONFIGS < Math.pow(directives.size(), 2)) {

			for (int j = 0; j < RandomSampling.NUMBER_CONFIGS; j++) {
				// It sets or not-sets each configuration..
				List<String> configuration = new ArrayList<>();
				for (int i = 0; i < (directives.size()); i++) {
					if (this.getRandomBoolean()) {
						configuration.add(directives.get(i));
					} else {
						configuration.add("!" + directives.get(i));
					}
				}
				if (!configurations.contains(configuration)) {
					configurations.add(configuration);
				}
			}

		} else {
			if (NUMBER_CONFIGS >= Math.pow(directives.size(), 2)) {
				configurations = SamplingAlgorithm.powerSet(directives);
			}
		}

		if (configurations.size() == 0) {
			configurations.add(new ArrayList<String>());
		}

		// It gets each configuration and adds an #UNDEF for the macros that are
		// not active..
		for (List<String> configuration : configurations) {
			for (String directive : directives) {
				if (!configuration.contains(directive)
						&& !configuration.contains("!" + directive)) {
					configuration.add("!" + directive);
				}
			}
		}

		return configurations;
	}

	public boolean getRandomBoolean() {
		Random random = new Random();
		return random.nextBoolean();
	}

}
