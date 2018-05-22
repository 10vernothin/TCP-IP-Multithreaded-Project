package client;

import java.io.File;

//Run this to send extractor_file.txt into server

public class FileReaderClientMain {

	public static void main(String[] args) {
		
		
		final String TARGET_FILE_PATH = "test files/extractor_file.txt";
		
		try {
			ReaderClient fc = new ReaderClient(MultiClient.buildMultiClient(3, "127.0.0.1", 1501));
			fc.readAndExtractFile(new File(TARGET_FILE_PATH));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
