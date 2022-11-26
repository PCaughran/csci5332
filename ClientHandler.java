import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
	
	public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
	private Socket socket;
	private BufferedReader br;
	private BufferedWriter bw;
	private String username;
	private boolean isAuthenticated;

	private Admin admin;
	
	public ClientHandler(Socket socket, Admin admin) {


	
	public ClientHandler(Socket socket) {

		try{
			this.socket = socket;
			this.bw = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
			this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.username = br.readLine();

			this.admin = admin;
			this.isAuthenticated = false;
			clientHandlers.add(this);
			this.isAuthenticated =this.authenticate();
			
			if(this.isAuthenticated) {
				
				broadcastMessage("SERVER: "+this.username+" has entered the chat");
			}
			else {
				
				this.bw.write("PASSWORD AUTHENTICATION FAILED");
				this.bw.newLine();
				this.bw.flush();
				
			}

			
			
			clientHandlers.add(this);
			broadcastMessage("SERVER: "+this.username+" has entered the chat");

		}catch(Exception ex) {
			closeEverything(this.socket, this.br, this.bw);
		}
	}

	public boolean authenticate() {
		
		for(ClientHandler c : clientHandlers) {
			try{
				String guess = "";
				if(!c.isAuthenticated) {
				int attempts = 3;
					while(attempts > 0 && !c.isAuthenticated) {
						c.bw.write(String.format("Attempts Remaining: %d| Enter the password for the server. ->",attempts));
						c.bw.newLine();
						c.bw.flush();
						guess = c.br.readLine();
						guess = guess.substring(this.username.length() +2);
						c.isAuthenticated = guess.equals(this.admin.getPassword());
						c.bw.write(String.format("Password is correct: %b", c.isAuthenticated));
						c.bw.newLine();
						c.bw.flush();
						
						attempts--;
					}
				}
			}catch(Exception ex) {
			
			}
		
		}
		return this.isAuthenticated;
	}
	

	@Override
	public void run(){
		String message;
		while(socket.isConnected()) {
			try {
				message = this.br.readLine();
				broadcastMessage(message);
			}catch(Exception ex) {
				closeEverything(this.socket, this.br, this.bw);
				break;
			}
		}
	}
	
	public void broadcastMessage(String message) {
		
		
		for(ClientHandler c: clientHandlers) {
			try {

				if (!c.username.equals(this.username)&& c.isAuthenticated) {

				if (!c.username.equals(this.username)) {

					c.bw.write(message);
					c.bw.newLine();
					c.bw.flush();
				}
			}catch(Exception ex) {
				closeEverything(this.socket, this.br, this.bw);
			}
		}
	}
	
	public void removeClientHandler() {
		clientHandlers.remove(this);
		broadcastMessage("SERVER: "+ this.username+" has left the chat");
	}
	
	
	
	
	public void closeEverything(Socket socket, BufferedReader br, BufferedWriter bw) {
		removeClientHandler();
			try {
				if (br != null) {
					br.close();
				}
				if (bw != null) {
					bw.close();
				}
				if(socket != null) {
					socket.close();
				}
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		
	}
}
