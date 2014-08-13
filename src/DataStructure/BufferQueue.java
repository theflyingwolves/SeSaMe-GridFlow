package DataStructure;

import java.util.ArrayList;

public class BufferQueue<elemT> {
	private ArrayList<elemT> store;
	private int maxBufferSize;
	
	public BufferQueue(int maxSize){
		maxBufferSize = maxSize;
		store = new ArrayList<elemT>();
	}
	
	public boolean add(elemT e){
//		if(store.size() >= maxBufferSize){
//			removeOldest();
//		}
//		while(store.size() >= maxBufferSize);
//		store.add(e);
		if(store.size() >= maxBufferSize){
//			System.out.println("Adding Failed");
			return false;
		}else{
			store.add(e);
			return true;
		}
	}
	
	public elemT getLatest(){
		if(store.size() > 0){
			int index = store.size()-1;
			elemT latestElemt = store.get(index);
			return latestElemt;
		}else{
			return null;
		}
	}
	
	public void removeOldest(){
		if(store.size() >= maxBufferSize){
			int oldestIndex = 0;
			store.remove(oldestIndex);
		}
	}
	
	public elemT getSecondLatest(){
		if(store.size() >= 2){
			return store.get(store.size()-2);
		}else{
			return null;
		}
	}
	
//	public elemT getAndRemoveOldest(){
//		elemT oldestElemt = getOldest();
//		if(oldestElemt != null){
//			store.remove(0);
//		}
//		return oldestElemt;
//	}
//	
//	public elemT getOldest(){
//		if(store.size()>0){
//			elemT oldestElemt = store.get(0);
//			return oldestElemt;
//		}else{
//			return null;
//		}
//	}
	
	public int size(){
		return store.size();
	}
}
