package br.com.cpsoftware.sampling.checker;

import java.io.File;
import java.util.List;

import br.com.cpsoftware.sampling.core.SamplingAlgorithm;
import br.com.cpsoftware.sampling.core.algorithms.OneDisabledSampling;
import br.com.cpsoftware.sampling.core.algorithms.OneEnabledSampling;
import jxl.Sheet;
import jxl.Workbook;

public class BugsChecker {
	
	public static final String SOURCE_LOCATION = "bugs/";
	
	public int configurations = 0;
	public int bugs = 0;
	
	public static void main(String[] args) throws Exception {
		
		double start = System.currentTimeMillis();
		BugsChecker checker = new BugsChecker();
		checker.configurations = 0;
		checker.bugs = 0;
		System.out.println("One-Enabled Sampling");
		checker.checkSampling(new OneEnabledSampling());
		double end = System.currentTimeMillis();
		System.out.println("Time: " + (end-start) + "\n");
		
		
		
		/*start = System.currentTimeMillis();
		checker = new BugsChecker();
		checker.configurations = 0;
		checker.bugs = 0;
		System.out.println("One-Disabled Sampling");
		checker.checkhSampling(new OneDisabledSampling());
		end = System.currentTimeMillis();
		System.out.println("Time: " + (end-start) + "\n");
		
		start = System.currentTimeMillis();
		checker = new BugsChecker();
		checker.configurations = 0;
		checker.bugs = 0;
		System.out.println("All-Enabled-Disabled Sampling");
		checker.checkhSampling(new AllEnabledDisabledSampling());
		end = System.currentTimeMillis();
		System.out.println("Time: " + (end-start) + "\n");
		
		start = System.currentTimeMillis();
		checker = new BugsChecker();
		checker.configurations = 0;
		checker.bugs = 0;
		System.out.println("Pair-wise Sampling");
		checker.checkhSampling(new TwiseSampling(2));
		end = System.currentTimeMillis();
		System.out.println("Time: " + (end-start) + "\n");
		
		start = System.currentTimeMillis();
		checker = new BugsChecker();
		checker.configurations = 0;
		checker.bugs = 0;
		System.out.println("Three-wise Sampling");
		checker.checkhSampling(new TwiseSampling(3));
		end = System.currentTimeMillis();
		System.out.println("Time: " + (end-start) + "\n");

		start = System.currentTimeMillis();
		checker = new BugsChecker();
		checker.configurations = 0;
		checker.bugs = 0;
		System.out.println("Four-wise Sampling");
		checker.checkhSampling(new TwiseSampling(4));
		end = System.currentTimeMillis();
		System.out.println("Time: " + (end-start) + "\n");
		
		start = System.currentTimeMillis();
		checker = new BugsChecker();
		checker.configurations = 0;
		checker.bugs = 0;
		System.out.println("Five-wise Sampling");
		checker.checkhSampling(new TwiseSampling(5));
		end = System.currentTimeMillis();
		System.out.println("Time: " + (end-start) + "\n");
		
		start = System.currentTimeMillis();
		checker = new BugsChecker();
		checker.configurations = 0;
		checker.bugs = 0;
		System.out.println("Six-wise Sampling");
		checker.checkhSampling(new TwiseSampling(6));
		end = System.currentTimeMillis();
		System.out.println("Time: " + (end-start) + "\n");*/
	}

