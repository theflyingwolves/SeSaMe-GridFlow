package majorDirectionAnalyzer;

import static org.junit.Assert.*;

import org.junit.Test;

import MotionDetectionUtility.Vector;

public class VectorComparatorTest {
	
	@Test
	public void test() {
		VectorComparator comparator = new VectorComparator(new Vector(1,0));
		Vector v1 = new Vector(1,0);
		Vector v2 = new Vector(0,-1);
		Vector v3 = new Vector(1,1);
		Vector v4 = new Vector(-1,-1);
		int res12 = comparator.compare(v1, v2);
		assert(res12>0);
		int res23 = comparator.compare(v2, v3);
		assert(res23>0);
		int res34 = comparator.compare(v3, v4);
		assert(res34 == 0);
	}
	
}
