package br.com.cpsoftware.sampling.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.com.cpsoftware.sampling.checker.BugsChecker;
import br.com.cpsoftware.sampling.core.GeneticSamplingAlgorithm;
import br.com.cpsoftware.sampling.core.SamplingAlgorithm;
import br.com.cpsoftware.sampling.core.algorithms.RandomSampling;
import br.com.cpsoftware.sampling.core.algorithms.TwiseSampling;

public class GA {

	public static int count = 0;
	public static int generation = 0;
	public static double score = 0;
	public static String lastFile = "nothing!";
	
	public static void main(String[] args) throws Exception {
		File folder = new File("bugs");
		GA ga = new GA();
		
		ga.listFilesForFolder(folder);
		System.out.println("------------");
		ga.listFilesForFolder(folder);
		System.out.println("------------");
		ga.listFilesForFolder(folder);
		
		/*SamplingAlgorithm oneDisabled = new OneDisabledSampling();
		List<SamplingAlgorithm> algorithms = new ArrayList<>();
		algorithms.add(oneDisabled);
		ga.runGA(algorithms, 
				new File("bugs/fvwm/fvwm/ewmh_icons.c"), false, 0.1, 0.6);*/
	}
	
	public void listFilesForFolder(final File folder) throws Exception {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	        	if (!fileEntry.getName().startsWith(".") && fileEntry.getName().endsWith(".c")) {
	        		
	        		//SamplingAlgorithm oneDisabled = new OneDisabledSampling();
	        		//SamplingAlgorithm oneEnabled = new OneEnabledSampling();
	        		//SamplingAlgorithm allEnabledDisabled = new AllEnabledDisabledSampling();
	        		
	        		SamplingAlgorithm pairwise = new TwiseSampling(2);
	        		
	        		List<SamplingAlgorithm> algorithms = new ArrayList<>();
	        		//algorithms.add(oneDisabled);
	        		//algorithms.add(oneEnabled);
	        		//algorithms.add(allEnabledDisabled);
	        		
	        		algorithms.add(pairwise);
	        		
	        		double start = System.currentTimeMillis();
	        		// ELITISM / MUTATION // TRUE FOR RANDOM
	        		new GA().runGA(algorithms, fileEntry, false, 0.2, 0.3);
	        		double end = System.currentTimeMillis();
	        		System.out.print((int)(end-start) + "\n");
	        	}
	        }
	    }
	}

	public void runGA(List<SamplingAlgorithm> algorithms, File file, boolean random, double elitismPercentage, double mutationPercentage) throws Exception {
		
		if (random) {
			
			// LSA SIZE
			/*int popSize[] = {
				    6, 14, 14, 8, 62, 8, 14, 54, 8, 36, 36, 94, 286, 
				    74, 74, 32, 6, 18, 6, 22, 38, 8, 100, 100, 6, 16, 
				    8, 6, 16, 42, 12, 8, 4, 30, 20, 4, 8, 6, 6, 10, 34, 
				    10, 10, 4, 4, 12, 6, 6, 16, 28, 72, 12, 20, 24, 6, 86, 
				    14, 8, 4, 4, 4, 4, 78, 94, 98, 40, 6, 22, 10, 10, 8, 12, 
				    10, 10, 10, 10, 10, 8, 6, 6, 10, 18, 14, 32, 48, 6, 6, 8, 
				    12, 30, 32, 12, 6, 14, 18, 6, 14, 10, 8, 10, 6, 24, 6, 8, 
				    10, 12, 6, 8, 38, 38, 26, 6, 8, 12, 40, 6, 12, 8, 10, 42, 
				    10, 128, 38, 26, 42, 244, 10, 372, 372};*/
			
			// Pairwise
			int popSize[] = {4, 6, 6, 4, 11, 4, 6, 10, 4, 10, 10, 11, 14, 
				    11, 11, 8, 4, 6, 4, 8, 10, 4, 11, 11, 4, 6, 4, 4, 6, 10, 
				    6, 4, 2, 8, 6, 2, 4, 4, 4, 6, 8, 6, 6, 2, 2, 6, 4, 4, 6, 
				    8, 11, 6, 6, 8, 4, 11, 6, 4, 2, 2, 2, 2, 11, 11, 11, 10, 
				    4, 8, 6, 6, 4, 6, 6, 6, 6, 6, 6, 4, 4, 4, 6, 6, 6, 8, 10, 
				    4, 4, 4, 6, 8, 8, 6, 4, 6, 6, 4, 6, 6, 4, 6, 4, 8, 4, 4, 
				    6, 6, 4, 4, 10, 10, 8, 4, 4, 6, 10, 4, 6, 4, 6, 10, 6, 12, 
				    10, 8, 10, 14, 6, 15, 15
				};
			
			RandomSampling randomSampling = new RandomSampling();
			
			int size = popSize[GA.count];
			count++;
			
			//int size = 0;
			/*for(int i = 0; i < algorithms.size(); i++) {
				size += algorithms.get(i).getSamples(file).size();
			}*/
			
			RandomSampling.NUMBER_CONFIGS = size;
			algorithms.add(randomSampling);
		} 
		
		

		GeneticSamplingAlgorithm ga = new GeneticSamplingAlgorithm(algorithms);
		List<List<String>> initialPopulation = null;
		
		List<String> directives = SamplingAlgorithm.getDirectives(file);
		if (directives.size() < 2) {
			initialPopulation = SamplingAlgorithm.powerSet(directives);
		} else {
			initialPopulation = ga.getInitialPopulation(file);
		}

		List<List<String>> currentPopulation = initialPopulation;

		boolean bugDetected = false;

		for (int i = 0; i < initialPopulation.size(); i++) {
			double score = ga.getScoreFunctionValue(file, initialPopulation.get(i));
			if (score == new BugsChecker().getNumberOfBug(file)) {
				bugDetected = true;
			}
		}
		
		//System.out.println("Interaction: 0");
		//System.out.println("Population size: " + initialPopulation.size() + "\n");
		
		if (bugDetected) {
			//System.out.print("0,");
			GA.generation = 0;
			GA.score = new BugsChecker().getNumberOfBug(file);
			System.out.print(file.getName() + ", 0, " + ((int)new BugsChecker().getNumberOfBug(file)) + ", " + ((int)GA.score) + ", ");
		}
		
		int interaction = 1;

		while (!bugDetected) {
			
			if (lastFile != null && !lastFile.equals(file.getName())) {
				List<List<String>> newPopulation = new ArrayList<>();
				// VARIATION
				List<List<String>> eletism = ga.elitism(file, currentPopulation, elitismPercentage);
				newPopulation.addAll(eletism);

				List<List<String>> childsBuffer = new ArrayList<>();
				List<List<String>> childs = new ArrayList<>();

				while (currentPopulation.size() >= 2) {
					childsBuffer.addAll(ga.crossing(currentPopulation.get(0), currentPopulation.get(1)));
					childs.addAll(ga.crossing(currentPopulation.get(0), currentPopulation.get(1)));
					currentPopulation.remove(currentPopulation.get(0));
					currentPopulation.remove(currentPopulation.get(0));
				}
				
				if (currentPopulation.size() == 1) {
					childsBuffer.add(currentPopulation.get(0));
					childs.add(currentPopulation.get(0));
					mutationPercentage = 1;
				}

				// VARIATION
				for (int i = 0; i < (int) (childsBuffer.size() * mutationPercentage); i++) {

					Random r = new Random();
					int low = 0;
					int high = childs.size();
					int index = r.nextInt(high - low) + low;

					childs.add(ga.mutation(childs.get(index)));
					
					for (int j = 1; j <= 9; j++) {
						low = 0;
						high = childs.size() - 1;
						index = r.nextInt(high - low) + low;
		
						childs.add(ga.mutation(childs.get(index)));
					}

				}

				newPopulation.addAll(childs);

				// Select best child
				if (newPopulation.size() > 100) {
					newPopulation = ga.selectBest(file, newPopulation, 100);
				}

				// Showing the best results
				//System.out.println(file.getName());
				//System.out.println("Number of Bugs: " + new BugsChecker().getNumberOfBug(file));
				//System.out.println("Interaction: " + interaction);
				//System.out.println("Population size: " + newPopulation.size());
				
				boolean print = true;
				
				for (int i = 0; i < newPopulation.size(); i++) {
					double score = ga.getScoreFunctionValue(file, newPopulation.get(i));
					if (i == 0) {
						//System.out.println("Best: " + newPopulation.get(i) + ": " + score);
						//System.out.println("Bugs found: " + score);
					}
					
					if (score == new BugsChecker().getNumberOfBug(file) && print) {
						bugDetected = true;
						print = false;
						GA.generation = interaction;
						GA.score = score;
						System.out.print(file.getName() + ", " + interaction + ", " + ((int)new BugsChecker().getNumberOfBug(file)) + ", " + ((int)GA.score) + ", ");
						lastFile = file.getName();
					}
					
					// We found something better
					if (GA.score < score) {
						GA.generation = interaction;
						GA.score = score;
					} else {
						if (GA.generation + 5 <= interaction && print) {
							//System.out.println("Not converging..");
							System.out.print(file.getName() + ", " + interaction + ", " + ((int)new BugsChecker().getNumberOfBug(file)) + ", " + ((int)GA.score) + ", ");
							bugDetected = true;
							lastFile = file.getName();
							print = false;
						}
					}
					
				}
				interaction++;
				currentPopulation = newPopulation;
				//System.out.println();
			}
			
		}
		
	}

}
