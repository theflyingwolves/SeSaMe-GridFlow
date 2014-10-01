package videoProcessorSting;

import sting.Combinable;

public class Significance extends Combinable{
	private boolean significance;
	
	public Significance(boolean isSignificant){
		this.significance = isSignificant;
	}
	
	public boolean getSignificance(){
		return this.significance;
	}
	
	@Override
	public Combinable combineWith(Combinable obj) {
		if(obj instanceof Significance){
			Significance s = (Significance)obj;
			Significance newS= new Significance(this.significance && s.getSignificance());
			return newS;
		}else{
			return null;
		}
	}
	
	@Override
	public Combinable combineWith(Combinable[] obj){
		int count = 0;
		for(Combinable c : obj){
			if(c instanceof Significance){
				Significance s = (Significance)c;
				if(s.getSignificance()){
					count++;
				}
			}
		}
		
		if(count >= 0.5*obj.length){
			return new Significance(true);
		}else{
			return new Significance(false);
		}
	}

	@Override
	public boolean shouldPropagate(Combinable[] objs) {
		return true;
	}
	
	public String toString(){
		if(significance){
			return "T";
		}else{
			return "F";
		}
	}
}
