package client;

import java.io.*;
import java.net.*;

class MultiClient {

	private	Socket[] sockets;
	private	String machineName = null;
	private int port = 1500;
	private int socksPerClient = 1;
	private static int clientID = 0; //this will be used later
	private boolean isOpen = false;
	
	protected MultiClient() {
		//THIS IS A BUILDER CLASS -- extended only to its children
		sockets = null;
		clientID++;
	}

//Getters
	public Socket getClientSocket(int n) {
		if (sockets != null) {
			return sockets[n];
		} else {
			return null;
		}
	}

	public Socket[] getSocketArray() {	
		return sockets;
	}
	
	public String getMachineName() {
		return machineName;
	}

	public int getPort() {
		return port;
	}

	public int getSocketAmt() {
		return socksPerClient;
	}

	public static int getClientID() {
		return clientID;
	}
	
	public boolean isClientOpen() {
		return isOpen;
	}
	
//setters
	
	/*
	 * setMultiClient set $socks, $name, $port to the member variables
	 */

	public MultiClient resetMultiClient(int socks, String name, int port) throws IOException {
		if (sockets != null) {
			for (int n = 0; n < sockets.length; n++) {
				sockets[n].close();	
			}
		}
		this.machineName = name;
		this.port = port;
		this.socksPerClient = socks;
		this.sockets = new Socket[socks];
		return this;
	}

	/*
	 * buildMultiClient is a static builder that creates an array of $sock_per_client Sockets at $name and $port
	 */

	public static MultiClient buildMultiClient(int sockAmt, String name, int port) throws ConnectException, IOException {
		MultiClient m = new MultiClient();
		try {
			m.resetMultiClient(sockAmt, name, port);
		} catch (java.lang.NullPointerException e) {
			System.out.println("Null Pointer Error");
			e.printStackTrace();
		}
		return m;
	}


	/*
	 * openClient cleans up any sockets 
	 * then reallocates an array of $socks_per_client new Sockets($machine_name, $port)
	 * returns &this for in-line purposes
	 */
	public MultiClient openMultiClient() throws Exception {
		try {
			if (socksPerClient == sockets.length) { //this should be exception safe but just on the safe side 
				sockets = new Socket[socksPerClient];
				for (int m = 0; m < socksPerClient; m++) {
					sockets[m] = new Socket(machineName, port);
				}
				System.out.println( socksPerClient + " sockets connected to Server at Port #" + port + ".");
				isOpen = true;
			} else {
				throw new Exception("SocketArray Length and Socket Number does not match");
			}
		} catch (ConnectException e) {
			System.out.println("Connection Error: Server at Port #" + port + " not available.");
			System.exit(-1);
		} catch (IOException e) {
			System.out.println(e);
			System.exit(-1);
		} catch (java.lang.NullPointerException e) {
			System.out.println("Null Pointer Error");
			System.exit(-1);
		}

		return this;
	}


	/*
	 * Closes all sockets for the client.
	 * NOTE: Should always be invoked at end of program.
	 */

	public void closeMultiClient() {
		if (sockets != null) {
			for (int n = 0; n < sockets.length; n++) {
				try {
					sockets[n].close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}		
}
