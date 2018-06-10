package messages;

import routing.Prefix;
import routing.Route.Priority;

public class UCTCMessage extends AbstractMessage {

	public UCTCMessage(int borderRouter, int source, int destination, Prefix prefix, int preference, int ASPathLength, int pathLength,
			Priority priority, int delay) {
		super(borderRouter, source, destination, prefix, preference, ASPathLength, pathLength, priority, delay);
	}
	
	public UCTCMessage(AbstractMessage mess, int source, int destination) {
		this(mess.borderRouter(), source, destination, mess.prefix(), mess.preference(), mess.ASPathLength(), mess.pathLength(), mess.priority(), mess.delay());
	}
	
}
