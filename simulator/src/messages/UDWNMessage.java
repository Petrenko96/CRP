package messages;

import routing.Prefix;
import routing.Route;
import routing.Route.Priority;

public class UDWNMessage extends AbstractMessage{

	public UDWNMessage(int borderRouter, int source, int destination, Prefix prefix, int preference, int ASPAthLength, int pathLength,
			Priority priority, int delay) {
		super(borderRouter, source, destination, prefix, preference, ASPAthLength, pathLength, priority, delay);
	}
	
	public UDWNMessage(Route r, int source, int destination, int delay) {
		super(r, source, destination, delay);
	}

}
