import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	private Socket socket;
	private BufferedReader br;
	private BufferedWriter bw;
	private String username;
	private boolean isAuthenticated;
	
	public Client(Socket socket, String username) {
		try {
		this.socket = socket;
		this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.bw = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
		this.username = username;
		this.isAuthenticated = false;
		}catch(Exception ex) {
			closeEverything(this.socket, this.br, this.bw);
		}
	}
	
	public void sendMessage() {
		try{
			
			this.bw.write(username);
			this.bw.newLine();
			this.bw.flush();
			
			Scanner scan = new Scanner(System.in);
			while(socket.isConnected()) {
				String message = scan.nextLine();
				this.bw.write(this.username+": "+message);
				this.bw.newLine();
				this.bw.flush();
				
			}
			
		}catch(Exception ex) {
			closeEverything(this.socket, this.br, this.bw);
		}	
	}
	
	public void listenForMessage() {
		new Thread(){
			@Override
			public void run() {
				String message;
				while(socket.isConnected()) {
					try {
						message = br.readLine();
						System.out.println(message);
					}catch(Exception ex) {
						closeEverything(socket, br, bw);
					}
				}
			}
		}.start();
	}
	
	public void closeEverything(Socket socket, BufferedReader br, BufferedWriter bw) {
		try {
			if(socket!=null) {
				socket.close();
			}
			if(br!=null) {
				br.close();
			}
			if(bw!=null) {
				bw.close();
			}
		}catch(Exception ex) {ex.printStackTrace();}
	}
	
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.print("Thank you for joining the SecureChat Server. What is the IP of the server you want to join? " );
		String ip = scan.nextLine();
		
		System.out.println("Enter your username: ");
		Socket socket= null;
		String username = scan.nextLine();
		try{
			socket = new Socket(ip, 5332);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
		Client client = new Client(socket, username );
		client.listenForMessage();
		client.sendMessage();
	}
}
