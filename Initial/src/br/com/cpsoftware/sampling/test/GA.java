package br.com.cpsoftware.sampling.test;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.com.cpsoftware.sampling.core.GeneticSamplingAlgorithm;
import br.com.cpsoftware.sampling.core.SamplingAlgorithm;
import br.com.cpsoftware.sampling.core.algorithms.AllEnabledDisabledSampling;
import br.com.cpsoftware.sampling.core.algorithms.OneDisabledSampling;
import br.com.cpsoftware.sampling.core.algorithms.OneEnabledSampling;
import br.com.cpsoftware.sampling.core.algorithms.RandomSampling;
import br.com.cpsoftware.sampling.core.algorithms.RandomSamplingLSA;
import br.com.cpsoftware.sampling.core.algorithms.RandomSamplingPairwise;
import br.com.cpsoftware.sampling.core.algorithms.TwiseSampling;

public class GA {

	public static int count = 0;
	
	public static void main(String[] args) throws Exception {
			
		for (int i = 1; i <= 10; i++) {
			GA.count = 0;
			PrintStream fileOut = new PrintStream("exec-" + i + ".csv");
			System.setOut(fileOut);
			
			System.out.println("algorithm,file,elitism,mutation,interaction,time(ms)");
			
			File folder = new File("bugs");
			GA ga = new GA();
			
			ga.listFilesForFolder(folder);
		}
		
		
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
	        		
	        		List<List<SamplingAlgorithm>> algorithmsList = new ArrayList<List<SamplingAlgorithm>>();
	        		
	        		//LSA
	        		SamplingAlgorithm oneDisabled = new OneDisabledSampling();
	        		SamplingAlgorithm oneEnabled = new OneEnabledSampling();
	        		SamplingAlgorithm allEnabledDisabled = new AllEnabledDisabledSampling();
	        		
	        		List<SamplingAlgorithm> lsa = new ArrayList<>();
	        		lsa.add(oneDisabled);
	        		lsa.add(oneEnabled);
	        		lsa.add(allEnabledDisabled);
	        		
	        		algorithmsList.add(lsa);
	        		
	        		//PAIRWISE
	        		List<SamplingAlgorithm> pairwise = new ArrayList<>();
	        		pairwise.add(new TwiseSampling(2));
	        		algorithmsList.add(pairwise);
	        		
	        		//RANDOM SAMPLING LSA
	        		List<SamplingAlgorithm> randomSamplingLSA = new ArrayList<>();
	        		randomSamplingLSA.add(new RandomSamplingLSA());
	        		algorithmsList.add(randomSamplingLSA);
	        		
	        		//RANDOM SAMPLING PAIRWISE
	        		List<SamplingAlgorithm> randomSamplingPairwise = new ArrayList<>();
	        		randomSamplingPairwise.add(new RandomSamplingPairwise());
	        		algorithmsList.add(randomSamplingPairwise);
	        		
	        		List<Double> elistismLst = new ArrayList<Double>();
	        		elistismLst.add(0.1);
	        		elistismLst.add(0.2);
	        		elistismLst.add(0.3);
	        		
	        		List<Double> mutationLst = new ArrayList<Double>();
	        		mutationLst.add(0.1);
	        		mutationLst.add(0.2);
	        		mutationLst.add(0.3);
	        		
	        		for (List<SamplingAlgorithm> algorithms : algorithmsList) {
	        			for (double elitism : elistismLst) {
	        				for (double mutation : mutationLst) {
	        					double start = System.currentTimeMillis();
	        	        		
	        					//IS_RANDOM
	        					boolean random = false;
	        					for (SamplingAlgorithm algorithm : algorithms) {
	        						if (algorithm instanceof RandomSamplingLSA ||
	        								algorithm instanceof RandomSamplingPairwise) {
	        							
	        							random = true;
	        							
	        						}
	        					}
	        					
	        					String algorithmInitialPop = "";
	        					if (algorithms.size() == 3) {
	        						algorithmInitialPop = "LSA";
	        					} else {
	        						if (algorithms.get(0) instanceof TwiseSampling) {
	        							algorithmInitialPop = "Pairwise";
	        						} else if (algorithms.get(0) instanceof RandomSamplingLSA) {
	        							algorithmInitialPop = "RandomSamplingLSA";
	        						} else if (algorithms.get(0) instanceof RandomSamplingPairwise) {
	        							algorithmInitialPop = "RandomSamplingPairwise";
	        						}
	        					}
	        					
	        					
	        					System.out.print(algorithmInitialPop + "," + fileEntry.getAbsolutePath().split("bugs/")[1] + ","  + elitism + "," + mutation + ",");
	        					
	        	        		// ELITISM / MUTATION
	        	        		new GA().runGA(algorithms, fileEntry, random, elitism, mutation);
	        	        		double end = System.currentTimeMillis();
	        	        		System.out.print((int)(end-start) + "\n");
		        			}
	        			}
	        		}
	        		
	        	}
	        }
	    }
	}

	public void runGA(List<SamplingAlgorithm> algorithms, File file, boolean random, double elitismPercentage, double mutationPercentage) throws Exception {
		
		if (random) {
			int size = 0;
			
			if (algorithms.get(0) instanceof RandomSamplingLSA) {
				size = RandomSamplingLSA.popSize[GA.count];
			} else if (algorithms.get(0) instanceof RandomSamplingPairwise) {
				size = RandomSamplingPairwise.popSize[GA.count];
			}
			
			count++;
			
			RandomSampling.NUMBER_CONFIGS = size;
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
			if (score == 1.0) {
				bugDetected = true;
			}
		}
		
		//System.out.println("Interaction: 0");
		//System.out.println("Population size: " + initialPopulation.size() + "\n");
		
		if (bugDetected) {
			System.out.print("0,");
		}
		
		int interaction = 1;

		while (!bugDetected) {
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
			//System.out.println("Interaction: " + interaction);
			//System.out.println("Population size: " + newPopulation.size());
			
			boolean print = true;
			
			for (int i = 0; i < newPopulation.size(); i++) {
				double score = ga.getScoreFunctionValue(file, newPopulation.get(i));
				if (i == 0) {
					//System.out.println("Best: " + newPopulation.get(i) + ": " + score);
				}
				
				if (score == 1.0 && print) {
					bugDetected = true;
					print = false;
					System.out.print(interaction + ",");
				}
			}
			interaction++;
			currentPopulation = newPopulation;
			//System.out.println();
		}
		
	}

}
