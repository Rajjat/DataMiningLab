package dominated.seq.mining;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;



import dominated.seq.mining.*;


public class FeatAlgoGenerator {
	
	List<PseudoSequence> dbinitial = null;
	public int support;
	boolean performPruning = true;
	private int maximumPatternLength = Integer.MAX_VALUE;
	private List<SequentialPattern> generators = null;
	public int prefixPrunedCount = 0;
	boolean showSequenceIdentifiers = false;
 public FeatAlgoGenerator()
 {

 }
 public List<SequentialPattern> runAlgorithm(GenesDatabase db,int minsup) throws IOException{
	this.support=minsup;
	feat(db);
	 return null;

 }
 private void feat(GenesDatabase database) throws IOException
 {
	 generators = new ArrayList<SequentialPattern>();
	 Map<Character, Set<Integer>> sequenceID = findSequencesContainingItems(database);
	 dbinitial = new ArrayList<PseudoSequence>();
		// for each sequence in  the database
		for(SequenceItemset sequence : database.getSequences()){
			// remove infrequent items
			SequenceItemset optimizedSequence = sequence.cloneSequenceMinusItems(sequenceID, support);
			if(optimizedSequence.size() != 0){
				// if the size is > 0, create a pseudo sequence with this sequence
				dbinitial.add(new PseudoSequence(optimizedSequence, 0,0));
			}
		}	
	  for(Entry<Character, Set<Integer>> entry : sequenceID.entrySet())
	  {
			// if the item is frequent  (has a support >= minsup)
			if(entry.getValue().size() >= support)
			{ 
				Character item = entry.getKey();
				SequentialPattern prefix = new SequentialPattern();  
				prefix.addItemset(new Itemsetcreate(item));
				prefix.setSequenceIDs(entry.getValue());	
				List<PseudoSequence> projectedDatabase
				  = buildProjectedDatabaseForSingleItem(item, dbinitial, entry.getValue());
				boolean canPrune = false;
				boolean isGenerator = true;				
			        if(dbinitial.size() == entry.getValue().size()) 
				{
					canPrune = checkforwardPruningFor1ItemSequence(item, projectedDatabase);
					isGenerator = false;
				}
				if(isGenerator) 
				{
					savePattern(prefix); 
				}
				if((performPruning == false || !canPrune) && maximumPatternLength >1)
				{
					featRecursion(prefix, projectedDatabase, 2); 
				}else {
				prefixPrunedCount++;
			        }
		      }
	}
 }
		
private Map<Character, Set<Integer>> findSequencesContainingItems(GenesDatabase database) 
{
		Map<Character, Set<Integer>> mapSequenceID = new HashMap<Character, Set<Integer>>(); 
		for(SequenceItemset sequence : database.getSequences()){
			for(Character item : sequence.getItemsets()){
					Set<Integer> IDs = mapSequenceID.get(item);
					if(IDs == null){
						IDs = new HashSet<Integer>();						
						mapSequenceID.put(item, IDs);
					}
					IDs.add(sequence.getId());
				}
			}
		
		return mapSequenceID;
}
	
private List<PseudoSequence> buildProjectedDatabaseForSingleItem(Character item, List<PseudoSequence> initialDatabase,Set<Integer> sidSet) {
		
     List<PseudoSequence> sequenceDatabase = new ArrayList<PseudoSequence>();
		// for each sequence in the database received as parameter
		for(PseudoSequence sequence : initialDatabase){ 

			// if this sequence do not contain the current prefix, then skip it.
			if(!sidSet.contains(sequence.getId())){
				continue;
			}
			for(int i = 0; i< sequence.size(); i++){
				int index = sequence.indexOfBis(i, item);
				if(index == -1 ){
					continue;
				}
				else{
					if ((i != sequence.size()-1)){
					sequenceDatabase.add(new PseudoSequence(sequence, 0, i+1));
					}
				}

			}
		}
		return sequenceDatabase; 
  }
	
private boolean checkforwardPruningFor1ItemSequence(Character item,
			List<PseudoSequence> projectedDatabase) 
{
       for(PseudoSequence seq : projectedDatabase) {
		
	// we use the first item of the ORIGINAL sequence:
	Character firstItem = seq.getOriginalSequence().get(0);
	if(!firstItem.equals(item)) {
				return false; // cannot prune
			}
		}
		return true; // can prune
	}
	private void savePattern(SequentialPattern prefix) throws IOException {
		generators.add(prefix);
	}

private void featRecursion(SequentialPattern prefix, List<PseudoSequence> database, int k) throws IOException {	
	// find frequent items of size 1 in the current projected database.

	Set<Pair> pairs = findAllFrequentPairs(database);
 	for(Pair pair : pairs){
		// if the item is frequent in the current projected database
		if(pair.getCount() >= support){
			// create the new postfix by appending this item to the prefix
			SequentialPattern newPrefix;
			// if the item is part of a postfix
			if(pair.isPostfix()){ 
				// we append it to the last itemset of the prefix
				newPrefix = appendItemToPrefixOfSequence(prefix, pair.getItem()); 
			}else{
                         // else, we append it as a new itemset to the sequence
				newPrefix = appendItemToSequence(prefix, pair.getItem());
			}
			newPrefix.setSequenceIDs(pair.getSequenceIDs()); 
			PairSequences projectedDatabase = buildProjectedDatabase(pair.getItem(), database, pair.getSequenceIDs(), pair.isPostfix());
			boolean canPrune = false;
			boolean isGenerator = true;
			
			if(prefix.getAbsoluteSupport() == pair.getSequenceIDs().size()) {
				// check forward pruning
				canPrune = checkForwardPruningGeneralCase(projectedDatabase, pair.getItem(), pair.isPostfix());
				isGenerator = false;
			}	
			
			if(!canPrune) { 
				Boolean[] returnValues = checkBackwardPruning(newPrefix, projectedDatabase.newSequences, isGenerator);
				isGenerator = returnValues[0];
				canPrune = returnValues[1];
			}
			
			if(isGenerator){
			savePattern(newPrefix); 
			}
			
			if((performPruning == false || !canPrune)  && k < maximumPatternLength){
				featRecursion(newPrefix, projectedDatabase.newSequences, k+1);
			}else {
				prefixPrunedCount++;
			}

			// ================ END OF SPECIFIC TO FEAT ================
			
		}
	}
}
protected Set<Pair> findAllFrequentPairs(List<PseudoSequence> sequences){
	// We use a Map the store the pairs.
	Map<Pair, Pair> mapPairs = new HashMap<Pair, Pair>();
	for(PseudoSequence sequence : sequences){
		for(int i=0; i< sequence.size(); i++){
				Character item = sequence.getItemset(i);
				Pair pair = new Pair(sequence.isPostfix(i), item);   
				// get the pair object store in the map if there is one already
				Pair oldPair = mapPairs.get(pair);
				// if there is no pair object yet
				if(oldPair == null){
					// store the pair object that we created
					mapPairs.put(pair, pair);
				}else{
					// otherwise use the old one
					pair = oldPair;
				}
				// record the current sequence id for that pair
				pair.getSequenceIDs().add(sequence.getId());
			}
		}
		return mapPairs.keySet();
}
private SequentialPattern appendItemToPrefixOfSequence(SequentialPattern prefix, Character item) {
	SequentialPattern newPrefix = prefix.cloneSequence();
	Itemsetcreate itemset = newPrefix.get(newPrefix.size()-1);  // add to the last itemset
	itemset.addItem(item);  
	return newPrefix;
}
private SequentialPattern appendItemToSequence(SequentialPattern prefix, Character item) {
	SequentialPattern newPrefix = prefix.cloneSequence();  // isSuffix
	newPrefix.addItemset(new Itemsetcreate(item));  
	return newPrefix;
}
private class PairSequences{
	List<PseudoSequence> olderSequences = new ArrayList<PseudoSequence>();
	List<PseudoSequence> newSequences = new ArrayList<PseudoSequence>();
}	
private PairSequences buildProjectedDatabase(Character item, List<PseudoSequence> database, Set<Integer> sidset, boolean inPostFix) {
	// We create a new projected database
	PairSequences sequenceDatabase = new PairSequences();
	for(PseudoSequence sequence : database){ 
		if(sidset.contains(sequence.getId()) == false){
			continue;
		}
		
		for(int i = 0; i< sequence.size(); i++){
             
			if (sequence.isPostfix(i) != inPostFix){
				// if the item is not in a postfix, but this itemset
				// is a postfix, then we can continue scanning from the next itemset
				continue;
			}

			// check if the itemset contains the item that we use for the projection
			int index = sequence.indexOfBis(i, item);
			// if it does not, move to next itemset
			if(index == -1 ){
				continue;
			}
				if ((i != sequence.size()-1)){
					sequenceDatabase.newSequences.add(new PseudoSequence( sequence, 0, i+1));
					sequenceDatabase.olderSequences.add(sequence);
				}
			

		}
	}
	return sequenceDatabase; // return the projected database
}
private boolean checkForwardPruningGeneralCase(PairSequences projectedDatabase, Character item, boolean postfix) {
	
	// for each sequence
	for(int i=0; i< projectedDatabase.newSequences.size(); i++) {
		PseudoSequence seq = projectedDatabase.newSequences.get(i);
		PseudoSequence seqProjected = projectedDatabase.olderSequences.get(i);
		
		// we need to check if there is exactly one less item in the
		// sequence projected by new prefix.
		// in this case it is a forward extension.
		
		// calculate the position of the next item following the sequence
		// projected by prefix.
		Character firstItem = seq.getItemAtInItemsetAt(0);
		
		// if the first item is not the one used to extend the prefix,
		// then there is an item between so this is not a forward extension
		if(!firstItem.equals(item)) {
			return false; // cannot prune
		}
		
		// calculate what is the next item position following the projection
		// by prefix in the original sequence
		int itemPos = seq.firstItem+1;
		int itemsetPos = seq.firstItemset;
		if(seq.getSizeOfItemsetAt(0) == itemPos) {
			itemPos = 0;
			itemsetPos++;
		}
		// if this position is different than the position projected by new prefix
		// than it is not a forward extension
		if(seqProjected.firstItem != itemPos ||
		    seqProjected.firstItemset != itemsetPos) {
			return false;
		}
	}
	return true; //  can prune
}
private Boolean[] checkBackwardPruning(SequentialPattern newPrefix,
		List<PseudoSequence> projectedDatabase, boolean isGeneratorParameter) {
	// initialize variables for returning values
	boolean isGenerator = isGeneratorParameter;
	boolean canPrune = false;
	
	// calculate the size of this prefix
	int prefixTotalSize = newPrefix.getItemOccurencesTotalCount();
	// for each item j 
      loop:	for(int j = 1; j < prefixTotalSize; j++) {

		// Create the truncated prefix
		List<List<Character>> prefixTruncated = new ArrayList<List<Character>>();
		List<Character> newItemset = new ArrayList<Character>();
		int itemCounter = j;
		loop1:	for(Itemsetcreate itemsetPrefix : newPrefix.getItemsets()) {
			//	List<Character> newItemset = new ArrayList<Character>();
				prefixTruncated.add(newItemset);
				for(Character currentItem : itemsetPrefix.getItems()) {
					newItemset.add(currentItem);
					itemCounter--;
					if(itemCounter < 0) {
						break loop1;
					}
				}
				
		}
		// for the i-th item of the newPrefix such that i is before j
		for(int i=0; i < j; i++) {		

			// variable to count the support of the prefix without i
			int supCount = 0;
			// variable to check if we should prune according to this prefix without i
			boolean localCanPrune = true;
			
			// variable to count the number of sequences remaining to be checked
			int seqRemaining = dbinitial.size();
					
			// for each sequence of the original database
			for(PseudoSequence originalSequence: dbinitial) {
				//  decrease the count of sequences remaining
				seqRemaining--;
				
				// we check if the prefix and the prefix without i have
				// the same projection or not
				ProjectionEnum result = sameProjection(originalSequence, newItemset, i);
				
				// if the sequence contain the prefix without i, we increase its support
				if(result.equals(ProjectionEnum.SAME_PROJECTION) ||
						result.equals(ProjectionEnum.CONTAIN_PREFIX_WITHOUT_I)) {
					supCount++;
				}
				
				// if not the same projection, then we cannot prune
				if(!result.equals(ProjectionEnum.SAME_PROJECTION)
						&& !result.equals(ProjectionEnum.SAME_PROJECTION_NOT_CONTAINED_IN)) {
					// so we note that.
					localCanPrune = false;
					// Then, if we know that the prefix is not a generator, then we don't
					// need to count the support of prefix without i, so we break
					if(isGenerator == false) {
						break;
					}else if(supCount + seqRemaining < newPrefix.getAbsoluteSupport()){
						// this means that the support of prefix without i cannot
						// be the same as prefix
						break;
					}
				}
				
			}
			// if the projections are the same for all sequences
			if(localCanPrune == true) {
				canPrune = true;
				// if we have established that we can prune and this is not a generator, we
				// don't need to continue this loop
				if(canPrune == true && isGenerator == false) {
					break loop;
				}
			}
			if(supCount == newPrefix.getAbsoluteSupport()) {
				isGenerator = false;
			}
			
		}
	}
	
	
	// return values
	Boolean returnValues[] = new Boolean[2];
	returnValues[0] = isGenerator;
	returnValues[1] = canPrune;
	return returnValues;
}
public enum ProjectionEnum {
    SAME_PROJECTION, SAME_PROJECTION_NOT_CONTAINED_IN, CONTAIN_PREFIX_WITHOUT_I
}
private ProjectionEnum sameProjection(PseudoSequence originalSequence,
		List<Character> newItemset, int i) {
	
	// CALCULATE THE PROJECTION WITHOUT I
	int projectionWithoutI = -1;
	//int itemsetPos = 0;
	int itemsetPos = 1;
	// determine item I or null if not in this itemset
	Character itemI = null;
	// for each itemset of the sequence
	for(int k = 0; k < originalSequence.size(); k++) {
		Character itemsetSequence = originalSequence.getItemset(k);	
		// check containment
		boolean contained = true;
		Character item =newItemset.get(itemsetPos);
			if(item != itemI && itemsetSequence!=item) {
				contained = false;
			
			}
		
		
		if(contained) {
			//i-= newItemset.get(itemsetPos).size();
			// move to next itemset
			itemsetPos++;
			
			if(itemsetPos == newItemset.size()) {
				projectionWithoutI = k;
				break;	
			}
			
			// update item I
			itemI = null;
			}
		}
	
	
	// CALCULATE THE PROJECTION WITH I
	int projectionWithI = -1;
	itemsetPos = 0;
	
	// for each itemset of the sequence
	for(int k = 0; k < originalSequence.size(); k++) {
		Character itemsetSequence = originalSequence.getItemset(k);
		// check containment
		boolean contained = true;
		Character item =newItemset.get(itemsetPos);
			if(itemsetSequence!=item) {
				contained = false;
			
			}
		
		
		if(contained) {
			// move to next itemset
			itemsetPos++;
			
			if(itemsetPos == newItemset.size()) {
				projectionWithI = k;
				break;	
			}
		}
	}
	
	// if same projection
	if(projectionWithI == projectionWithoutI) {
		if(projectionWithI > 0) {
			return ProjectionEnum.SAME_PROJECTION;
		}else {
			return ProjectionEnum.SAME_PROJECTION_NOT_CONTAINED_IN;
		}
	}

	// if the prefix without i occurred in this sequence
	if(projectionWithoutI >=0) {
		return ProjectionEnum.CONTAIN_PREFIX_WITHOUT_I;
	}
	// should not occur
	return null;
  }
public void writeResultTofile(String path) throws IOException {
	BufferedWriter writer = new BufferedWriter(new FileWriter(path)); 
	Iterator<SequentialPattern> iter = generators.iterator();
	int count=0;
	while (iter.hasNext()) {
		SequentialPattern pattern = (SequentialPattern) iter.next();
		StringBuilder buffer = new StringBuilder();
		// for each itemset in this sequential pattern
		for(Itemsetcreate itemset : pattern.getItemsets()){
			// for each item
			for(Character item : itemset.getItems()){
				buffer.append(item.toString()); // add the item
				buffer.append(' ');
				
			}
			buffer.append(""); // add the itemset separator
		}		
		// write separator
		buffer.append(" #SUP: ");
		// write support
		buffer.append(pattern.getAbsoluteSupport());
		// write sequence identifiers
		if(showSequenceIdentifiers) {
			buffer.append(" #SID: ");
        	for (Integer sid: pattern.getSequenceIDs()) {
        		buffer.append(sid);
        		buffer.append(" ");
        	}
		}
		count=0;
		
		writer.write(buffer.toString());
		writer.newLine();
	}
	
	writer.close();
}

}





	
	



	
	
	
