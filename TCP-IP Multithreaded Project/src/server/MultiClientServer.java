package server;
import java.awt.Desktop;
import java.io.*;
import java.net.*;
import java.util.Scanner;

/*
 * 
 * REFERENCES: 
 * https://systembash.com/a-simple-java-tcp-server-and-tcp-client/ 
 * http://net-informations.com/java/net/multithreaded.htm
 * 
 *
 */

class ServerConnectionThread extends Thread {

	Socket serverClient;
	int clientNo;
	Object writer;

	//constructor for writing
	ServerConnectionThread(Socket inSocket,int address, PrintWriter r){
		serverClient = inSocket;
		clientNo = address;
		writer = r;
	}

	//constructor for reading
	ServerConnectionThread(Socket inSocket, int address){
		serverClient = inSocket;
		clientNo = address;
		writer = false;
	}

	public void run(){
		try{
			//initialize variables
			DataInputStream inStream = new DataInputStream(serverClient.getInputStream());
			DataOutputStream outStream = new DataOutputStream(serverClient.getOutputStream());
			String clientMessage = "";
			String serverMessage = "";

			//bounce-back(read) OR write data
			while(!clientMessage.equals("/end/")){
				clientMessage= inStream.readUTF();
				System.out.println("Message From Client " + clientNo + ": "+clientMessage);
				//serverMessage="Server Received Message: " + clientMessage;

				if (!(writer.getClass() == boolean.class || clientMessage.equals("/end/"))) {
					((PrintWriter) writer).println(clientMessage);
				}
				serverMessage= "Received: " + clientMessage;
				outStream.writeUTF(serverMessage);
				outStream.flush();
			}
			if (writer.getClass() != boolean.class) {
				((PrintWriter) writer).flush();
			}
			
			//cleanup
			inStream.close();
			outStream.close();
			serverClient.close();
			return;
			
		//exception handling
		}catch (EOFException e) {			
		} catch (FileNotFoundException e) {
			System.out.println(e);
			return;
		}catch(Exception e){
			System.out.println(e);
		}finally{
			System.out.println("Client " + clientNo + " disconnected from server.");
		}
	}
}


public class MultiClientServer extends ServerSocket {

	/*
	 * This class implements a singleton builder method
	 */

	//class variable
	static MultiClientServer connection;

	//unreachable constructor
	private MultiClientServer(int port) throws IOException {
		super(port);
		System.out.println("Server successfully opened at local port #" + super.getLocalPort() + ".");
	}

	//class methods

	//WARNING: This will prompt an input if no Server exists. Use the overloaded one for no input prompt
	public static MultiClientServer connectServer() throws NullPointerException {

		if (connection == null) {
			String answer = "";
			String prompt = "No Server Currently Exists. Input a Port# (ex. 1500) to create new server or \\n\\ to exit.";
			System.out.println(prompt);
			Scanner s = new Scanner(System.in);
			while (!((answer == "n") || (answer.matches("-?\\d+") && (Integer.parseInt(answer) >= 1500)))) {
				if (answer != "") {
					System.out.println("Invalid input. Input a valid Port# (ex. 1500) to create new server or \\n\\ to exit.");
				}

				answer = s.nextLine();
				if (answer.matches("-?\\d+")){  //looks for only digits no spaces
					if (Integer.parseInt(answer) >= 1500) { 
						try {
							connection = new MultiClientServer(Integer.parseInt(answer));
						} catch (NumberFormatException | IOException e) {
							e.printStackTrace();
						} 
					}
				}
				s.reset();
			}
			s.close();
		}
		if (connection == null) {
			System.out.println("Error: No Server Exists.");
			System.exit(-1);;
		}
		return connection;
	}

	//softConnectServer will get Server or create a Server at $port if none is found  
	public static MultiClientServer softConnectServerAtPort(int port) throws IOException {
		if (connection == null) {
			if (port > 1500) {
				connection = new MultiClientServer(port);
			} else {
				System.out.println("Error: Failed to create Server. Invalid Port.");
				System.exit(-1);
			}
		} else if (!(connection.getLocalPort() == port)) {
			System.out.println("Connection Established at Port #" +connection.getLocalPort() + " instead.");
		}
		return connection;
	}

	//resetServerAtPort will close the current server-socket and renew the socket on a new $port
	public static MultiClientServer resetServerAtPort(int port) throws IOException {
		if (connection != null) {
			connection.close();
		}
		connection = new MultiClientServer(port);
		return connection;
	}

	/*
	 * openServer will prompt the server to start listening for connections and create client threads. This function will be opened indefinitely
	 *until the window is closed (time-based closing is not yet implemented)
	 *Important to note: this is a member method, not a class method
	 *
	 */
	public void openServerforWriting(String targetFile) throws IOException{
		System.out.println("Looking for clients...");
		Socket serverClient = new Socket();
		serverClient.close();
		boolean firstClient = false;
		int count = 0;
		int timeout = 0; 
		ServerConnectionThread[] s;
		if (connection != null) {
			while (true) {
				try {
					timeout++; //timer for inactivity
					connection.setSoTimeout(1); //hold has 1milisecond timeout to catch exceptions
					serverClient = connection.accept(); //first hold has no timeout
					timeout = 0;
					s = new ServerConnectionThread[1];
					firstClient = true;
					PrintWriter writer = new PrintWriter(targetFile, "UTF-8");
					while (true) {
						if (!firstClient) {
							try {
								timeout++; //timer for inactivity
								Socket extraClient= connection.accept();
								timeout = 0;
								count++;
								ServerConnectionThread[] s_temp = new ServerConnectionThread[count];
								for (int i = 0; i < count-1; i++) {
									s_temp[i] = s[i];
								};
								s = s_temp;
								System.out.println(" >> " + "Client " + extraClient.getPort() + " connected to server.");
								s[count-1] = new ServerConnectionThread(extraClient, extraClient.getPort(), writer);
								s[count-1].start();
							} catch (SocketTimeoutException e) { 	//socket timeout to check if all sockets are dead. If so, the function exits 
								boolean isDead = true; 				//to refresh the server. May re-edit this in the future to be more elegant
								for (ServerConnectionThread m: s) {
									if (m.isAlive()) {
										isDead = false;
									}
								}
								if (isDead) {
									Desktop.getDesktop().open(new File(targetFile));
									System.out.println("Client task complete, Server will now refresh.");
									System.out.println("Looking for new clients...");
									break;
								} else {
									continue;
								}
							} //server accept the client connection request

						}  else {
							count++;
							firstClient = false;
							System.out.println(" >> " + "Client " + serverClient.getPort() + " connected to server.");
							s[count-1] = new ServerConnectionThread(serverClient, serverClient.getPort(), writer);
							s[count-1].start();
						} //send the request to a separate thread
					}
				} catch (SocketTimeoutException e) {
					int max_timeout = 600000;
					
					if ((timeout-1)%60000 == 0) {
						System.out.println((max_timeout - ((timeout-1)))/60000 + " minutes left before timeout.");
					}
					if (timeout == max_timeout-1) { //10 mins = 10*60*1000milisecond ticks (plus processing time)
						System.out.println("Server timed out due to inactivity.");
						break;
					}
					continue;
				}
			}
		}
	}
}


