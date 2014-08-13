package sting;

public class StingTestFake {
	public static class Significance extends Combinable{
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
				return "true";
			}else{
				return "false";
			}
		}
	}
	
	public static void main(String[] args){
		Cell[][] cells = new Cell[8][8];
		int counter = 0;
		for(int i=0; i<cells.length;i++){
			for(int j=0; j<cells[0].length; j++){
				boolean value = true;
				if(j==1){
					value = false;
				}
				
				Significance sig = new Significance(value);
				cells[i][j] = new Cell(sig);
				counter++;
			}
		}
		
		System.out.println("Counter: "+counter);
		
		Sting sting = new Sting(cells,2);
		
		printStingInfo(sting);
		sting.execute();
		
		printStingInfo(sting);
		sting.execute();
		
		printStingInfo(sting);
		sting.execute();
		
		printStingInfo(sting);
		sting.execute();
	}
	
	private static void printStingInfo(Sting sting){
		Cell[] activeCells = sting.getActiveCells();
		System.out.println("Number of Cells Left: "+activeCells.length);
		for(Cell c : activeCells){
			System.out.println("Cell Content: "+c);
		}
	}
}
