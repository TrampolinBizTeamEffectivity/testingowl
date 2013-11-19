package ch.sebastianfiechter.testingowl

class FrameIndexEntry implements Serializable {

	long frameTime
	long streamPosition
	int lastFullFrame 
	
	public FrameIndexEntry(time, position, lastFull) {
		frameTime = time;
		streamPosition = position;
		lastFullFrame = lastFull;
	}
	
}
