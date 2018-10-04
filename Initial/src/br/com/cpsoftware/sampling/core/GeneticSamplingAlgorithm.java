package br.com.cpsoftware.sampling.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import br.com.cpsoftware.sampling.checker.BugsChecker;
import br.com.cpsoftware.sampling.core.algorithms.OneDisabledSampling;
import br.com.cpsoftware.sampling.core.algorithms.OneEnabledSampling;
import br.com.cpsoftware.sampling.core.algorithms.TwiseSampling;

public class GeneticSamplingAlgorithm extends SamplingAlgorithm {

	public static void main(String[] args) throws Exception {
		System.out.println("File: bugs/example/test.c");
		System.out.println("Configs: A, B, C, D, E, F");
		System.out.println("Number of bugs in this file: 1");
		System.out.println("Presence Condition of the Bug: A, B, C, D, E, !F\n\n");
		
		SamplingAlgorithm allEnabledDisabled = new TwiseSampling(2);
		SamplingAlgorithm oneEnabled = new OneEnabledSampling();
		SamplingAlgorithm oneDisabled = new OneDisabledSampling();

		List<SamplingAlgorithm> algorithms = new ArrayList<>();
		algorithms.add(oneDisabled);
		algorithms.add(oneEnabled);
		algorithms.add(allEnabledDisabled);

		GeneticSamplingAlgorithm ga = new GeneticSamplingAlgorithm(algorithms);

		File file = new File("bugs/example/test.c");
		List<List<String>> initialPopulation = ga.getInitialPopulation(file);

		System.out.println("Initial Population\n");
		for (int i = 0; i < initialPopulation.size(); i++) {
			System.out.println(initialPopulation.get(i));
			System.out.println("Score Function: " + ga.getScoreFunctionValue(file, initialPopulation.get(i)) + "\n");
		}
		
		System.out.println("Selected Configurations (Elitism)\n");
		List<List<String>> selectedConfigs = ga.elitism(file, initialPopulation, 0.1);
		for (int i = 0; i < selectedConfigs.size(); i++) {
			System.out.println(selectedConfigs.get(i) + " - Score Function: " + ga.getScoreFunctionValue(file, selectedConfigs.get(i)));
		}
		
		System.out.println("\n\nMutation\n");
		System.out.println("Original: " + initialPopulation.get(0));
		System.out.println("Mutant: " + ga.mutation(initialPopulation.get(0), 0.2));
		
		System.out.println("\n\nCrossing\n");
		
		List<List<String>> childs = ga.crossing(initialPopulation.get(0), initialPopulation.get(3));
		
		for (int i = 0; i < childs.size(); i++) {
			System.out.println("Child " + i + ": " + childs.get(i));
		}
		System.out.println();
		
		childs = ga.crossing(initialPopulation.get(2), initialPopulation.get(3));
		
		for (int i = 0; i < childs.size(); i++) {
			System.out.println("Child " + i + ": " + childs.get(i));
		}
		System.out.println();
	}

	private List<SamplingAlgorithm> initialPopulationSelecion;

	public GeneticSamplingAlgorithm(List<SamplingAlgorithm> initialPopulationSelection) {
		this.initialPopulationSelecion = initialPopulationSelection;
	}

