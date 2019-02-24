package ANTLR_NPP;

/***
 * Excerpted from "The Definitive ANTLR 4 Reference",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/tpantlr2 for more book information.
***/
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class NPPDriverAll {
    public static void main(String[] args) throws Exception {
    	
    	long startTime = System.currentTimeMillis();
    	
        String inputFile = null;
        String tempInputFile = "temp";
        int countError = 1, I;
        
        System.out.print("Loading classes ...");
        for(I = 1; I <= 129; I++){
        	if(I % 5 == 0) System.out.print(".");
        	inputFile = "Inputs\\" + I + ".oobn";
        	Scanner Sc = new Scanner (new File(inputFile));
        	
        	FileWriter write = new FileWriter(tempInputFile);
    		PrintWriter print_line = new PrintWriter(write);
    		
    		String line;
    		while(Sc.hasNext()){
    			line = Sc.nextLine();
    			line = line.split("%")[0];
    			print_line.println(line);
    		}
            print_line.close();
            
            String outputLogFile = "Inputs\\" + I + ".tree";
            FileWriter write2 = new FileWriter(outputLogFile);
    		PrintWriter print_line2 = new PrintWriter(write2);
    		
    		PrintStream out = new PrintStream(new FileOutputStream("Inputs\\"+I+".log"));
    		
    		System.setErr(out);
    		System.err.println("Now parsing tree for " + I + ".oobn");
    		
            inputFile = tempInputFile;
            InputStream is = System.in;
            if ( inputFile!=null ) is = new FileInputStream(inputFile);
            ANTLRInputStream input = new ANTLRInputStream(is);
            NETPlusPlusLexer lexer = new NETPlusPlusLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            NETPlusPlusParser parser = new NETPlusPlusParser(tokens);
            ParseTree tree = parser.prog(); // parse
            
            NPPvisit traverse = new NPPvisit();
            traverse.visit(tree);
            
            if (ThrowingErrorListener.errorCount>0){
            	System.out.println(ThrowingErrorListener.errorCount + "errors found in " + inputFile);
            	ThrowingErrorListener.errorCount = 0;
            }
            
            print_line2.println(tree.toStringTree(parser));
            
            print_line2.close();
            
            int lineCount = 0;
            LineNumberReader  lnr = new LineNumberReader(new FileReader(new File("Inputs\\"+I+".log")));
            lnr.skip(Long.MAX_VALUE);
//          System.out.println(lineCount = lnr.getLineNumber() + 1); //Add 1 because line index starts at 0
//          Finally, the LineNumberReader object should be closed to prevent resource leak
            lineCount = lnr.getLineNumber() + 1;
            lnr.close();
            
            if(lineCount > 2){
            	System.out.println("\n" + countError++ + ": Error found in file : " + I + "(" + lineCount + ")");
            	System.out.print("Loading remaining classes ...");
            }
        }
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        
        System.out.println("\n"+(I-countError)+" Classes are Loaded!!! \nTime required: " + elapsedTime + " miliseconds");
    }
}
