package majorDirectionAnalyzer;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import MotionDetectionUtility.Vector;

public class MajorDirectionAnalyzerTest {

	@Test
	public void test() {
		ArrayList<Vector> vectors = new ArrayList<Vector>();
		vectors.add(new Vector(0,1));
		vectors.add(new Vector(-1,-0.5));
		vectors.add(new Vector(0,3));
		vectors.add(new Vector(1,1));
		vectors.add(new Vector(1,-1));
		vectors.add(new Vector(1,2));
		vectors.add(new Vector(-1,0.3));
		vectors.add(new Vector(3,0));
		vectors.add(new Vector(4,0));
		vectors.add(new Vector(5,0));
		vectors.add(new Vector(8,0));
		vectors.add(new Vector(2,0));

		MajorDirectionAnalyzer analyzer = new MajorDirectionAnalyzer(vectors);
		Vector majorDir = analyzer.getMajorDirection();
		System.out.println(majorDir);
		assert(true);
	}
}
