package huginIntegration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class CleaningOutcome {
	public void cleanFile() throws IOException {
		String sourceFileName = "C:\\Users\\msam34\\git\\JGraphX_own\\editor\\huginIntegration\\SIIC_Output - Send Ann-David.txt";
		String destinationFileName = "C:\\Users\\msam34\\git\\JGraphX_own\\editor\\huginIntegration\\SIIC_Output-Send Ann-David.txt";

	    BufferedReader br = null;
	    PrintWriter pw = null; 
	    try {
	         br = new BufferedReader(new FileReader(sourceFileName));
	         pw =  new PrintWriter(new FileWriter(destinationFileName));
	         String line;
	         boolean doubleStarLine = false;
	         while ((line = br.readLine()) != null) {
	        	 if(!line.startsWith("Parse") && !line.startsWith("#####")) {
	        		 if(line.startsWith("*****")) {
	        			 if(doubleStarLine == false) {
	        				 doubleStarLine = true;
	        				 pw.println(line);
	        			 }
	        		 }
	        		 else if(line.startsWith("\t")){
	        			 doubleStarLine = false;
        				 pw.println(line);
	        		 }
	        	 }
	         }
	         br.close();
	         pw.close();
	    }catch (Exception e) {
	         e.printStackTrace();
	    }
	}
	public static void main(String args[]) throws IOException {
		CleaningOutcome co = new CleaningOutcome();
		co.cleanFile();
	}
}
