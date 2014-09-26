package videoProcessorSting;

import MotionDetectionUtility.Vector;
import sting.Combinable;

public class Direction extends Combinable{
	
	private Vector direction;
	
	public Direction(Vector v){
		this.direction = v;
	}
	
	public Vector getDirectionVector(){
		return this.direction;
	}

	@Override
	public Combinable combineWith(Combinable obj) {
		if(obj instanceof Direction){
			Direction dir = (Direction)obj;
			Vector v = dir.getDirectionVector();
			return new Direction(this.direction.add(v));
		}else{
			return null;
		}
	}
	
//	@Override
//	public Combinable combineWith(Combinable[] obj){
//		if()
//	}

	@Override
	public boolean shouldPropagate(Combinable[] objs) {
		// TODO
		return true;
//		for(Combinable obj : objs){
//			if(obj instanceof Direction){
//				
//			}else{
//				return false;
//			}
//		}
	}
}
