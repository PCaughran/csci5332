import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {
	private Socket socket;
	private BufferedReader br;
	private BufferedWriter bw;
	private String username;
	
	
	//constructor
	public Client(Socket socket, String username) {
		try { //set instance variables of the client, BR will read outgoing messages and BW will display incoming messages
		this.socket = socket;
		this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.bw = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
		this.username = username;
		
		}catch(Exception ex) {
			System.out.println("ERROR: Failure in Client Constructor, exiting");
			closeEverything(this.socket, this.br, this.bw);
			System.exit(1);
		}
	}
	
	//
	public void sendMessage() {
		try(Scanner scan = new Scanner(System.in)){
			/*initial message sent, will trigger the creation of a server-side clientHandler object
			to correspond to this client*/
			this.bw.write(username);
			/*utilizing bw.newLine(); bw.flush(); because it is very unlikely that the message body is long enough
			to fill the buffer*/
			this.bw.newLine();
			this.bw.flush();
			
			
			while(socket.isConnected()) {
				String message = scan.nextLine();
				this.bw.write(this.username+": "+message); //send the message, using the formatting USER: MESSAGE
				this.bw.newLine();
				this.bw.flush();
			}
		
		//if something goes wrong, terminate everything
		}catch(Exception ex) {
			System.out.println("ERROR: An unexpected error has occured, exiting. Restart to try again.");
			closeEverything(this.socket, this.br, this.bw);
			System.exit(1);
		}	
	}
	
	
	//will open a new thread, so as to run in parallel to the main thread, which will be executing sendMessage()
	public void listenForMessage() {
		//create a new thread
		new Thread(){
			@Override
			public void run() {
				String message;
				
				while(socket.isConnected()) {
					try {
						message = br.readLine(); //get the message
						//check to see if it is a server distributed error message, proceeed accordingly
						if(message.equals("PASSWORD AUTHENTICATION FAILED")) {
							System.out.println("Termination caused by too many incorrect password attempts");
							System.exit(-1);
						}
						
						//if program made it here, the message is not an error message, so it is displayed.
						System.out.println(message);
					//terminate if something fails.
					}catch(Exception ex) {
						System.out.println("ERROR: An unexpected error has occured, exiting. Restart to try again.");
						closeEverything(socket, br, bw);
						System.exit(1);
					}	
				}//end of while
			}//end of run
		}.start(); //will start the newly described thread
	}
	
	//self explanatory
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

		Client client= null;
		Scanner scan = new Scanner(System.in);
		
		try{
		//query user for IP Address and Port number, these need to be previously known. unique to each server.
		System.out.print("Thank you for joining the SecureChat Server. What is the IP/port# of the server you want to join? " );
		String[] ipAndPort = scan.nextLine().split("/");	//split the input at the / because thats how i want it
		
		//TODO implement regex for incorrect IP/PORT input
		
		//query user for username. this will be immutable
		System.out.println("Enter your username, it cannot be changed for the duration of your session. -> ");
		String username = scan.nextLine();

		//create the IP Address Object, and the socket
		InetAddress IPv4 = InetAddress.getByName(ipAndPort[0]);
		Socket socket = new Socket(IPv4, Integer.parseInt(ipAndPort[1]));
		
		//instantiate Client object, and then continuously have them listen and send messages.
		//listenForMessage will open a new thread, so the two threads(main & listen) will be executed in parallel 
		client = new Client(socket, username );
		client.listenForMessage();
		client.sendMessage();
		
		}catch(ConnectException ce) {
			System.out.println("ERROR: Could not connect to the server, insure IP/Port is correct. Restart to retry.");
			System.exit(1);
		}catch(SocketException se) {
			System.out.println("ERROR: Unreachable IP Address, exiting.");
			System.exit(1);
		}catch(NullPointerException npe) {
			System.out.println("Client not properly instantiated, exiting.");
			System.exit(1);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
		
		
		
	}
}
