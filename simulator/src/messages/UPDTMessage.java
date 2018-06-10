package messages;

import routing.Prefix;
import routing.Route.Priority;

public class UPDTMessage extends AbstractMessage {

	public UPDTMessage(int borderRouter, int source, int destination, Prefix prefix, int preference, int ASPathLength, int pathLength,
			Priority priority, int delay) {
		super(borderRouter, source, destination, prefix, preference, ASPathLength, pathLength, priority, delay);
	}

}