	public List<List<String>> crossing(List<String> configuration1, List<String> configuration2) {
		List<List<String>> childs = new ArrayList<>();
		
		Comparator<String> strCompa = new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				o1 = o1.replace("!", "");
				o2 = o2.replace("!", "");
				return o1.compareTo(o2);
			}
		};
		
		Collections.sort(configuration1, strCompa);
		Collections.sort(configuration2, strCompa);
		
		System.out.println("Parent 1: " + configuration1);
		System.out.println("Parent 2: " + configuration2);
		
		Random r = new Random();
		int low = 1;
		int high = configuration1.size();
		int result = r.nextInt(high-low) + low;
		
		System.out.println("Partition: " + result);
		
		List<String> newConfig1 = new ArrayList<>();
		for (int i = 0; i < result; i++) {
			newConfig1.add(configuration1.get(i));
		}
		
		for (int i = result; i < configuration1.size(); i++) {
			newConfig1.add(configuration2.get(i));
		}
		
		childs.add(newConfig1);
		
		//low = configuration1.size() / 2;
		//high = configuration1.size();
		//result = r.nextInt(high-low) + low;
		
		List<String> newConfig2 = new ArrayList<>();
		for (int i = 0; i < result; i++) {
			newConfig2.add(configuration2.get(i));
		}
		
		for (int i = result; i < configuration1.size(); i++) {
			newConfig2.add(configuration1.get(i));
		}
		
		childs.add(newConfig2);
		
		return childs;
	}
	
	public List<String> mutation(List<String> configuration, double percentual){
		
		int changes = (int)(configuration.size() * percentual);
		
		for (int i = 1; i <= changes; i++) {
			Random r = new Random();
			int low = 0;
			int high = configuration.size()-1;
			int result = r.nextInt(high-low) + low;
			
			String currentValue = configuration.get(result);
			String newValue = "";
			if (currentValue.startsWith("!")) {
				newValue = currentValue.replace("!", "");
			} else {
				newValue = "!" + currentValue;
			}
			
			configuration.set(result, newValue);
		}
		return configuration;
	}
	
	public List<List<String>> elitism(File file, List<List<String>> samples, double percentual) throws Exception {
		Map<List<String>, Double> configurations = new HashMap<>();

		for (int i = 0; i < samples.size(); i++) {
			List<String> sample = samples.get(i);
			configurations.put(sample, this.getScoreFunctionValue(file, sample));
		}

		Map<List<String>, Double> sorted = sortByComparator(configurations, false);

		List<List<String>> selectedConfigurations = new ArrayList<>();
		
		int limitSize = (int) (samples.size() * percentual);
		
		Iterator<Map.Entry<List<String>,Double>> it = sorted.entrySet().iterator();
	    while (it.hasNext() && selectedConfigurations.size() <= limitSize) {
	        Map.Entry<List<String>,Double> pair = (Map.Entry<List<String>,Double>)it.next();
	        selectedConfigurations.add(pair.getKey());
	        it.remove();
	    }
		
		return selectedConfigurations;
	}

	public double getScoreFunctionValue(File file, List<String> configuration) throws Exception {
		BugsChecker checker = new BugsChecker();
		checker.configurations = 0;
		checker.bugs = 0;

		SamplingAlgorithm singleConfiguration = new SamplingAlgorithm() {

			@Override
			public List<List<String>> getSamples(File file) throws Exception {

				List<List<String>> sample = new ArrayList<List<String>>();
				sample.add(configuration);
				return sample;
			}
		};

		checker.checkhSampling(singleConfiguration);

		return checker.bugs;
	}

	public List<List<String>> getInitialPopulation(File file) throws Exception {
		return this.getSamples(file);
	}

	@Override
	public List<List<String>> getSamples(File file) throws Exception {
		List<List<String>> samples = new ArrayList<>();

		for (int i = 0; i < this.initialPopulationSelecion.size(); i++) {
			samples.addAll(this.initialPopulationSelecion.get(i).getSamples(file));
		}

		return samples;
	}

	private static Map<List<String>, Double> sortByComparator(Map<List<String>, Double> unsortMap, final boolean order) {

		List<Entry<List<String>, Double>> list = new LinkedList<Entry<List<String>, Double>>(unsortMap.entrySet());

		Collections.sort(list, new Comparator<Entry<List<String>, Double>>() {
			public int compare(Entry<List<String>, Double> o1, Entry<List<String>, Double> o2) {
				if (order) {
					return o1.getValue().compareTo(o2.getValue());
				} else {
					return o2.getValue().compareTo(o1.getValue());

				}
			}
		});

		Map<List<String>, Double> sortedMap = new LinkedHashMap<List<String>, Double>();
		for (Entry<List<String>, Double> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}
}
