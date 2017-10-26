package dominated.seq.mining;

import java.io.IOException;

import dominated.seq.mining.*;

public class MainClass {

	public static void main(String[] args) throws IOException {
		//TODO Auto-generated method stub
		Replacefile fp=new Replacefile();
		fp.cleandata();
		
		GenesDatabase geneDatabase=new GenesDatabase();
		geneDatabase.readFile("newcommand.txt");
		geneDatabase.print();
		int minsup=242;
	        FeatAlgoGenerator feat = new FeatAlgoGenerator();
	        feat.runAlgorithm(geneDatabase, minsup); 
	        feat.writeResultTofile("output.txt");   
	}

}

