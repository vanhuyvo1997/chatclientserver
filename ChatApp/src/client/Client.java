package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Scanner;

public class Client {

	private static final int minPortNum = 1024, maxPortNum = 65535;

	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	private Thread receiver, sender;
	private String message;
	private String nickName;
	private int port;
	private String hostName;

	public Client() throws IOException {
		super();
		nickName = null;
		message = null;
		hostName = null;
		port = 0;

	}

	private void typeHostName() {

		boolean err = false;
		Scanner sc;

		while (!err) {

			System.out.println("input your Host IP Address:");
			sc = new Scanner(System.in);
			hostName = sc.nextLine();

			if (hostName.equals("")) {
				err = false;
				System.out.println("your Hostname must be not null. please try again!");
			} else {
				err = true;
			}
		}
		
	}

	private void typePortNumber() {
		
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
				
				if (number < Client.minPortNum || number > Client.maxPortNum) {
					err = true;
				}
					
			} catch (Exception e) {
				err = true;
			}
			
			if (err) {
				System.out.println("Port value must be greater or equal than " + Client.minPortNum
						+ " and lower or equal " + Client.maxPortNum);
			}
			
		} while (err);
		port = number;
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
			} else {
				err = true;
			}
		}
	}

	private void connectToServer() throws IOException {
		
		boolean isConnected;
		
		typeNickName();
		
		
		do {
			try {
				
				typeHostName();
				typePortNumber();
				socket = new Socket(hostName, port);
				isConnected = true;
				createSenderAndReceiver();
				System.out.println("Connected to server! \n Type your message now...");
				
			} catch (UnknownHostException unknowHostEx) {
				
				System.out.println("Unkowhost, please try again! ");
				isConnected = false;
				
			} catch (ConnectException connectEx) {
				
				System.out.println("Port not found, please try again! ");
				isConnected = false;
			}
		} while (!isConnected);

	}

	private void createSenderAndReceiver() throws UnknownHostException, IOException {
		
		message = null;
		in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		createSender();
		createReceiver();
	}

	private void createSender() {
		
		sender = new Thread(() -> {
			
			Scanner sc;
			while (!socket.isClosed()) {
				
				sc = new Scanner(System.in);
				message = sc.nextLine();
				message = message.trim();
				
				if (!message.equals("") && !message.equals("\r")) {
					sendMessage(message);
				}
			}
		}, "Sender");
	}

	private void sendMessage(String message2) {
		
		message = encodeSendedMessage(message);
		
		try {
			
			out.write(message);
			out.newLine();
			out.flush();
			
		} catch (IOException e) {
			System.out.println("Can't send, Server has been not found...");
		}
	}

	private void createReceiver() {
		
		receiver = new Thread(new Runnable() {
			
			public void run() {
				
				try {
					while (!socket.isClosed()) {
						message = in.readLine();
						message = decodeReceivedMessage(message);
						if (message != null && !message.equals("\r") && !message.equals("")) {
							System.out.println(message);
						}
					}
				} catch (IOException e) {
					
					System.out.println("Server not fonud ...");
				} catch (NullPointerException e) {
					
					e.printStackTrace();
				}
			}
		}, "Receiver");

	}

	private String encodeSendedMessage(String message) {
		
		return (new StringBuilder(socket.toString()).append("@").append(this.nickName).append(" :").append(message))
				.toString();
	}

	private String decodeReceivedMessage(String message) {
		
		String[] tempMessage;
		tempMessage = message.split("@", 2);
		if (this.socket.toString().equals(tempMessage[0])) {
			return null;
		}
		message = tempMessage[1];
		
		return message;
	}

	public void start() throws IOException {
		connectToServer();
		receiver.start();
		sender.start();
	}
}
