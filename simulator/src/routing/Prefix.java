package routing;

public class Prefix {
	
	private String prefix;
	private int length;
	private String bitString;
	
	public Prefix(String prefix) {
		String split[] = prefix.split("/");
		this.prefix = split[0];
		this.length = Integer.valueOf(split[1]);
		this.bitString = "";
		
		String fields[] = this.prefix.split("\\.");
		for(int i = 0 ; i < fields.length ; i++) {
			String temp = Integer.toBinaryString(Integer.valueOf(fields[i]));
			for(int j = 8 ;  j > temp.length() ; j--) {
				bitString+="0";
			}
			bitString+=temp;
		}	
	}
	
	public Prefix(String prefix, int length) {
		this.prefix = prefix;
		this.length = length;
		this.bitString = "";
		
		String fields[] = prefix.split("\\.");
		for(int i = 0 ; i < fields.length ; i++) {
			String temp = Integer.toBinaryString(Integer.valueOf(fields[i]));
			for(int j = 8 ;  j > temp.length() ; j--) {
				bitString+="0";
			}
			bitString+=temp;
		}	
	}
	
	public int length() {
		return length;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Prefix) {
			Prefix p = (Prefix) obj;
			return this.hashCode() == p.hashCode();
		}
		return false;
	}
	
	public String getBitString() {
		return this.bitString;
	}
	
	@Override
	public int hashCode() {
		if(length == 0) return 0;
		Long l =  Long.parseLong(bitString.substring(0, length), 2);
		return l.intValue();
	}
	
	public String toString() {
		return prefix+"/"+length;
	}
	
	public boolean related(Prefix p) {
		int min_length = (length < p.length) ? length : p.length;
		return bitString.substring(0, min_length).equals(p.bitString.substring(0, min_length));
	}
	
	/*
	 * Return true if p is included in the current prefix
	 */
	public boolean include(Prefix p) {	
		if(this.length > p.length) return false;
		return this.bitString.contains(p.bitString.substring(0, this.length));
	}
	
}