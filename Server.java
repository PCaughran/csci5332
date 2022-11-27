import java.net.ServerSocket;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.net.InetAddress;
public class Server {
	private ServerSocket serverSocket;
	private String password;
	static int portNumber=3050;
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
		while(true)
			try(Scanner scan = new Scanner(System.in)){
			//TODO ASK FOR PORT NUMBER AT SPOME POINT
			portNumber = 3050;
			SERVER_IP=InetAddress.getLocalHost(); 
			if(SERVER_IP != null) break;
			}catch(InputMismatchException ime) {
				System.out.print("\nInput not of desired type, try again.");
				continue;
			}catch(Exception ex) {
			ex.printStackTrace();
			}
		
		System.out.printf("Secure Server Started on %s port# %d\n", SERVER_IP, portNumber);

		try {
			while(!serverSocket.isClosed()) {
				Socket socket = serverSocket.accept();
				

				System.out.println("A new client has accessed the server.");
				ClientHandler clientHandle = new ClientHandler(socket, admin);

				
				Thread thread = new Thread(clientHandle);
				thread.start();
			}
		}catch(Exception ex) {
			
		}
	}
	
	public String establishPassword() {
		Scanner scan = new Scanner(System.in);
		while(true) {
		System.out.print("What is the password for this server? ->");
		String password = scan.nextLine();
		System.out.print("Password is "+password+". (y/n)->");
		char answer = scan.nextLine().charAt(0);
		if(answer == 'y') {
			

		return password;

		}
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
		
		ServerSocket serverSocket = new ServerSocket(portNumber);
		Server server = new Server(serverSocket);
		server.startServer();
		
	}
}
