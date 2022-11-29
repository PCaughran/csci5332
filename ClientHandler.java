import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
	
	enum errorMessages {BANNED, PASSWORD_AUTHENTICATION_FAILED}

	public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
	private Socket socket;
	private BufferedReader br;
	private BufferedWriter bw;
	private String username;
	private boolean isAuthenticated;
	private errorMessages curErrorMessage;
	private Admin admin;
	
	public ClientHandler(Socket socket, Admin admin) {
		try{
			this.socket = socket;
			//instantiate objects that will be key to processing I/O procedures
			this.bw = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
			this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			
			//establish username, will be prompted on the client side
			this.username = br.readLine();

			//instantiate the admin object from the server, 
			//it will be responsible for Banned IP addresses and Password authentication
			this.admin = admin;
			
			//client is not yet authenticated
			this.isAuthenticated = false;
			//we have to add it to the list to make it work, since not yet authenticated they will not recieve any messages
			//TODO investigate using add(0, this) to increase efficiency
			clientHandlers.add(this);
			//now authenticate password
			this.isAuthenticated =this.authenticate();
			
			if(this.isAuthenticated) {
				//let all other users know someone new joined
				broadcastMessage("SERVER: "+this.username+" has entered the chat");
				//let just-joined user see who is currently in the Server
				System.out.println(this.currentServerPopulation());
				writeToClient(this.username,"Welcome, your friends:\n" +this.currentServerPopulation()+"\nare excited to see you");
			}
			else {
				//add the IP address to the server's banned IP list
				this.admin.banIPAddress(socket.getInetAddress());
				//send the client a message, when it is recieved on the client side, it will trigger local actions
				this.writeToClient(this.username, this.curErrorMessage.toString());
			}
		}catch(Exception ex) {
			closeEverything(this.socket, this.br, this.bw);
		}
	}


	//return a Comma Deliminated List of all other usernames Currently in the server
	public String currentServerPopulation(){
		String output = "";
		//if there is only one client in the list, display a message saying that
		if(clientHandlers.size() == 1){
			output="NO ELSE ONE IN SERVER YET";
		}else{
			for(int i = 0; i < clientHandlers.size(); i++){
				//check to see if the username is the user who called it, dont return their name
				if(!clientHandlers.get(i).username.equals(this.username)){
				//we are using +2, because List.add puts the element at the end of the list, so we are looking for the second to last element
				//idea, what if we inserted the new element at the start of the list, to minimize the time it takes to find the element.
				if(i+2 <= clientHandlers.size())	{output+=(clientHandlers.get(i).username+", ");}
				else								{output+=(clientHandlers.get(i).username);}
			}
		}
	}
		return output;
	}

	//Display a message to only the clients that match the given username
	public void writeToClient(String username, String message){
		//iterate over the clients
		for (ClientHandler c : clientHandlers){
			if(c.username.equals(username)){	//find the ones with a matching name
				try{
					//write to the sockets output stream
					c.bw.write(message);
					c.bw.newLine();
					c.bw.flush();
			}catch(IOException ioe){
				//I hope there isn't an exception
			}
			}
		}
	}

	//method to verify that the newly joined user is not on a banned IP address, and has the right password
	public boolean authenticate() {
		boolean passwordCorrect = false;
		
		//check admin to make sure IP is not already banned
		if (this.admin.isIPbanned(this.socket.getInetAddress())){ 
			this.curErrorMessage = errorMessages.BANNED;
		}

		//if IP address is not banned
		if(this.curErrorMessage!=errorMessages.BANNED){
			//iterate over all the clients
			for(ClientHandler c : clientHandlers) {
				try{
					String guess = "";
					if(!c.isAuthenticated) { //find the unauthenticated client
					int attempts = this.admin.getAttemptsAllowed(); //get the number of attempts allowed from the admin object
						
						//loop while there are still attempts and they haven't gotten the right password
						while(attempts > 0 && !passwordCorrect) {
							//query the client for the password, i know this is slightly inefficient, trying to clean up the code and
							//will optimize later
							c.writeToClient(c.username, String.format("Attempts Remaining: %d| Enter the password for the server. ->",attempts));
							
							//process the input, remove the "username: " from the start of their message
							guess = c.br.readLine();
							guess = guess.substring(this.username.length() +2);

							//verify the input using the admin object
							passwordCorrect = guess.equals(this.admin.getPassword());
							
							//write to the user describing the results
							c.writeToClient(c.username, String.format("Password is correct: %b", passwordCorrect));
							
							//decrement attempts to eventually terminate the loop
							attempts--;
						}
					}
				}catch(Exception ex) {
					//dear god i hope there isn't an exception, I don't know what would cause it
				}
			}
		}
		
		//if the error message not banned or not failed password, return true
		return (this.curErrorMessage !=errorMessages.BANNED && this.curErrorMessage != errorMessages.PASSWORD_AUTHENTICATION_FAILED);
	}
	

	//thread that will be running in primary, it is responsible for getting the input and broadcasting it. 
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
	

	//method to send a message to ALL non-Sender users in the server
	public void broadcastMessage(String message) {
		//iterate over all clients in the server
		for(ClientHandler c: clientHandlers) {
			try {
				//if the username does not match the sender's and that client is authenticated
				if (!c.username.equals(this.username)&& c.isAuthenticated) {
					//write to the client. some ineffiency, working on it
					c.writeToClient(c.username, message);
				}
			}catch(Exception ex) {
				closeEverything(this.socket, this.br, this.bw);
			}
		}
	}
	

	//function to remove a client from the server
	public void removeClientHandler() {
		clientHandlers.remove(this);
		broadcastMessage("SERVER: "+ this.username+" has left the chat");
	}
	

	//method to safely close the instance objects that need closing
	public void closeEverything(Socket socket, BufferedReader br, BufferedWriter bw) {
		//we want to make sure the client corresponding to the socket being closed is disconnected from the server
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
}//end of ClientHandler
