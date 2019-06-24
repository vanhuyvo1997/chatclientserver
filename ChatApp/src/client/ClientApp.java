package client;

import java.io.IOException;

public class ClientApp {
	
	public static void main(String[] agrs) {
		try {
			Client app = new Client();
			app.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block 
			e.printStackTrace();
		}
	}
	
}
