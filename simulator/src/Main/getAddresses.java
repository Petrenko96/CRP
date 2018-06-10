package Main;

import java.util.ArrayList;

import routing.Address;
import routing.Prefix;

public class getAddresses {
	
	public static String bitStringToPrefix(String bitString) {
		
		int length = bitString.length();
		String prefix = "";
		
		while(bitString.length() < 32) {
			bitString += '0';
		}
		
		for(int i = 0 ; i < 4 ; i++) {
			String temp = bitString.substring(i*8, (i+1)*8);
			int v = Integer.valueOf(temp, 2);
			prefix += Integer.toString(v);
			if(i != 3)
				prefix += '.';
			else
				prefix += '/';
		}
		prefix += length;
		
		return prefix;
	}

	public static Iterable<Address> getAddresses(Prefix p) {
		
		ArrayList<Address> addr = new ArrayList<>();
		
		String bitString = p.getBitString();
		int length = p.length();
		
		for(int i = 0 ; i < Math.pow(2, 32-length) ; i++) {
			String b = Integer.toBinaryString(i);
			for(int j = b.length() ; j < 8 ; j++) {
				b = '0'+b;
			}
			
			String prefix = bitStringToPrefix(bitString.substring(0, length)+b);
			String split[] = prefix.split("/");
			addr.add(new Address(split[0]));
		}
		
		return addr;
	}
	
	public static void main(String args[]) {
		String utah[] = {"161.119.0.0/16", "161.119.0.0/23", "161.119.3.0/24", "161.119.4.0/22", "161.119.8.0/21", "161.119.16.0/20", "161.119.32.0/19",
				"161.119.64.0/18", "161.119.128.0/17", "165.239.0.0/16", "168.177.0.0/16", "168.178.0.0/16", "168.179.0.0/16", "168.180.0.0/16", 
				"204.113.0.0/22", "204.113.3.0/24", "204.113.6.0/23", "204.113.8.0/21", "204.113.16.0/20"};	
		
		String newYork[] = {"128.228.0.0/16", "134.74.0.0/16", "146.95.0.0/16", "146.96.0.0/16", "146.111.0.0/16", "146.245.0.0/16", "146.245.0.0/17", 
				"146.245.128.0/17", "148.84.0.0/16", "149.4.0.0/16", "150.210.0.0/16", "163.238.0.0/16", 
				"198.61.16.0/20", "198.180.141.0/24", "207.159.192.0/18"};
		
		String losAngeles[] = {"161.149.0.0/16", "161.149.5.0/24", "161.149.11.0/24", "161.149.12.0/24", "161.149.14.0/24", "161.149.40.0/24", 
				"161.149.49.0/24", "161.149.50.0/24", "161.149.57.0/24", "161.149.63.0/24", "161.149.80.0/24", "161.149.95.0/24", 
				"161.149.97.0/24", "161.149.102.0/24", "161.149.108.0/24", "161.149.196.0/24", "161.149.204.0/24", "161.149.213.0/24",
				"161.149.221.0/24", "161.149.223.0/24", "161.149.240.0/24", "161.149.251.0/24"};
		
		String miami[] = {"134.53.0.0/16", "134.53.0.0/18", "134.53.64.0/19", "134.53.96.0/19", "134.53.119.0/24", "134.53.128.0/19", 
				"134.53.160.0/19", "134.53.192.0/18", "208.115.160.0/19", "208.115.160.0/24"};

		String seattle[] = {"72.233.128.0/18", "72.233.128.0/20", "72.233.142.0/24", "72.233.143.0/24", "72.233.144.0/22", "72.233.148.0/22", 
				"72.233.152.0/22", "72.233.156.0/22", "72.233.160.0/19", "72.233.160.0/20", "72.233.176.0/20"};
		
		String prefixes[][] = {utah, newYork, losAngeles, miami, seattle};
		int borderRouters[] = {16, 21, 23, 47, 14};
		//String prefixes[][] = {{"192.168.0.0/16"}};
		
		for(int j = 0 ; j < prefixes.length ; j++) {
			for(int k = 0 ; k < prefixes[j].length ; k++) {
				String split[] = prefixes[j][k].split("/");
				Integer i = Integer.valueOf(split[1]);
				System.out.println(prefixes[j][k]+"\t"+(int)Math.pow(2, 32-i));
			}
		}
	}
}
