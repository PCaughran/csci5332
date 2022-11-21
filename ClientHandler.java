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

	
	public ClientHandler(Socket socket) {
		try{
			this.socket = socket;
			this.bw = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
			this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.username = br.readLine();
			
			
			clientHandlers.add(this);
			broadcastMessage("SERVER: "+this.username+" has entered the chat");
		}catch(Exception ex) {
			closeEverything(this.socket, this.br, this.bw);
		}
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
