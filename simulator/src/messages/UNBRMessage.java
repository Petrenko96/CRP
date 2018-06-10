package messages;

import routing.Prefix;
import routing.Route.Priority;

public class UNBRMessage extends AbstractMessage {

	private int hop;
	private int sender;
	
	public UNBRMessage(int borderRouter, int source, int destination, Prefix prefix, int preference, int ASPathLength, int pathLength,
			Priority priority, int delay, int sender, int hop) {
		super(borderRouter, source, destination, prefix, preference, ASPathLength, pathLength, priority, delay);
		this.hop = hop;
		this.sender = sender;
	}
	
	public UNBRMessage(UNBRMessage mess, int source, int destination, int delay) {
		this(mess.borderRouter(), source, destination, mess.prefix(), mess.preference(), mess.ASPathLength(), mess.pathLength()+1, 
				mess.priority(), mess.delay()+delay, mess.sender, mess.hop-1);
	}
	
	public int hop() {
		return hop;
	}
	
	public int sender() {
		return sender;
	}

}
