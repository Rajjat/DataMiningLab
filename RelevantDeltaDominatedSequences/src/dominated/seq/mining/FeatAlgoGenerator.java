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
 private void feat(GenesDatabase database) throws IOException{
	 generators = new ArrayList<SequentialPattern>();
	 Map<Character, Set<Integer>> sequenceID = findSequencesContainingItems(database);
	// System.out.println("map sequence ccraeted"+sequenceID);

	 dbinitial = new ArrayList<PseudoSequence>();
		// for each sequence in  the database
		for(SequenceItemset sequence : database.getSequences()){
			// remove infrequent items
		//	System.out.println("sequnce iteration"+sequence);
			SequenceItemset optimizedSequence = sequence.cloneSequenceMinusItems(sequenceID, support);
			//System.out.println("optimised sequence"+ optimizedSequence.getItemsets());
		
		
			if(optimizedSequence.size() != 0){
				// if the size is > 0, create a pseudo sequence with this sequence
				dbinitial.add(new PseudoSequence(optimizedSequence, 0,0));
			}
			//System.out.println("optimised sequence"+ optimizedSequence);
		}
		System.out.println("intial database"+ dbinitial);	
		System.out.println("----------------------------------------------------------------------------");	
	for(Entry<Character, Set<Integer>> entry : sequenceID.entrySet()){
			// if the item is frequent  (has a support >= minsup)
			if(entry.getValue().size() >= support){ 
				Character item = entry.getKey();
				SequentialPattern prefix = new SequentialPattern();  
				prefix.addItemset(new Itemsetcreate(item));
				prefix.setSequenceIDs(entry.getValue());
				//System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII");
				System.out.println("item dbintial entry.getvalue ="+item +" "+dbinitial+" "+ entry.getValue());	
				List<PseudoSequence> projectedDatabase
				  = buildProjectedDatabaseForSingleItem(item, dbinitial, entry.getValue());
				boolean canPrune = false;
				boolean isGenerator = true;
				System.out.println("dbinitial.size() == entry.getValue().size()) "+dbinitial.size() +"== "+entry.getValue().size());
				
			if(dbinitial.size() == entry.getValue().size()) {
					// check forward pruning
					canPrune = checkforwardPruningFor1ItemSequence(item, projectedDatabase);
					//System.out.println("canprune="+canPrune);
					isGenerator = false;
				}
				if(isGenerator) {
					savePattern(prefix); 
				}
				// patterns starting with this prefix
				//System.out.println("prefix is="+prefix);
				System.out.println("projected databse and item  and prefix ="+projectedDatabase +" "+ item +" "+ prefix);
				System.out.println("******************************************************************");
				if((performPruning == false || !canPrune) && maximumPatternLength >1){
					featRecursion(prefix, projectedDatabase, 2); 
				}else {
				prefixPrunedCount++;
			}

				//System.out.println("dbinitial.size() == entry.getValue().size()"+dbinitial.size()+" =="+ entry.getValue().size());
		
			}
		}
 }
		
	private Map<Character, Set<Integer>> findSequencesContainingItems(GenesDatabase database) {
		Map<Character, Set<Integer>> mapSequenceID = new HashMap<Character, Set<Integer>>(); 
		for(SequenceItemset sequence : database.getSequences()){
			for(Character item : sequence.getItemsets()){
				//for(List<Character> item : sequence){
					Set<Integer> IDs = mapSequenceID.get(item);
					//System.out.println("sequenceIDs="+IDs);
					
					if(IDs == null){
						IDs = new HashSet<Integer>();
						
						mapSequenceID.put(item, IDs);
						//System.out.println("sequenceIDs if null=="+item +"    "  +IDs);
					}
					
					IDs.add(sequence.getId());
				}
			}
		
		return mapSequenceID;
	}
	
	private List<PseudoSequence> buildProjectedDatabaseForSingleItem(Character item, List<PseudoSequence> initialDatabase,Set<Integer> sidSet) {
		// We create a new projected database
		List<PseudoSequence> sequenceDatabase = new ArrayList<PseudoSequence>();
	//	System.out.println("Inti size"+initialDatabase.size());
    // System.out.println("item:"+item);
		// for each sequence in the database received as parameter
		for(PseudoSequence sequence : initialDatabase){ 

			// if this sequence do not contain the current prefix, then skip it.
			if(!sidSet.contains(sequence.getId())){
				continue;
			}
			//System.out.println("seq size"+sequence.size());
			for(int i = 0; i< sequence.size(); i++){
				// System.out.println("intial data base in build project="+sequence.toString());
				int index = sequence.indexOfBis(i, item);
				// System.out.println(" index computed=" +index);
				if(index == -1 ){
					continue;
				}
				//System.out.println("comaprsion="+ ""+ sequence.getSizeOfItemsetAt(i));
				//if(index == sequence.getSizeOfItemsetAt(i)-1){ 
				//	if ((i != sequence.size()-1)){
					///	sequenceDatabase.add(new PseudoSequence( sequence, i+1, 0));
				///	}
				else{
					if ((i != sequence.size()-1)){
					//System.out.println("sequence added with");
					sequenceDatabase.add(new PseudoSequence(sequence, 0, i+1));
					}
				}

			}
			//System.out.println("projected datatbase again for item"+item+" "+sequenceDatabase);
		}
		//System.out.println("projected datatbase again for item"+item+" "+sequenceDatabase.toString());
	/*	for(PseudoSequence sequence : sequenceDatabase){
			// for each itemset
			System.out.println("sequence is="+sequence+"and its size is="+(sequence.size()));
			for(int i=0; i< sequence.size(); i++){
				Character item1 = sequence.getItemset(i);
				System.out.println("helo item is="+item1);
			}
			}
*/
		return sequenceDatabase; 
	}
	
	private boolean checkforwardPruningFor1ItemSequence(Character item,
			List<PseudoSequence> projectedDatabase) {
		//System.out.println("projecteddatabse---------------"+projectedDatabase);
		// There is a forward extension if the item of the prefix appeared in the first
		// position of each sequence.
		
		// for each sequence
		for(PseudoSequence seq : projectedDatabase) {
		//	System.out.println("Seq---------------"+seq);
			// we use the first item of the ORIGINAL sequence:
			Character firstItem = seq.getOriginalSequence().get(0);
         //  System.out.println("original seq and first item  and item== "+seq.getOriginalSequence()+" "+firstItem +"and"+item);
			// if not the same item
			if(!firstItem.equals(item)) {
			//	 System.out.println("can not prune");
				return false; // cannot prune
			}
		}
		return true; // can prune
	}
	private void savePattern(SequentialPattern prefix) throws IOException {
		// increase the number of pattern found for statistics purposes
		generators.add(prefix);
	}

