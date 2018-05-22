package client;

import java.io.*;
import java.net.*;

/*
 * FileReaderClient is meant to read a text file and send the data with through to a multithreaded server through Multiple Clients 
 * Is an extension of the MultiClient class
 */
public class ReaderClient extends MultiClient {

	//constructor
	public ReaderClient(int socks_opened, String name, int port) throws ConnectException, IOException {
		super();
		this.resetMultiClient(socks_opened, name, port);
	}
	
	//this constructor inputs a MultiClient object and turns it into a new ReaderClient object 
	public ReaderClient(MultiClient m) throws IOException {
		super();
		this.resetMultiClient(m.getSocketAmt(), m.getMachineName(), m.getPort());
	}
	
//member functions
	
	public void readAndExtractFile(File f) throws Exception {
		
		//opens the client socket connections if it isn't
		if (!this.isClientOpen()) {
			this.openMultiClient();
		}
		
		//initializing variables
		int socketAmt = this.getSocketAmt();
		DataOutputStream[] outStreams = new DataOutputStream[socketAmt];
		DataInputStream[] inStreams = new DataInputStream[socketAmt];
		Socket socks[] = new Socket[socketAmt];
		for (int x = 0; x < socketAmt; x++) {
			socks[x]= this.getClientSocket(x);
			inStreams[x] = new DataInputStream(socks[x].getInputStream());
			outStreams[x] = new DataOutputStream(socks[x].getOutputStream());
		}
		BufferedReader br=new BufferedReader(new FileReader(f));
		String textData;
		String serverMessage="";

		//sending and reading the data
		int iterator = 0;
		while (true) { 
			if ((textData = br.readLine()) != null){
				System.out.println(textData);
			} else {
				for(DataOutputStream x: outStreams) {
					x.writeUTF("/end/");
					x.flush();
				}
				break;
			}
			outStreams[iterator].writeUTF(textData);
			outStreams[iterator].flush();
			serverMessage=inStreams[iterator].readUTF();
			if (iterator == socketAmt-1) {
				iterator = 0;
			} else {
				iterator++;
			}
			System.out.println("Server says:" + serverMessage);
		}
		System.out.println("File transfer complete.");
		
		//cleanup
		br.close();
		for (int i = 0; i < socketAmt; i++) {
			inStreams[i].close();
			outStreams[i].close();
		}
		this.closeMultiClient();
	}
//alternatively just 
public void readFromInputPrompt() throws Exception {
		
		//opens the client socket connections if it isn't
		if (!this.isClientOpen()) {
			this.openMultiClient();
		}
		
		//initializing variables
		int socketAmt = this.getSocketAmt();
		DataOutputStream[] outStreams = new DataOutputStream[socketAmt];
		DataInputStream[] inStreams = new DataInputStream[socketAmt];
		Socket socks[] = new Socket[socketAmt];
		for (int x = 0; x < socketAmt; x++) {
			socks[x]= this.getClientSocket(x);
			inStreams[x] = new DataInputStream(socks[x].getInputStream());
			outStreams[x] = new DataOutputStream(socks[x].getOutputStream());
		}
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		String clientMessage;
		String serverMessage="";

		//sending and reading the data
		int iterator = 0;
		while (true) { 
			System.out.println("Enter Message:");
			clientMessage = br.readLine();
			if (!clientMessage.equals("/end/")){
				System.out.println(clientMessage);
			} else {
				for (int x = 0; x < outStreams.length; x++) {
					for(DataOutputStream x2: outStreams) {
						x2.writeUTF("/end/");
						x2.flush();
					}
				}
				System.out.println("Program complete.");
				
				//cleanup
				br.close();
				for (int i = 0; i < socketAmt; i++) {
					inStreams[i].close();
					outStreams[i].close();
				}
				this.closeMultiClient();
				return;
			}
			outStreams[iterator].writeUTF(clientMessage);
			outStreams[iterator].flush();
			serverMessage=inStreams[iterator].readUTF();
			if (iterator == socketAmt-1) {
				iterator = 0;
			} else {
				iterator++;
			}
			System.out.println("Server says:" + serverMessage);
		}
		
	}
	
}
