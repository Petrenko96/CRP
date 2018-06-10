package routing;

public class ForwardingPath {

	private Prefix prefix;
	private int nextHop;
	private boolean in;
	
	public ForwardingPath(Prefix prefix, int nextHop, boolean in) {
		this.prefix = prefix;
		this.nextHop = nextHop;
		this.in = in;
	}
	
	public Prefix prefix() {
		return prefix;
	}
	
	public int nextHop() {
		return nextHop;
	}
	
	public boolean in() {
		return in;
	}
	
	public boolean equals(ForwardingPath p) {
		return prefix.equals(p.prefix) && nextHop == p.nextHop && in == p.in;
	}
}
