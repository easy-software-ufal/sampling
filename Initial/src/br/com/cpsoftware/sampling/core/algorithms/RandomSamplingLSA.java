package br.com.cpsoftware.sampling.core.algorithms;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.com.cpsoftware.sampling.core.SamplingAlgorithm;

public class RandomSamplingLSA extends SamplingAlgorithm {

	public static int NUMBER_CONFIGS = 0;

	public static int popSize[] = {
		    6, 14, 14, 8, 62, 8, 14, 54, 8, 36, 36, 94, 286, 
		    74, 74, 32, 6, 18, 6, 22, 38, 8, 100, 100, 6, 16, 
		    8, 6, 16, 42, 12, 8, 4, 30, 20, 4, 8, 6, 6, 10, 34, 
		    10, 10, 4, 4, 12, 6, 6, 16, 28, 72, 12, 20, 24, 6, 86, 
		    14, 8, 4, 4, 4, 4, 78, 94, 98, 40, 6, 22, 10, 10, 8, 12, 
		    10, 10, 10, 10, 10, 8, 6, 6, 10, 18, 14, 32, 48, 6, 6, 8, 
		    12, 30, 32, 12, 6, 14, 18, 6, 14, 10, 8, 10, 6, 24, 6, 8, 
		    10, 12, 6, 8, 38, 38, 26, 6, 8, 12, 40, 6, 12, 8, 10, 42, 
		    10, 128, 38, 26, 42, 244, 10, 372, 372};
	
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
