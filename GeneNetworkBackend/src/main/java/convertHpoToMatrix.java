
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author patri
 */
public class convertHpoToMatrix {

	/**
	 * @param args the command line arguments
	 * @throws java.io.IOException
	 * @throws java.lang.Exception
	 */
	public static void main(String[] args) throws IOException, Exception {

		final File hpoFile = new File("C:\\UMCG\\Genetica\\Projects\\GeneNetwork\\HPO\\135\\ALL_SOURCES_ALL_FREQUENCIES_diseases_to_genes_to_phenotypes.txt");
		final File ncbiToEnsgMapFile = new File("C:\\UMCG\\Genetica\\Projects\\GeneNetwork\\ensgNcbiId.txt");
		final File geneOrderFile = new File("C:\\UMCG\\Genetica\\Projects\\GeneNetwork\\geneOrder.txt");
		final File outputFile = new File(hpoFile.getAbsolutePath() + "_matrix.txt");

		HashMap<String, String> ncbiToEnsgMap = loadNcbiToEnsgMap(ncbiToEnsgMapFile);

		HashMap<String, HashSet<String>> hpoToGenes = readHpoFile(hpoFile, ncbiToEnsgMap);

		ArrayList<String> geneOrder = readGenes(geneOrderFile);
		
		System.out.println("Total HPO terms: " + hpoToGenes.size());
		System.out.println("Genes in order file: " + geneOrder.size());
		
		

	}

	private static HashMap<String, String> loadNcbiToEnsgMap(File ncbiToEnsgMapFile) throws FileNotFoundException, IOException, Exception {

		final CSVParser parser = new CSVParserBuilder().withSeparator('\t').withIgnoreQuotations(true).build();
		final CSVReader reader = new CSVReaderBuilder(new BufferedReader(new FileReader(ncbiToEnsgMapFile))).withSkipLines(0).withCSVParser(parser).build();

		String[] nextLine = reader.readNext();

		if (!nextLine[0].equals("Gene stable ID") || !nextLine[1].equals("NCBI gene ID")) {
			throw new Exception("Header of ncbi to ensg map should be: \"Gene stable ID	NCBI gene ID\"");
		}

		HashMap<String, String> ncbiToEnsgMap = new HashMap<>(70000);

		while ((nextLine = reader.readNext()) != null) {
			ncbiToEnsgMap.put(nextLine[1], nextLine[0]);
		}

		return ncbiToEnsgMap;

	}

	private static HashMap<String, HashSet<String>> readHpoFile(File hpoFile, HashMap<String, String> ncbiToEnsgMap) throws Exception {

		final CSVParser hpoParser = new CSVParserBuilder().withSeparator('\t').withIgnoreQuotations(true).build();
		final CSVReader hpoReader = new CSVReaderBuilder(new BufferedReader(new FileReader(hpoFile))).withSkipLines(1).withCSVParser(hpoParser).build();

		HashMap<String, HashSet<String>> hpoToGenes = new HashMap<>();

		String[] nextLine;
		while ((nextLine = hpoReader.readNext()) != null) {
			String hpo = nextLine[3];
			String ncbiId = nextLine[2];
			String ensgId = ncbiToEnsgMap.get(ncbiId);
			if (ensgId == null) {
				System.err.println("Missing mapping for gene: " + ncbiId + " " + nextLine[1]);
			} else {

				HashSet<String> hpoGenes = hpoToGenes.get(hpo);
				if (hpoGenes == null) {
					hpoGenes = new HashSet<>();
					hpoToGenes.put(hpo, hpoGenes);
				}

				hpoGenes.add(ensgId);

			}

		}

		return hpoToGenes;

	}

	private static ArrayList<String> readGenes(File geneOrderFile) throws IOException {
		
		final CSVParser parser = new CSVParserBuilder().withSeparator('\t').withIgnoreQuotations(true).build();
		final CSVReader reader = new CSVReaderBuilder(new BufferedReader(new FileReader(geneOrderFile))).withSkipLines(0).withCSVParser(parser).build();

		String[] nextLine;
		ArrayList<String> geneOrder = new ArrayList<>();
		
		while ((nextLine = reader.readNext()) != null) {
			
			geneOrder.add(nextLine[0]);
			
		}
		
		return geneOrder;
		
	}

}