	public void checkSampling(SamplingAlgorithm algorithm) throws Exception{
		File inputWorkbook = new File("bugs.xls");
		Workbook w = Workbook.getWorkbook(inputWorkbook);
		Sheet sheet = w.getSheet(0);
		
		String project, path, presenceCondition = null;
		
		for (int i = 0; i < sheet.getRows(); i++) {
			
			project = sheet.getCell(0, i).getContents();
			path = sheet.getCell(3, i).getContents();
			presenceCondition = sheet.getCell(4, i).getContents();
			
			presenceCondition = presenceCondition.replaceAll("\\s", "");
			String[] options = presenceCondition.split("\\)\\|\\|\\(");
			boolean detected = false;
			
			for (String option : options){
				String[] macros = option.split("&&");
				
				this.checkingMissingMacros(new File(BugsChecker.SOURCE_LOCATION + project + "/" + path), macros);
				
				List<List<String>> samplings = algorithm.getSamples(new File(BugsChecker.SOURCE_LOCATION + project + "/" + path));
				this.configurations += samplings.size();
				
				detected = this.doesSamplingWork(macros, samplings);
				if (detected){
					bugs++;
					break;
				}	
			}
		}
		
		// It counts the number of configurations in C source files without faults.
		this.listAllFiles(new File("code"), algorithm);
		
		//System.out.println("Bugs: " + bugs);
		//System.out.println("Configurations: " + configurations);
		
		// Total number of configuration / total number of files in all projects.
		//System.out.println("Configurations per file:" + ((double)configurations)/50078);
		
	}
	
	
	public void checkSampling(SamplingAlgorithm algorithm, File file) throws Exception{
		File inputWorkbook = new File("bugs.xls");
		Workbook w = Workbook.getWorkbook(inputWorkbook);
		Sheet sheet = w.getSheet(0);
		
		String project, path, presenceCondition = null;
		
		for (int i = 0; i < sheet.getRows(); i++) {
			
			project = sheet.getCell(0, i).getContents();
			path = sheet.getCell(3, i).getContents();
			presenceCondition = sheet.getCell(4, i).getContents();
			
			File lineFile = new File(BugsChecker.SOURCE_LOCATION + project + "/" + path);
			if (!lineFile.getAbsolutePath().equals(file.getAbsolutePath())) {
				continue;
			}
			
			presenceCondition = presenceCondition.replaceAll("\\s", "");
			String[] options = presenceCondition.split("\\)\\|\\|\\(");
			boolean detected = false;
			
			for (String option : options){
				String[] macros = option.split("&&");
				
				this.checkingMissingMacros(new File(BugsChecker.SOURCE_LOCATION + project + "/" + path), macros);
				
				List<List<String>> samplings = algorithm.getSamples(new File(BugsChecker.SOURCE_LOCATION + project + "/" + path));
				this.configurations += samplings.size();
				
				detected = this.doesSamplingWork(macros, samplings);
				if (detected){
					bugs++;
					break;
				}	
			}
		}
		
		// It counts the number of configurations in C source files without faults.
		this.listAllFiles(new File("code"), algorithm);
		
		//System.out.println("Bugs: " + bugs);
		//System.out.println("Configurations: " + configurations);
		
		// Total number of configuration / total number of files in all projects.
		//System.out.println("Configurations per file:" + ((double)configurations)/50078);
		
	}
	
	public int getNumberOfBug(File file) throws Exception{
		File inputWorkbook = new File("bugs.xls");
		Workbook w = Workbook.getWorkbook(inputWorkbook);
		Sheet sheet = w.getSheet(0);
		
		String project, path = null;
		
		int bugs = 0;
		
		for (int i = 0; i < sheet.getRows(); i++) {
			
			project = sheet.getCell(0, i).getContents();
			path = sheet.getCell(3, i).getContents();
			
			File lineFile = new File(BugsChecker.SOURCE_LOCATION + project + "/" + path);
			if (lineFile.getAbsolutePath().equals(file.getAbsolutePath())) {
				bugs++;
			} 
			
			
		}
		return bugs;
	}
	
	public void listAllFiles(File path, SamplingAlgorithm algorithm) throws Exception{
		if (path.isDirectory()){
			for (File file : path.listFiles()){
				this.listAllFiles(file, algorithm);
			}
		} else {
			if (path.getName().endsWith(".c")){
				List<List<String>> samplings = algorithm.getSamples(path);
				this.configurations += samplings.size();
			}
		}
	}
	
	public boolean doesSamplingWork(String[] macros, List<List<String>> samplings) throws Exception{
		for (List<String> configuration : samplings){
			boolean containsAll = true;
			
			for (String macro : macros){
				macro = macro.replace("(", "").replace(")", "").replaceAll("\\s", "");
				
				if (!configuration.contains(macro)){
					containsAll = false;
				}
			}
			if (containsAll){
				return true;
			}
		}
		return false;
	}
	
	public void checkingMissingMacros(File file, String[] macros) throws Exception{
		
		SamplingAlgorithm pairwise = new OneDisabledSampling();
		pairwise.getSamples(file);
		List<String> directives = SamplingAlgorithm.getDirectives(file);
		
		for (String macro : macros){
			macro = macro.replace("!", "").replace("(", "").replace(")", "");
			if (!directives.contains(macro)){
				System.out.println("PROBLEM: " + file.getAbsolutePath());
				System.out.println("PROBLEM: " + directives);
				System.out.println("PROBLEM: " + macro);
			}
		}

	}
	
}
