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
	
	public void FindNWriteTabular() {
		String sourceFileName = "C:\\Users\\msam34\\git\\iOOBNFinal_v1\\iOOBNFinal\\editor\\huginIntegration\\SIIC_Output_Extended.txt";
		String destinationFileName = "C:\\Users\\msam34\\git\\iOOBNFinal_v1\\iOOBNFinal\\editor\\huginIntegration\\SIIC_Output_tabular.txt";

	    BufferedReader br = null;
	    PrintWriter pw = null; 
	    try {
	         br = new BufferedReader(new FileReader(sourceFileName));
	         pw =  new PrintWriter(new FileWriter(destinationFileName));
	         // RComplexity = real complexity
//	         String str = "NumOfNodes     NumOfAvgPar \t NumOfStates \t NumOfClass \t   NumOfObj \t     Hugin \t       SIIC  \t     Preproc\n";
	         String str = "Fold   \t NumOfNodes     NumOfAvgPar \t NumOfStates \t NumOfClass \t   NumOfObj \t     RComplexity\t    Hugin-JTCost \t       SIIC-JTCost   \t  Hugin   \t     SIIC\n";
	         pw.print(str);
	         String line;
	         boolean doubleStarLine = false;
	         while ((line = br.readLine()) != null) {
	        	 if(line.startsWith("(){}<>{}()")) {
	        		 line = line.replace("(){}<>{}()", "  ");
	        		 String data[] = line.trim().split("\\s+");
	        		 
	        		 str = "";
	        		 int temp = 0;
	        		 try {
	        			 temp = Integer.parseInt(data[data.length-2])-Integer.parseInt(data[data.length-1]);// assuming last two data are totalSIIC and preprocessing time, so SIIC = totalSIIC - preprocessing
	        		 }
	        		 catch(RuntimeException re) {
	        			 System.err.println(re);
	        		 }
	        		 str += data[0];
	        		 for(int i = 1; i < data.length-2; i++) {
	        			 str += "\t\t"+ data[i];
//	        			 System.out.print("data = " + data[i]);
	        		 }
//	        		 System.out.println();
	        		 str += "\t\t" + Integer.toString(temp);
	        		 pw.println(str);
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
//		co.cleanFile();
		co.FindNWriteTabular();
	}
}
