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

	private ServerSocket serverSocket;
	private boolean isExit;
	private List<ConnectionHandler> listConnection;
	private Queue<String> messageQueue;
	private Thread sender;
	private Thread closer;
	private InetSocketAddress inetSocket;

	public Server(int port) throws IOException {
		super();
		init(port);
	}

	private void init(int port) throws IOException {
		isExit = false;
		listConnection = new LinkedList<>();
		messageQueue = new PriorityQueue<>();
		serverSocket = new ServerSocket();
		inetSocket = new InetSocketAddress(InetAddress.getLocalHost(),port);
		serverSocket.bind(inetSocket);
		startSender();
		startCloser();
	}

	private void startCloser() {
		closer = new Thread() {
			Scanner sc;

			public void run() {
				while (!isExit) {
					sc = new Scanner(System.in);
					String statement = sc.nextLine();

					if (statement.equals("exit")) {
						System.out.print(statement);
						try {
							exit();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}
			}
		};
		closer.start();
	}

	private void exit() throws IOException {
		for (ConnectionHandler connection : listConnection) {
			connection.disconnect();
		}
		listConnection.clear();
		isExit = true;
	}

	private void startSender() {
		sender = new Thread() {
			public void run() {
				while (!isExit) {
					try {
						if (messageQueue.size() > 0) {
							for (ConnectionHandler connection : listConnection) {
								if (connection.isAlive())
									connection.send(messageQueue.peek());
							}
							messageQueue.poll();
						}
						currentThread();
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		sender.start();
	}

	public void start() throws IOException {
		System.out.println("Server have been started...");
		showServerInfo();
		
		while (!isExit) {
			Socket socket = serverSocket.accept();
			ConnectionHandler connection = new ConnectionHandler(socket, messageQueue);
			connection.start();
			connection.send(socket.toString() + "@Connected to :" + socket.toString());
			listConnection.add(connection);
			System.out.println("New clients connect:" + socket);
		}
		System.out.println("...server have been closed!");
	}

	private void showServerInfo() {
		System.out.println(
			new StringBuilder("Server Address: ")
			.append("\n\t Port: ").append(inetSocket.getPort())
			.append("\n\t Host name: ").append(inetSocket.getHostName())
			.append("\n\t Address: ").append(inetSocket.getAddress())
			.append("\n\t Canonical Host Name: ").append(inetSocket.getAddress().getCanonicalHostName())
		);
	}
}
