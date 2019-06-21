package chatclientserver.server;

import java.io.IOException;
import java.util.Scanner;

public class ServerApp {
	private Server server;
	private int port;
	
	public ServerApp() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	private void typePort(int minNum, int maxNum) {
		int number = 0;
		boolean err = false;
		String strTemp;
		Scanner scanner;
		do {
			System.out.println("Input your port: ");
			try {
				err = false;
				scanner = new Scanner(System.in);
				strTemp = scanner.nextLine();
				number = Integer.parseInt(strTemp);
				if (number < minNum || number > maxNum)
					err = true;
			} catch (Exception e) {
				err = true;
			}
			if (err) {
				System.out.println(
						"Port value must be greater or equal than " + minNum + " and lower or equal " + maxNum);
			}
		} while (err);
		port = number;
	}
	
	public void start() {
		typePort(1024, 65535);
		try {
			server = new Server(port);
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] agrs) {
		ServerApp app = new ServerApp();
		app.start();
	}

}
