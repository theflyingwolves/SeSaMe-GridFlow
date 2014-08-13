package sting;

public abstract class Combinable {
 	public abstract Combinable combineWith(Combinable obj);
 	public abstract boolean shouldPropagate(Combinable[] objs);
 	public Combinable combineWith(Combinable[] obj){
 		Combinable all = null;
 		for(Combinable c : obj){
 			if(all == null){
 				all = c;
 			}else{
 				all = all.combineWith(c);
 			}
 		}
 		return all;
 	}
}
