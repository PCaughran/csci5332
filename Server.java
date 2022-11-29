import java.net.ServerSocket;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.net.BindException;
import java.net.InetAddress;
public class Server {
	private ServerSocket serverSocket;
	private String password;
	static int portNumber;
	private Admin admin;
	//default constructor, will use default number of attempts provided
		public Server(ServerSocket serverSocket) {
			this(serverSocket, 3);
		}

		//constructor where you can choose the number of attempts
		public Server(ServerSocket serverSocket, int attempts) {
			this.serverSocket = serverSocket;
			this.password = establishPassword();
			this.admin = new Admin(this.password, attempts);
		}
	
	public void startServer() {
	
		InetAddress SERVER_IP = null;
		
			try(Scanner scan = new Scanner(System.in)){
				//get the machine that the server will be running on, get that IP Address
				SERVER_IP=InetAddress.getLocalHost(); 
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		
		System.out.printf("Secure Server Started on %s port# %d\n", SERVER_IP, portNumber);

		try {
			while(!serverSocket.isClosed()) {
				//wait for a connection to the Server
				Socket socket = serverSocket.accept();
				
				//log that a new client has joined, and the clients IP address
				System.out.println("A new client has accessed the server. IP Address: " +socket.getInetAddress().getHostAddress().toString());
				
				//make a client handler object, to run on the server side and take care of functionality
				ClientHandler clientHandle = new ClientHandler(socket, admin);
				//create and start a seperate thread to run the Client handler
				Thread thread = new Thread(clientHandle);
				thread.start();
			}
		}catch(Exception ex) {
			
		}
	}
	
	public String establishPassword() {
		Scanner passwordScanner = new Scanner(System.in);
		while(true) {
			System.out.print("What is the password for this server? ->");
			String password = passwordScanner.nextLine();
			System.out.print("Password is "+password+". (y/n)->");
			char answer = passwordScanner.nextLine().charAt(0);
			if(answer == 'y') {
				return password;
			}else continue;
		}
	}
	
	public void closeServerSocket() {
		try {
			if(serverSocket != null) {
				serverSocket.close();
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	
	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket = null;
		Scanner scan = new Scanner(System.in);
		int attempts;
		while(true){
			try{
				System.out.print("What port number would you like to operate this server on? ->");
				portNumber = scan.nextInt();
				
				System.out.print("How many Password Attempts would you like clients to have? ->");
				attempts = scan.nextInt();
				serverSocket = new ServerSocket(portNumber);
				break;
			}catch(BindException be){
				System.out.println("\nERROR: Port already in use, try another number");
				
			}catch(InputMismatchException ime){
				System.out.println("\nERROR: input was not a valid number, try again.");
				scan.next();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
		Server server = new Server(serverSocket,attempts);
		server.startServer();
		
	}
}
