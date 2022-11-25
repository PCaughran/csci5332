import java.util.Scanner;

public class Admin {
	private String password;
	//maybe include a list of IP addresses that have been excluded due to too many incorrect passwords
	//initialize the Admin object and set the password
	public Admin(String password) {
		this.password = password;
	}
	
	//returns true if password is correct in 3 times, false if not.
	//if not correct, client will forcefully be disconnected
	public boolean authenticate() {
		boolean isAuthenticated = false;
		Scanner scan = new Scanner(System.in);
		int attempt = 0;
		while(attempt < 3 && !isAuthenticated) {
			System.out.print("Enter the password for the Secure Server. ->");
			String response = scan.nextLine();
			isAuthenticated = response.equals(this.password);
			
		}
		return isAuthenticated;
	}
	
	public String getPassword() {
		return this.password;
	}
}
