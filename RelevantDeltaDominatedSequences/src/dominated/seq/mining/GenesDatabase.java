package dominated.seq.mining;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import dominated.seq.mining.*;

public class GenesDatabase {	 
	private final List<SequenceItemset> sequences = new ArrayList<SequenceItemset>();
	public void readFile(String path) throws IOException {
		String currentline;
		BufferedReader myInput = null;
		try {
			FileInputStream fileinput = new FileInputStream(new File(path));
			myInput = new BufferedReader(new InputStreamReader(fileinput));
			int i=1;
			while ((currentline = myInput.readLine()) != null) 
			{
				if (currentline.isEmpty() == false )
				{
                                  if(currentline.contains("-"))//considering only negative sequences
                                   {
               	                      seqAdd(currentline);
                                    }
                                    i++;
		                 }
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (myInput != null) {
				myInput.close();
			}
		}
	}

	void seqAdd(String tokens) { 
		char[] charArray=tokens.toCharArray();
		SequenceItemset sequence=new SequenceItemset(sequences.size());
		List<Character> item = new ArrayList<Character>();
		for(int i=0;i<charArray.length;i++)
		{
			if(charArray[i]=='-')
				continue;
			else if(charArray[i]==' ')
				continue;
			else{
			sequence.addItemset(charArray[i]);
			item.add(charArray[i]);
			}
		}
		sequences.add(sequence);
		                                 
	}
	public void print() 
	{
	   System.out.println("***SEQUENTIAL DATABASE WITH NEGATIVE CLASS LABEL****");	
	   for (SequenceItemset sequence : sequences) 
	   { 
	     System.out.print(sequence.getId() + ":  ");
	     sequence.print();
	     System.out.println("");
	   }
	}
	public List<SequenceItemset> getSequences() {
		return sequences;
	}
	public int size() {
		return sequences.size();
	}

}

	



