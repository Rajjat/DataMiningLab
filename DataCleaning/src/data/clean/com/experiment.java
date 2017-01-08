package data.clean.com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class experiment {
	void fun1()
	{
       try
       {
          File file = new File("newcommand.txt");
          BufferedReader reader = new BufferedReader(new FileReader(file));
          String line = "", oldtext = "";
          while((line = reader.readLine()) != null)
          {
    	      oldtext += line + "3\r\n";
          }
          reader.close();
          

         String replacedtext  = oldtext.replaceAll("", "" + " 1 ");
         replacedtext=replacedtext.replaceAll("1 \\+ 1   1", "" + "+");
         replacedtext=replacedtext.replaceAll("1 - 1   1", "" + "-");
         replacedtext=replacedtext.replaceAll("1\\s\\n","" + "");
         replacedtext=replacedtext.replaceAll("  \\+","" + "+");
         replacedtext=replacedtext.replaceAll("  -","" + "-");
         replacedtext=replacedtext.replaceAll("1 -","" + "1 N");
         replacedtext=replacedtext.replaceAll("3 1 ","" + "3");
         replacedtext=replacedtext.replaceAll("  1 ","" + "");
         replacedtext=replacedtext.replaceAll(" \\+","" + "+");
         FileWriter writer = new FileWriter("newcommand2.txt");
         writer.write(replacedtext);
         writer.close();
         if(file.delete()){
 			System.out.println(file.getName() + " is deleted!");
 		}else{
 			System.out.println("Delete operation is failed.");
 		}


      }
      catch (IOException ioe)
     {
        ioe.printStackTrace();
     }
   }
}
