package dominated.seq.mining;



import java.util.HashSet;
import java.util.Set;


class Pair{
	// the item
	protected final Character item;
	// indicate if this represents the item appearing 
	// in an itemset that is cut at the left or not
	protected final boolean postfix; 
	
	// List of the sequence IDs that contains this item .
	private Set<Integer> sequencesID = new HashSet<Integer>();

	/**
	 * Constructor
	 * @param postfix indicate if this is the case of an item appearing
	 *  in an itemset that is cut at the left because of a projection
	 * @param item2 the item
	 */
	Pair(boolean postfix, Character item2){
		this.postfix = postfix;
		this.item = item2;
	}
	
	/**
	 * Check if two pairs are equal (same item and both appears in a postfix or not).
	 * @return true if equals.
	 */
	public boolean equals(Object object){
		Pair paire = (Pair) object;
		if((paire.postfix == this.postfix) 
				&& (paire.item.equals(this.item))){
			return true;
		}
		return false;
	}
	
	/**
	 * Method to calculate an hashcode (because pairs are stored in a map).
	 */
	public int hashCode()
	{// Ex: 127333,P,X,1  127333,N,Z,2
		// transform it into a string
		StringBuilder r = new StringBuilder();
		r.append((postfix ? 'P' : 'N')); // the letters here have no meanings. they are just used for the hashcode
		r.append(item);
		// then use the hashcode method from the string class
		return r.toString().hashCode();
	}
	
	/**
	 * Check if this is the case of the item appearing in a postfix
	 * @return true if this is the case.
	 */
	public boolean isPostfix() {
		return postfix;
	}

	/**
	 * Get the item represented by this pair
	 * @return the item.
	 */
	public Character getItem() {
		return item;
	}

	/**
	 * Get the support of this item (the number of sequences 
	 * containing it).
	 * @return the support (an integer)
	 */
	public int getCount() {
		return sequencesID.size();
	}		

	/**
	 * Get the list of sequence IDs associated with this item.
	 * @return  the list of sequence IDs.
	 */
	public Set<Integer> getSequenceIDs() {
		return sequencesID;
	}
}