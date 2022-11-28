import java.net.ServerSocket;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.net.BindException;
import java.net.InetAddress;
public class Server {
	private ServerSocket serverSocket;
	private String password;
	static int portNumber=5332;
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
				SERVER_IP=InetAddress.getLocalHost(); 
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		
		System.out.printf("Secure Server Started on %s port# %d\n", SERVER_IP, portNumber);

		try {
			while(!serverSocket.isClosed()) {
				Socket socket = serverSocket.accept();
				System.out.println(socket.getInetAddress().getHostAddress().toString());
				System.out.println("A new client has accessed the server.");
				ClientHandler clientHandle = new ClientHandler(socket, admin);
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
		while(true){
			try{
				System.out.println("What port number would you like to operate this server on? ->");
				portNumber = scan.nextInt();
				serverSocket = new ServerSocket(portNumber);
				break;
			}catch(BindException be){
				System.out.println("ERROR: Port already in use, try another number");
				
			}catch(InputMismatchException ime){
				System.out.println("\nERROR: input was not a valid port number, try again.");
				scan.next();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		 
		Server server = new Server(serverSocket);
		server.startServer();
		
	}
}
