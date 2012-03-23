package www.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetUtils {

	//get local ip address
	public static String getLocalIpAddress() {  		     
		try {  		        
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();   		    
			en.hasMoreElements();) {  		    
				NetworkInterface intf = en.nextElement();  	        
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();          
				enumIpAddr.hasMoreElements();) {  		       
					InetAddress inetAddress = enumIpAddr.nextElement();  	                
					if (!inetAddress.isLoopbackAddress()) {  		            						
						return inetAddress.getHostAddress().toString();  		                
					}  	           
				}  		        
			} 		    
		} catch (Exception e) {						
			e.printStackTrace();				
		}
		return null;  	
	}
}