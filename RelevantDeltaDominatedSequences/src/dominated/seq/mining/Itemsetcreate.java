package  dominated.seq.mining;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;



import  dominated.seq.mining.*;

public class Itemsetcreate {
		private final List<Character> items = new ArrayList<Character>(); 
		public Itemsetcreate(Character item){
			addItem(item);
		}
		public Itemsetcreate(){
		}

		public void addItem(Character item){
				items.add(item);
		}
		
		public List<Character> getItems(){
			return items;
		}
		
		public Character get(int index){
			return items.get(index);
		}
		public String toString(){
			StringBuilder r = new StringBuilder ();
			for(Character item : items){
				r.append(item.toString());
				r.append(' ');
			}
			return r.toString();
		}
		
			public int size(){
			return items.size();
		}
		public Itemsetcreate cloneItemSet(){
			Itemsetcreate itemset = new Itemsetcreate();
			itemset.getItems().addAll(items);
			return itemset;
		}
		

}

