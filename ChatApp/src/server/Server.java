package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

public class Server {

	public static final String SERVER_IP = "192.168.40.157";
	public static final int MIN_PORT_NUMBER = 1024;
	public static final int MAX_PORT_NUMBER = 65535;

	private ServerSocket serverSocket;
	private List<ConnectionHandler> listConnection;
	private Queue<String> messageQueue;
	private InetSocketAddress inetSocket;
	private boolean isExit;

	public Server() throws IOException {
		super();
		init();
		System.out.println("Server has started...");
		showServerInfo();
	}

	private void init() throws IOException {
		listConnection = new LinkedList<>();
		messageQueue = new PriorityQueue<>();
		serverSocket = new ServerSocket();
		inetSocket = new InetSocketAddress(InetAddress.getByName(SERVER_IP), typePortNumber());
		serverSocket.bind(inetSocket);
	}

	private int typePortNumber() {

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

				if (number < Server.MIN_PORT_NUMBER || number > Server.MAX_PORT_NUMBER)
					err = true;
			} catch (Exception e) {
				err = true;
			}

			if (err) {
				System.out.println("Port value must be greater or equal than " + Server.MIN_PORT_NUMBER
						+ " and lower or equal " + Server.MAX_PORT_NUMBER);
			}
		} while (err);
		return number;
	}

	private void listenConnections() throws IOException {
		Socket socket = null;
		while (!isExit) {
			socket = serverSocket.accept();
			System.out.println(socket + " is Connected!");
			ConnectionHandler connection = new ConnectionHandler(socket, messageQueue);
			connection.start();
			listConnection.add(connection);
		}

	}
	
	

	private void startSender() {
		Thread sender;
		sender = new Thread(() -> {

			try {
				while (!isExit) {
					sendMessageToClients();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}, "Sender");
		sender.start();
	}
	
	private void sendMessageToClients() throws InterruptedException{
		if (!messageQueue.isEmpty()) {
			for (ConnectionHandler connection : listConnection) {
				if (connection.isAlive()) {
					connection.send(messageQueue.peek());
				} else {
					listConnection.remove(connection);
				}
			}
			messageQueue.poll();
		}
		Thread.sleep(100);
	}

	public void start() throws IOException {
		startSender();
		listenConnections();
	}

	private void showServerInfo() {
		System.out.println(new StringBuilder("Server Address: ").append("\n\t Port: ").append(inetSocket.getPort())
				.append("\n\t Address: ").append(inetSocket.getAddress()));

	}
}