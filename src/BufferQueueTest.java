import static org.junit.Assert.*;

import org.junit.Test;

import DataStructure.BufferQueue;


public class BufferQueueTest {
	
	@Test
	public void test() {
		BufferQueue<Integer> q = new BufferQueue<Integer>(2);
		q.add(2);
		q.add(3);
		q.add(4);
		assert(q.size() == 2);
		int n = q.getLatest();
		assert(n==4);
		assert(q.size() == 2);
		n = q.getLatest();
		assert(n==4);
		assert(q.size() == 2);
		q.add(5);
		n = q.getSecondLatest();
		assert(n==4);
		assert(q.size() == 2);
	}
}
