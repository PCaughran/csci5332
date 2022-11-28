import java.net.InetAddress;
import java.util.ArrayList;

public class Admin {
	private String password;
	private int attemptsAllowed;
	//TODO see if we can include a list of IP addresses that have been excluded due to too many incorrect passwords
	ArrayList<InetAddress> bannedIPAddresses;
	//initialize the Admin object and set the password and number of attempts
	public Admin(String password, int attemptsAllowed) {
		this.password = password;
		this.attemptsAllowed = attemptsAllowed;
		this.bannedIPAddresses = new ArrayList<>();
	}
	
	public int getAttemptsAllowed() {
		return this.attemptsAllowed;
	}
	
	public String getPassword() {
		return this.password;
	}

	public void banIPAddress(InetAddress address){
		bannedIPAddresses.add(address);
	}
}
