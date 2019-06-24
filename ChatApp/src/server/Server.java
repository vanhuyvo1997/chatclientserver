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

	private static final int minPortNum = 1024, maxPortNum = 65535;

	private ServerSocket serverSocket;
	private List<ConnectionHandler> listConnection;
	private Queue<String> messageQueue;
	private Thread sender;
//	private Thread closer;
	private InetSocketAddress inetSocket;
	private int port;
	private boolean isExit;

	public Server() throws IOException {
		super();
		init();
		showServerInfo();
		listenConnections();
//		startSender();
//		startCloser();
	}

	private void init() throws IOException {
		listConnection = new LinkedList<>();
		messageQueue = new PriorityQueue<>();
		serverSocket = new ServerSocket();
		typePortNumber();
		inetSocket = new InetSocketAddress(InetAddress.getLocalHost(), port);
		serverSocket.bind(inetSocket);
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
				if (number < Server.minPortNum || number > Server.maxPortNum)
					err = true;
			} catch (Exception e) {
				err = true;
			}
			if (err) {
				System.out.println("Port value must be greater or equal than " + Server.minPortNum
						+ " and lower or equal " + Server.maxPortNum);
			}
		} while (err);
		port = number;
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

	private void initSenderThread() {
		try {
			while (!isExit) {
				if (messageQueue.size() > 0) {
					for (ConnectionHandler connection : listConnection) {
						if (connection.isAlive()) {
							connection.send(messageQueue.peek());
						} else
							listConnection.remove(connection);

					}
					messageQueue.poll();
				}
				Thread.sleep(500);

			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//
	private void startSender() {
		sender = new Thread(() -> {
			initSenderThread();
		}, "Sender");
		sender.start();
	}

//	private void startCloser() {
//		closer = new Thread() {
//			Scanner sc;
//
//			public void run() {
//				while (!isExit) {
//					sc = new Scanner(System.in);
//					String statement = sc.nextLine();
//
//					if (statement.equals("exit")) {
//						System.out.print(statement);
//						try {
//							exit();
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//
//				}
//			}
//		};
//		closer.start();
//	}
//
//	private void exit() throws IOException {
//		for (ConnectionHandler connection : listConnection) {
//			connection.disconnect();
//		}
//		listConnection.clear();
//		isExit = true;
//	}
//
//
//
	public void start() throws IOException {
		startSender();
	}

	private void showServerInfo() {
		System.out.println(new StringBuilder("Server Address: ").append("\n\t Port: ").append(inetSocket.getPort())
				.append("\n\t Host name: ").append(inetSocket.getHostName()).append("\n\t Address: ")
				.append(inetSocket.getAddress()).append("\n\t Canonical Host Name: ")
				.append(inetSocket.getAddress().getCanonicalHostName()));
	}
}