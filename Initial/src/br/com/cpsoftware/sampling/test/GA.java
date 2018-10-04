package br.com.cpsoftware.sampling.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.com.cpsoftware.sampling.core.GeneticSamplingAlgorithm;
import br.com.cpsoftware.sampling.core.SamplingAlgorithm;
import br.com.cpsoftware.sampling.core.algorithms.AllEnabledDisabledSampling;

public class GA {

	public static void main(String[] args) throws Exception {
		
		//RandomSampling randomSampling = new RandomSampling();
		//RandomSampling.NUMBER_CONFIGS = 30;
		
		SamplingAlgorithm allEnabledDisabled = new AllEnabledDisabledSampling();
		
		List<SamplingAlgorithm> algorithms = new ArrayList<>();
		//algorithms.add(randomSampling);
		algorithms.add(allEnabledDisabled);
		
		
		GeneticSamplingAlgorithm ga = new GeneticSamplingAlgorithm(algorithms);
		File file = new File("bugs/example/test.c");
		
		List<List<String>> initialPopulation = ga.getInitialPopulation(file);
		
		// Set minimum number of configuration in the initial population
		List<List<String>> extraConfigs = new ArrayList<>();
		if (initialPopulation.size() < 10) {
			int childs = 10 - initialPopulation.size();
			for (int i = 1; i <= childs; i++) {
				Random r = new Random();
				int low = 0;
				int high = initialPopulation.size()-1;
				int index = r.nextInt(high-low) + low;
				
				extraConfigs.add(initialPopulation.get(index));
			}
			initialPopulation.addAll(extraConfigs);
		}
		
		List<List<String>> currentPopulation = initialPopulation;
		
		//System.out.println("initialPopulation: " + initialPopulation.size());
		//System.out.println("currentPopulation: " + currentPopulation.size());
		
		boolean bugDetected = false;
		
		int interaction = 1;
		
		while (!bugDetected) {
			
			List<List<String>> newPopulation = new ArrayList<>();
			
			List<List<String>> eletism = ga.elitism(file, currentPopulation, 0.1);
			newPopulation.addAll(eletism);
			
			List<List<String>> childsBuffer = new ArrayList<>();
			List<List<String>> childs = new ArrayList<>();
			
			//System.out.println("currentPopulation: " + currentPopulation.size());
			
			while (currentPopulation.size() >= 2) {
				childsBuffer.addAll(ga.crossing(currentPopulation.get(0), currentPopulation.get(1)));
				childs.addAll(ga.crossing(currentPopulation.get(0), currentPopulation.get(1)));
				currentPopulation.remove(currentPopulation.get(0));
				currentPopulation.remove(currentPopulation.get(0));
			}
			
			//System.out.println("CHILD: " + childsBuffer.size());
			
			// Mutation for 30%
			for (int i = 0; i < (int)(childsBuffer.size()*0.3); i++) {
				
				Random r = new Random();
				int low = 0;
				int high = childs.size()-1;
				int index = r.nextInt(high-low) + low;
				
				childs.add(ga.mutation(childs.get(index), 0.3));
				
				low = 0;
				high = childs.size()-1;
				index = r.nextInt(high-low) + low;
				
				childs.add(ga.mutation(childs.get(index), 0.3));
				
				low = 0;
				high = childs.size()-1;
				index = r.nextInt(high-low) + low;
				
				childs.add(ga.mutation(childs.get(index), 0.3));
				
				low = 0;
				high = childs.size()-1;
				index = r.nextInt(high-low) + low;
				
				childs.add(ga.mutation(childs.get(index), 0.3));
				
			}
			
			newPopulation.addAll(childs);
			
			// Select best childs
			if (newPopulation.size() > 30) {
				newPopulation = ga.selectBest(file, newPopulation, 30);
			}
			
			// Showing the best results
			System.out.println("Interaction: " + interaction);
			System.out.println("Population size: " + newPopulation.size());
			
			for (int i = 0; i < newPopulation.size(); i++) {
				double score = ga.getScoreFunctionValue(file, newPopulation.get(i));
				if (i == 0) {
					System.out.println("Best: " + newPopulation.get(i) + ": " + score);
				}
				if (score == 1.0) {
					bugDetected = true;
				}
			}
			interaction++;
			currentPopulation = newPopulation;
			System.out.println();
		}
		
	}
	
}
