package  dominated.seq.mining;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import  dominated.seq.mining.*;

public class SequenceItemset {

	List<Character> itemsets = new ArrayList<Character>();
		/** sequence id */
		private int id; 
		public SequenceItemset(int id) {
			this.id = id;
		}
		public List<Character> getItemsets() {
			return itemsets;
		}

		public void addItemset(Character itemset) {
			itemsets.add(itemset);
		}
		public int getId() {
			return id;
		}

		public void print() {
			System.out.print(toString());
		}

		
public String toString() {
			StringBuilder r = new StringBuilder("");
			// for each itemset
			for (Character itemset : itemsets) {
				r.append(itemset);
				if(r.length()< itemsets.size()*2-1)
				r.append(',');
				}

			return r.append("    ").toString();
		}
	
		public Character get(int index) {
			return itemsets.get(index);
		}
		public int size() {
			return itemsets.size();
		}

		public SequenceItemset cloneSequenceMinusItems(Map<Character, Set<Integer>> sequenceID, int support) {
			// create a new sequence
			SequenceItemset sequence = new SequenceItemset(getId());
			List<Character> newItemset = new ArrayList<Character>();
			// for each  itemset in the original sequence
			for(Character item : itemsets){
				if(sequenceID.get(item).size() >= support){
					newItemset.add(item);
					sequence.addItemset(item);
				}
			} 
						return sequence; // return the new sequence
		}
		public List<String> cloneItemsetMinusItems(Map<String, Set<Integer>> sequenceID, List<String> itemset,int support) {
			// create a new itemset
			List<String> newItemset = new ArrayList<String>();
			// for each item of the original itemset
			for(String item : itemset){
				// if the support is enough
				// if the support is enough
				if(sequenceID.get(item).size() >= support){
					newItemset.add(item); // add it to the new itemset
				}
			}
			return newItemset; // return the new itemset.
		} 


}
