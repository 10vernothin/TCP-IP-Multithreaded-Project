package client;

//run this to send prompted user-input into server

public class InputClientMain {
	public static void main(String[] args) throws Exception {			
			ReaderClient m = new ReaderClient(MultiClient.buildMultiClient(3, "127.0.0.1", 1501));
			m.readFromInputPrompt();
	}		
}
	

