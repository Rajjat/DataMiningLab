package dominated.seq.mining;

import java.util.List;


import dominated.seq.mining.*;

public class PseudoSequence1 {
	
		protected SequenceItemset sequence;
		protected int firstItemset;
		protected int firstItem;
		
		protected PseudoSequence1(){
			
		}
		public SequenceItemset getOriginalSequence() {
			return sequence;
		}

		protected PseudoSequence1(PseudoSequence sequence, int indexItemset, int indexItem){
			this.sequence = sequence.sequence;
			this.firstItemset = indexItemset + sequence.firstItemset;
			if(this.firstItemset == sequence.firstItemset){
				this.firstItem = indexItem + sequence.firstItem;
			}else{
				this.firstItem = indexItem; 
			}
		}
		protected  PseudoSequence1(SequenceItemset sequence, int indexItemset, int indexItem){
			this.sequence = sequence;
			this.firstItemset = indexItemset;
			this.firstItem = indexItem;
		}


		protected int size() {
			int size = sequence.size() - firstItemset;
			return size;
		}
		public int getSizeOfItemsetAt(int index) {
			
			int size = sequence.getItemsets().size();
			if(isFirstItemset(index)){
				size -=  firstItem;
			}
			return size; // return the size
		}

		protected boolean isPostfix(int indexItemset) {
			return indexItemset == 0  && firstItem !=0;
		}

		protected boolean isFirstItemset(int index) {
			return index == 0;
		}
		protected boolean isLastItemset(int index) {
			return (index + firstItemset) == sequence.getItemsets().size() -1;
		}

		public Character getItemAtInItemsetAt(int indexItem) {
			// if it is in the first itemset
			if(isFirstItemset(indexItem)){
				// we need to consider if the itemset was cut at the left
				// by adding "firstItem"
				return sequence.get(indexItem + firstItem);
			}else{// otherwise
				return sequence.get(indexItem);
			}
		}

		public Character getItemset(int index) {
			return sequence.get(index+firstItemset);
		
		}

		protected int getId() {
			return sequence.getId();
		}
	
		public void print() {
			System.out.print(toString());
		}

		public String toString() {
			StringBuilder r = new StringBuilder();
			
			for(int i=0; i < size(); i++){
				r.append(getItemset(i));
					if(isPostfix(i)){
						r.append(' ');
					}

				if(!isLastItemset(i) ){
						r.append(' ');
				}
				System.out.println("Stringbuikder"+r.toString());
				}
			return r.toString();
		}


		protected int indexOfBis(int indexItemset, Character item) {
				if(getItemAtInItemsetAt(indexItemset).equals(item)){
					return indexItemset; // if equal, return the current position
				}else if(!getItemAtInItemsetAt( indexItemset).equals(item))
					{
					return -1;
			}
				return -1;
		}
			
		
}

