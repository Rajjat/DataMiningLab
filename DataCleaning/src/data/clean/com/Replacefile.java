package data.clean.com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Replacefile {
	 public static void main(String[] args) {
	 {
	    try
	      {
	             File file = new File("splice.txt");
	             BufferedReader reader = new BufferedReader(new FileReader(file));
	             String line = "", oldtext = "";
	             while((line = reader.readLine()) != null)
	                 {
	                 oldtext += line + "\r\n";
		             }
	             reader.close();

	             String replacedtext  = oldtext.replaceAll("EI", "" + "+");

	             replacedtext = replacedtext.replaceAll("IE", "" + "-");
	             replacedtext = replacedtext.replaceAll("N", "" + "-");
	             replacedtext = replacedtext.replaceAll(",(.*)-(.*)-(.*),","" +" ");
	             replacedtext= replacedtext.replaceAll("( )+", " ");
	             FileWriter writer = new FileWriter("newcommand.txt");
	             writer.write(replacedtext);
	             writer.close();
	         }
	         catch (IOException ioe)
	             {
	             ioe.printStackTrace();
	         }
	     }
	 //experiment data1=new experiment();
     // data1.fun1();

	  }


}
