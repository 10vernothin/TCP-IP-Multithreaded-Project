//please copy-paste MultiServer.java into the same Project and Package before compiling

package server;

import java.io.*;

public class MultiServerMain {

	public static void main(String[] args) throws InterruptedException {
		
		final String RECEIVED_FILE_PATH = "test files/receiver_file.txt";
		
		try {
				MultiClientServer.softConnectServerAtPort(1501).openServerforWriting(RECEIVED_FILE_PATH);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
