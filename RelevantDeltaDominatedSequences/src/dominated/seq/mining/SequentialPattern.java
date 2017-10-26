package dominated.seq.mining;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import dominated.seq.mining.*;

public class SequentialPattern {
	// the list of itemsets
	private final List<Itemsetcreate> itemsets;

	// IDs of sequences containing this pattern
	private Set<Integer> sequencesIds;

	private int itemCount = -1;
	public void setSequenceIDs(Set<Integer> sequencesIds) {
		this.sequencesIds = sequencesIds;
	}
	public List<Itemsetcreate> getItemsets() {
		return itemsets;
	}
	
	/**
	 * Defaults constructor
	 */
	public SequentialPattern() {
		itemsets = new ArrayList<Itemsetcreate>();
	}
	public Itemsetcreate get(int index) {
		return itemsets.get(index);
	}
	public int size(){
		return itemsets.size();
	}


	public SequentialPattern(Itemsetcreate itemset, Set<Integer> sequencesIds) {
		itemsets = new ArrayList<Itemsetcreate>();
		this.itemsets.add(itemset);
		this.sequencesIds = sequencesIds;
	}

	public SequentialPattern(List<Itemsetcreate> itemsets, Set<Integer> sequencesIds) {
		this.itemsets = itemsets;
		this.sequencesIds = sequencesIds;
	}
	public Set<Integer> getSequenceIDs() {
		return sequencesIds;
	}
	
	public void addItemset(Itemsetcreate itemset) {
		// itemCount += itemset.size();
		itemsets.add(itemset);
	}
	public SequentialPattern cloneSequence(){
		// create a new empty sequential pattenr
		SequentialPattern sequence = new SequentialPattern();
		// for each itemset
		for(Itemsetcreate itemset : itemsets){
			// make a copy and add it
			sequence.addItemset(itemset.cloneItemSet());
		}
		return sequence;
	}
	public int getAbsoluteSupport() {
		return sequencesIds.size();
	}
	
	public int getItemOccurencesTotalCount(){
		if(itemCount == -1) {
			itemCount =0;
			// for each itemset
			for(Itemsetcreate itemset : itemsets){
				// add the size of this itemset
				itemCount += itemset.size();
			}
		}
		return itemCount; // return the total size.
	}
	public void print() {
		System.out.print(toString());
	}
	
	/**
	 * Get a string representation of this sequential pattern, 
	 * containing the sequence IDs of sequence containing this pattern.
	 */
	public String toString() {
		StringBuilder r = new StringBuilder("");
		// For each itemset in this sequential pattern
		for(Itemsetcreate itemset : itemsets){
			r.append('('); // begining of an itemset
			// For each item in the current itemset
			for(Character item : itemset.getItems()){
				String string = item.toString();
				r.append(string); // append the item
				r.append(' ');
			}
			r.append(')');// end of an itemset
		}
		return r.append("    ").toString();
	}
	

}
