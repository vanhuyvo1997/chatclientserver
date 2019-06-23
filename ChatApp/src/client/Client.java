package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	private Thread receiver, sender;
	private Scanner sc;
	private String message;
	private String name = "";
	private int port;
	private String hostName;
	private boolean isReconnected;

	public Client(String hostName, int port, String name) throws UnknownHostException, IOException {
		super();
		init(hostName, port, name);

	}

	private void createSender() {
		sender = new Thread(() -> {
			while (!socket.isClosed()) {
				sc = new Scanner(System.in);
				message = sc.nextLine();
				message = message.trim();
				if (!message.equals("") && !message.equals("\r")) {
					if (message.equals("exit")) {
						exit();
					} else {
						send(message);
					}
				}
			}
		}, "Sender");
	}

	private void exit() {
		try {
			out.write("exit");
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void send(String message2) {
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
				while (!socket.isClosed()) {
					try {
						message = decodeReceivedMessage(in.readLine());
						if (message != null && !message.equals("\r") && !message.equals("")) {
							System.out.println(message);
						}
					} catch (IOException e) {
						disconnect();
						System.out.println("Server has been not found!");
						reconnect();
					} catch (NullPointerException e) {
						disconnect();
						System.out.println("Server has been not found!");
						reconnect();
					}
				}
			}

		}, "Receiver");
	}

	private void disconnect() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void init(String hostName, int port, String name) throws UnknownHostException, IOException {
		isReconnected = false;
		message = null;
		this.name = name;
		this.port = port;
		this.hostName = hostName;
		socket = new Socket(hostName, port);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		createSender();
		createReceiver();
	}

	private String encodeSendedMessage(String message) {
		return (new StringBuilder(socket.toString()).append("@").append(this.name).append(" :").append(message))
				.toString();
	}

	public void reconnect() {
		Thread reconnecter = new Thread() {
			private int count = 0;

			public void run() {
				System.out.println("...reconnecting....");
				while (count < 10 && !isReconnected) {
					try {
						count++;
						System.out.println("...");
						init(hostName, port, name);
						isReconnected = true;
					} catch (UnknownHostException e) {
						System.out.println("..Unknownhost...");
					} catch (IOException e) {
						System.out.println("..Can not IO...");
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (isReconnected) {
					System.out.println("Reconnect success.");
					try {
						Client.this.start();
					} catch (IOException e) {
						System.out.println("Can not reconnect!");
					} catch (IllegalThreadStateException e) {
						try {
							Client client = new Client(Client.this.hostName, Client.this.port, Client.this.name);
							client.start();
						} catch (ConnectException e1) {
							System.out.println("Can not reconnect, please restart apllications! ^^");
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				} else
					System.out.println("Reconnect fail.");
			}
		};
		reconnecter.start();
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
		receiver.start();
		sender.start();
	}
}
