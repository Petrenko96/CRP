package messages;

public abstract class CRPMessage implements Comparable<CRPMessage> {
	
	private int delay;
	private int source;
	private int destination;
	
	public CRPMessage(int source, int destination, int delay) {
		this.delay = delay;
		this.source = source;
		this.destination = destination;
	}
	
	public void increaseDelay(int delay) {
		this.delay += delay;
	}
	
	public int delay() {
		return delay;
	}
	
	public int source() {
		return source;
	}
	
	public int destination() {
		return destination;
	}
	
	@Override
	public int compareTo(CRPMessage o) {
		return Integer.compare(this.delay, o.delay);
	}
}