private void featRecursion(SequentialPattern prefix, List<PseudoSequence> database, int k) throws IOException {	
	// find frequent items of size 1 in the current projected database.
	System.out.println("I am in featrecursion");

	Set<Pair> pairs = findAllFrequentPairs(database);
  // System.out.println("pairs"+pairs);
	// For each pair found (a pair is an item with a boolean indicating if it
	// appears in an itemset that is cut (a postfix) or not, and the sequence IDs
	// where it appears in the projected database).
	for(Pair pair : pairs){
		// if the item is frequent in the current projected database
		if(pair.getCount() >= support){
			// create the new postfix by appending this item to the prefix
			SequentialPattern newPrefix;
			System.out.println("pair.getitem "+pair.getItem()+" and prefix="+prefix);
			// if the item is part of a postfix
			if(pair.isPostfix()){ 
				//System.out.println("hello");
				// we append it to the last itemset of the prefix
				newPrefix = appendItemToPrefixOfSequence(prefix, pair.getItem()); 
			}else{
				//System.out.println("Yello");// else, we append it as a new itemset to the sequence
				newPrefix = appendItemToSequence(prefix, pair.getItem());
			}
			newPrefix.setSequenceIDs(pair.getSequenceIDs()); 
			PairSequences projectedDatabase = buildProjectedDatabase(pair.getItem(), database, pair.getSequenceIDs(), pair.isPostfix());
          System.out.println("newprefix="+newPrefix.toString());
			boolean canPrune = false;
			boolean isGenerator = true;
			
			if(prefix.getAbsoluteSupport() == pair.getSequenceIDs().size()) {
				// check forward pruning
				canPrune = checkForwardPruningGeneralCase(projectedDatabase, pair.getItem(), pair.isPostfix());
				isGenerator = false;
//				System.out.println(prefix);
			}	
			
			if(!canPrune) { 
//				System.out.println(newPrefix);
				Boolean[] returnValues = checkBackwardPruning(newPrefix, projectedDatabase.newSequences, isGenerator);
				isGenerator = returnValues[0];
				canPrune = returnValues[1];
			}
			
			//if(isGenerator && newPrefix.getItemsets().size() <= 6) {
			if(isGenerator){
				System.out.println("^^^^^^^^^^^^^^^^^^isGenertor^^^^^^^^^="+newPrefix);
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
	// check the current memory usage
	//MemoryLogger.getInstance().checkMemory();
}
protected Set<Pair> findAllFrequentPairs(List<PseudoSequence> sequences){
	// We use a Map the store the pairs.
	Map<Pair, Pair> mapPairs = new HashMap<Pair, Pair>();
	System.out.println("sprojected databse came  ="+ sequences +"and its size is= " +sequences.size());
	// for each sequence
	//System.out.println("deqprojection  is"+sequences);
	//System.out.println("sprojected databse came  ="+ sequences +"and its size is= " +sequences.size());
	for(PseudoSequence sequence : sequences){
		// for each itemset
	//	System.out.println("sequence is="+sequence+"and its size is="+(sequence.size()));
		System.out.println("sequence is="+sequence+"and its size is="+sequence.size());
		for(int i=0; i< sequence.size(); i++){
			// for each item
		//	for(int j=0; j < sequence.getSizeOfItemsetAt(i); j++){
				Character item = sequence.getItemset(i);
		//		System.out.println("item is"+item);
				// create the pair corresponding to this item
				Pair pair = new Pair(sequence.isPostfix(i), item);   
				// get the pair object store in the map if there is one already
				Pair oldPair = mapPairs.get(pair);
				// if there is no pair object yet
				if(oldPair == null){
					// store the pair object that we created
					mapPairs.put(pair, pair);
				//	System.out.println("put my map="+mapPairs.get(pair).toString().charAt(0));
					
				}else{
					// otherwise use the old one
					pair = oldPair;
				}
				// record the current sequence id for that pair
				pair.getSequenceIDs().add(sequence.getId());
			}
		}
	
	//MemoryLogger.getInstance().checkMemory();  // check the memory for statistics.
	// return the map of pairs
	//System.out.println("map pairs"+mapPairs.keySet().size());
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
//System.out.println("in buildProjectedDatabase item is ="+item+" dtabase is="+database+" sidset="+sidset+" inpostfix="+inPostFix);
	//System.out.println("in buildProjectedDatabase----------------------------llll----");
	// for each sequence in the database received as parameter
	for(PseudoSequence sequence : database){ 
		//System.out.println("sequence inside projected databse="+sequence);
		if(sidset.contains(sequence.getId()) == false){
			continue;
		}
		
		// for each itemset of the sequence
		for(int i = 0; i< sequence.size(); i++){
             
			if (sequence.isPostfix(i) != inPostFix){
				// if the item is not in a postfix, but this itemset
				// is a postfix, then we can continue scanning from the next itemset
				continue;
			}

			// check if the itemset contains the item that we use for the projection
			int index = sequence.indexOfBis(i, item);
			//System.out.println("index is "+index);
			// if it does not, move to next itemset
			if(index == -1 ){
				continue;
			}
			//System.out.println("comaprsion="+ ""+ sequence.getSizeOfItemsetAt(i));
			//if(index == sequence.getSizeOfItemsetAt(i)-1){ 
			//	if ((i != sequence.size()-1)){
				///	sequenceDatabase.add(new PseudoSequence( sequence, i+1, 0));
			///	}
		
				if ((i != sequence.size()-1)){
				//System.out.println("sequence added with sequence"+sequence+" and index i="+i);;
				//sequenceDatabase.add(new PseudoSequence(sequence, 0, i+1));
					sequenceDatabase.newSequences.add(new PseudoSequence( sequence, 0, i+1));
					sequenceDatabase.olderSequences.add(sequence);
				}
			

		}
	}//System.out.println("projected datatbase again for item"+item+" "+sequenceDatabase);
	
	//System.out.println("projected datatbase again for item"+item+" "+sequenceDatabase.newSequences);
			
			// if the item is the last item of this itemset
			/*if(index == sequence.getSizeOfItemsetAt(i)-1){ 
				// if it is not the last itemset
				if ((i != sequence.size()-1)){
					// create new pseudo sequence
					// add it to the projected database.
//					PairSequences pair = new PairSequences();
					sequenceDatabase.newSequences.add(new PseudoSequence( sequence, i+1, 0));
					sequenceDatabase.olderSequences.add(sequence);
//					sequenceDatabase.add(pair);
					//System.out.println(sequence.getId() + "--> "+ newSequence.toString());
//					break itemsetLoop;
				}
			}else{
				// create a new pseudo sequence and
				// add it to the projected database.
//				PairSequences pair = new PairSequences();
				sequenceDatabase.newSequences.add(new PseudoSequence(sequence, i, index+1));
				sequenceDatabase.olderSequences.add(sequence);
//				sequenceDatabase.add(pair);
				//System.out.println(sequence.getId() + "--> "+ newSequence.toString());
//				break itemsetLoop;
			}*/
		
	
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
	System.out.println("newPrefix is" +newPrefix+" and itrs projectio is " +"projecteddatabse came**************** "+projectedDatabase+"isgenertor=="+isGeneratorParameter);
	// initialize variables for returning values
	boolean isGenerator = isGeneratorParameter;
	boolean canPrune = false;
	
	// calculate the size of this prefix
	int prefixTotalSize = newPrefix.getItemOccurencesTotalCount();
System.out.println("prefixTotalSize="+prefixTotalSize);
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
				System.out.println("original seq sent="+originalSequence+"prefix truncaated="+prefixTruncated);
				ProjectionEnum result = sameProjection(originalSequence, newItemset, i);
				
				// if the sequence contain the prefix without i, we increase its support
				if(result.equals(ProjectionEnum.SAME_PROJECTION) ||
						result.equals(ProjectionEnum.CONTAIN_PREFIX_WITHOUT_I)) {
					System.out.println("SAME_PROJECTION||CONTAIN_PREFIX_WITHOUT_I");
					System.out.println("original seq sent="+originalSequence+"prefix truncaated="+prefixTruncated);
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
				System.out.println("%%%%%%%%");
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
	System.out.println("prefix="+newItemset+"and i="+i+ "and original seq is="+originalSequence);
	int projectionWithoutI = -1;
	//int itemsetPos = 0;
	int itemsetPos = 1;
	// determine item I or null if not in this itemset
	Character itemI = null;
	/*if(i < newItemset.get(itemsetPos).size()) {
		System.out.println("prefix.get(itemsetPos).size()="+newItemset.get(itemsetPos).size());
		// if there is only a single item, we just move to the next itemset
		if(newItemset.get(itemsetPos).size()==1) {
			itemsetPos++;
			i--;
	}
	}*/
	System.out.println("itempos="+itemsetPos);
	// for each itemset of the sequence
	for(int k = 0; k < originalSequence.size(); k++) {
		Character itemsetSequence = originalSequence.getItemset(k);	
		System.out.println("itemsetSequence= "+itemsetSequence+" originalSequence.size()="+ originalSequence.size());
		// check containment
		boolean contained = true;
		Character item =newItemset.get(itemsetPos);
			if(item != itemI && itemsetSequence!=item) {
				System.out.println("item=iiiiiiiiiiii"+item);
				contained = false;
			
			}
		
		
		if(contained) {
			//i-= newItemset.get(itemsetPos).size();
			System.out.println("contained="+i);
			// move to next itemset
			itemsetPos++;
			
			if(itemsetPos == newItemset.size()) {
				projectionWithoutI = k;
				System.out.println("projectionWithoutI="+projectionWithoutI);
				break;	
			}
			
			// update item I
			itemI = null;
			/*if(i < newItemset.get(itemsetPos).size() && i >=0) {
				// if there is only a single item, we just move to the next itemset
				if(newItemset.get(itemsetPos).size()==1) {
					itemsetPos++;
					i--;
				}else {*/
					// otherwise, there is more than one item so we set item I correctly
				//	itemI = newItemset.get(i);
				
			}
		}
	
	
	// CALCULATE THE PROJECTION WITH I
	int projectionWithI = -1;
	itemsetPos = 0;
	
	// for each itemset of the sequence
	for(int k = 0; k < originalSequence.size(); k++) {
		Character itemsetSequence = originalSequence.getItemset(k);	
		System.out.println("itemsetSequence="+itemsetSequence);
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
				System.out.println("itemsetPos == prefix.size()");
				projectionWithI = k;
				System.out.println("projectionWithI ="+projectionWithI);
				break;	
			}
		}
	}
	
	// if same projection
	if(projectionWithI == projectionWithoutI) {
		if(projectionWithI > 0) {
			System.out.println("ProjectionEnum.SAME_PROJECTION");
			return ProjectionEnum.SAME_PROJECTION;
		}else {
			System.out.println("ProjectionEnum.SAME_PROJECTION_NOT_CONTAINED_IN");
			return ProjectionEnum.SAME_PROJECTION_NOT_CONTAINED_IN;
		}
	}

	// if the prefix without i occurred in this sequence
	if(projectionWithoutI >=0) {
		System.out.println("ProjectionEnum.CONTAIN_PREFIX_WITHOUT_I");
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
				//count++;
				//if(count>=3)
				//	break;
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
//		System.out.println(buffer);
	}
	
	writer.close();
}

}





	
	



	
	
	