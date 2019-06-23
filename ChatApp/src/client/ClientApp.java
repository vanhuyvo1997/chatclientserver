package client;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientApp {
	private Client client;
	private String nickName, hostName;
	private int port;

	public ClientApp() {
		super();
	}

	private void intit() {
		typeNickName();
		typeHostName();
		typePort(1024, 65535);
	}

	private void typePort(int minNum, int maxNum) {
		int number = 0;
		boolean err = false;
		String strTemp;
		Scanner scanner;
		do {
			try {
				System.out.println("Type your port: ");
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

	private void typeHostName() {
		boolean err = false;
		Scanner sc;
		while (!err) {
			System.out.println("input your Hostname:");
			sc = new Scanner(System.in);
			hostName = sc.nextLine();
			if (hostName.equals("")) {
				err = false;
				System.out.println("your Hostname must be not null. please try again!");
			} else
				err = true;
		}
	}

	private void typeNickName() {
		boolean err = false;
		Scanner sc;
		while (!err) {
			System.out.println("input your nickname:");
			sc = new Scanner(System.in);
			nickName = sc.nextLine();
			if (nickName.equals("")) {
				err = false;
				System.out.println("your nickname must be not null. please try again!");
			} else
				err = true;
		}
	}

	public void start() {
		boolean err = true;
		do {
			err = true;
			intit();
			try {
				client = new Client(hostName, port, nickName);
				client.start();
				err = false;
			} catch (UnknownHostException e) {
				System.out.println("Host not found, please try again!");
				err = true;
			} catch (ConnectException e) {
				System.out.println("Host not found, please try again!");
				err = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				err = true;
			}
		} while (err);
	}

	public static void main(String[] agrs) {
		ClientApp app = new ClientApp();
		app.start();
	}
}
