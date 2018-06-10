package messages;

import routing.Prefix;

public class ActivationMessage extends CRPMessage {
	
	private Prefix prefix;
	private boolean activate;
	private Prefix desactivator;
	
	public ActivationMessage(int source, int destination, Prefix prefix, boolean activate, Prefix desactivator, int delay) {
		super(source, destination, delay);
		
		this.prefix = prefix;
		this.activate = activate;
		this.desactivator = desactivator;
	}
	
	public boolean activate() {
		return activate;
	}
	
	public Prefix prefix() {
		return prefix;
	}
	
	public Prefix desactivator() {
		return desactivator;
	}

}
