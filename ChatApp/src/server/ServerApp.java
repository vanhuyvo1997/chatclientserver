package server;

import java.io.IOException;

public class ServerApp {
	public static void main(String[] agrs) {
		try {
			Server app = new Server();
			app.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
