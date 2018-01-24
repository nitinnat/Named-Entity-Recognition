package com.dutta.nlp.core_nlp_example;

/**
 * Hello world!
 *
 */

import java.util.*;
import java.util.stream.Collectors;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;


public class BasicPipelineExample {

    public static void main(String[] args)  {

    	//Read the file and load the lines
    	String fileName = "C:\\Users\\Nitin\\Documents\\Projects\\Haimonti - Tweet Prejudice\\unique_tweets_sw_removed.txt";
    	
    	String line = null;
    	List<String> text_list = new ArrayList<String>();
    	try {
    		FileReader fileReader = new FileReader(fileName);
    		// Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
            	
            	    text_list.add(line);
            }
            
         // Always close files.
            bufferedReader.close();
    	}
    	catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
    	 catch(IOException ex) {
             System.out.println(
                 "Error reading file '" 
                 + fileName + "'");                  
             // Or we could just do this: 
             // ex.printStackTrace();
         }
        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        int count = 0;
        boolean found = false;
        
        
        //data array to store 
        List<String[]> data = new ArrayList<String[]>();
        data.add(new String[]{"SENTENCE", "NAMED ENTITIES"});
        List<String[]> all_entities = new ArrayList<String[]>();
        all_entities.add(new String[]{"NAMED ENTITIES"});
        for (String text : text_list)
        {
        	//if (count > 100){break;};
	        // create an empty Annotation just with the given text
	        Annotation document = new Annotation(text);
	        String ner_words = "";
	        // run all Annotators on this text
	        
	        pipeline.annotate(document);
	        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
	        for(CoreMap sentence: sentences) 
	        {
	        	
	        	  // traversing the words in the current sentence
	        	  // a CoreLabel is a CoreMap with additional token-specific methods
	        	  for (CoreLabel token: sentence.get(TokensAnnotation.class)) 
	        	  {
	        	    // this is the text of the token
	        		  
		        	    String word = token.get(TextAnnotation.class);
		        	    //Check if the whole word is in uppercase. If that's the case, then don't convert it to lowercase
		        	    if (word.toUpperCase() != word) {
		        	    	word = word.toLowerCase();
		        	    }
		        	    
		        	    // this is the NER label of the token
		        	    String ne = token.get(NamedEntityTagAnnotation.class);
		        	   
		        	    if (ne.equals("PERSON")) 
		        	    {
		        	    	System.out.println(String.format("count [%d] sentence: [%s] WORD  [%s] ne: [%s]", count,text,word,ne));
		        	    	ner_words += word;
		        	    	all_entities.add(new String[] {word});
		        	    	ner_words += " ";
		        	    	found = true;
		        	    	
		        	    }
	        	    
	        	  }
        	  
        	  
	        }	
	        data.add(new String[]{text, ner_words});
	    if (found == false) {System.out.println(count);}
	    
	    
        
        count += 1;
        
        }
      
        
        //Write document and corresponding entities into a CSV file
        CSVWriter writer;
    	
		try {
			String csv = "C:\\Users\\Nitin\\Documents\\Projects\\Haimonti - Tweet Prejudice\\named_entities.csv";
			writer = new CSVWriter(new FileWriter(csv));
			writer.writeAll(data);
			writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	 
      
    
    System.out.println("CSV File written successfully All at a time");
    /*------------------------------------------------------------------------------------------------------*/
        
        //Convert all_entities to a set and write into another csv file
        Set<String[]> unique_entities = new HashSet<>(all_entities);
        List<String[]> list_entities = new ArrayList<String[]>(unique_entities);

        
		try {
			String csv = "C:\\Users\\Nitin\\Documents\\Projects\\Haimonti - Tweet Prejudice\\unique_entities.csv";
			writer = new CSVWriter(new FileWriter(csv));
			writer.writeAll(list_entities);
			writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 System.out.println("Unique entities written to CSV file.");
		 
		 
		/*------------------------------------------------------------------------------------------------------*/
		 //Read existing csv and compare existence of entities
		
				 
				//Read the file and load the lines
		 String fileNameUniqueUnigrams = "C:\\Users\\Nitin\\Documents\\Projects\\Haimonti - Tweet Prejudice\\unigrams_handcoding_new.csv";	
		 List<String> loaded_entities = new ArrayList<String>();
		 List<String> group_indicators = new ArrayList<String>();	    	
		 List<String> individual_indicators = new ArrayList<String>();
		 try (
		            //Reader reader = Files.newBufferedReader(Paths.get(fileNameUniqueUnigrams));
		            CSVReader csvReader = new CSVReader(new FileReader(fileNameUniqueUnigrams), ',','"',1);

		        ) {
		            // Reading Records One by One in a String array
		            String[] nextRecord;
		            while ((nextRecord = csvReader.readNext()) != null) {
		            	
		            	loaded_entities.add(nextRecord[0]); 
		                group_indicators.add(nextRecord[1]);
		                individual_indicators.add(nextRecord[2]);
		            }
		        } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		 
		 //Get all the entities where group is 1 or individual is 1
		 int num_unique_ents = loaded_entities.size();
		 List<String> filtered_loaded_ents = new ArrayList<String>();
		 for (int i = 0; i < num_unique_ents; i++) {
			 
			 if (i>10) {break;}
			 if (group_indicators.get(i).equals("1") || individual_indicators.get(i).equals("1")) {
				 
				 filtered_loaded_ents.add(loaded_entities.get(i));
			 }
			
		}
		 Set<String> filtered_loaded_ents_set = new HashSet<>(filtered_loaded_ents);
	/*------------------------------------------------------------------------------------------------------*/	 
       //Need to compare unique_entities obtained using Stanford NLP with filtered_loaded_ents_set obtained through handcoding method
		 List<String[]> ents_comparisons = new ArrayList<String[]>();
		 ents_comparisons.add(new String[]{"NAMED ENTITY","HANDCODING", "STANFORD NLP"});
		 int num_final_ents = filtered_loaded_ents.size();
		 for (int i = 0; i < num_final_ents; i++) {
			 if (unique_entities.contains(filtered_loaded_ents.get(i))) {
				 //Element exists in handcoded entities and in Stanford NLP entities
				 ents_comparisons.add(new String[]{filtered_loaded_ents.get(i),"Yes", "Yes"});
				
			 }
			 else {
				 ents_comparisons.add(new String[]{filtered_loaded_ents.get(i),"Yes", "No"});
			 }
			 
		 }
		 
		 //Write ents_comparisons to a csv file
		 try {
				String csv = "C:\\Users\\Nitin\\Documents\\Projects\\Haimonti - Tweet Prejudice\\entities_comparison.csv";
				writer = new CSVWriter(new FileWriter(csv));
				writer.writeAll(ents_comparisons);
				writer.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    	 
	      
	    
	    System.out.println("Comparisons written to a entities_comparison.csv successfully");

}
}
