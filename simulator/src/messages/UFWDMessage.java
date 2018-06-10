package messages;

import routing.Prefix;
import routing.Route.Priority;

public class UFWDMessage extends AbstractMessage {

	public UFWDMessage(int borderRouter, int source, int destination, Prefix prefix, int preference, int ASPathLength, int pathLength,
			Priority priority, int delay) {
		super(borderRouter, source, destination, prefix, preference, ASPathLength, pathLength, priority, delay);
	}
	
	public UFWDMessage(UFWDMessage mess, int source, int destination) {
		super(mess, source, destination);
	}
}
