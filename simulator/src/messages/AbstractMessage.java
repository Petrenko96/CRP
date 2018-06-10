package messages;

import routing.Prefix;
import routing.Route;
import routing.Route.Priority;

public abstract class AbstractMessage implements Comparable<AbstractMessage> {

	private int borderRouter;
	private int source;
	private int destination;
	private Priority priority;
	private Prefix prefix;
	private int preference;
	private int pathLength;
	private int ASPathLength;
	
	private int delay;
	
	public AbstractMessage(int borderRouter, int source, int destination, 
			Prefix prefix, int preference, int ASPathLength, int pathLength, Priority priority, int delay) {
		this.borderRouter = borderRouter;
		this.source = source;
		this.destination = destination;
		this.preference = preference;
		this.prefix = prefix;
		this.pathLength = pathLength;
		this.priority = priority;
		this.delay = delay;
		this.ASPathLength = ASPathLength;
	}
	
	public AbstractMessage(AbstractMessage mess, int source, int destination) {
		this(mess.borderRouter, source, destination, mess.prefix, mess.preference, mess.ASPathLength, mess.pathLength+1, mess.priority, mess.delay);
	}
	
	public AbstractMessage(Route r, int source, int destination, int delay) {
		this(r.border_router(), source, destination, r.prefix(), r.local_pref(), r.as_path_length(), r.path_length()+1, r.priority(), delay);
	}
	
	public UNBRMessage toNBR(int grp, int source, int destination) {
		
		int hop;
		if(this.priority() == Priority.LOW)
			return null;
		else if(this.priority() == Priority.MEDIUM)
			hop = 1;
		else
			hop = (2*grp)-1;
		
		UNBRMessage mess = new UNBRMessage(this.borderRouter(), source, destination, this.prefix(), this.preference(), this.ASPathLength, this.pathLength()+1, this.priority(), this.delay(), source, hop);
		return mess;
	}
	
	public UDWNMessage toDwn(int source, int destination) {
		UDWNMessage mess = new UDWNMessage(this.borderRouter(), source, destination, this.prefix(), 
				this.preference(), this.ASPathLength, this.pathLength()+1, this.priority(), this.delay());
		
		return mess;
	}
	
	@Override
	public int compareTo(AbstractMessage o) {
		return Integer.compare(this.delay, o.delay);
	}
	
	public int borderRouter() {
		return borderRouter;
	}
	
	public int source() {
		return source;
	}
	
	public int destination() {
		return destination;
	}
	
	public Prefix prefix() {
		return prefix;
	}
	
	public int pathLength() {
		return pathLength;
	}
	
	public Priority priority() {
		return priority;
	}
	
	public int delay() {
		return delay;
	}
	
	public int preference() {
		return preference;
	}
	
	public int ASPathLength() {
		return ASPathLength;
	}
	
	public void increaseDelay(int delay) {
		this.delay += delay;
	}
	
	public void increasePathLength(int i) {
		this.pathLength += i;
	}
	
	public String toString() {
		return borderRouter+" "+source+" "+destination+" "+prefix+" "+preference+" "+ASPathLength;
	}
}
