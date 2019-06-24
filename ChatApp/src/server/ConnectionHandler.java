package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Queue;

public class ConnectionHandler extends Thread {

	private BufferedReader in;
	private BufferedWriter out;
	private String message;
	private Socket socket;
	private Queue<String> messageQueue;

	public ConnectionHandler(Socket socket, Queue<String> messageQueue) throws IOException {

		super();
		this.socket = socket;
		this.messageQueue = messageQueue;

		in = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), Charset.forName("UTF-8")));
		out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));

		message = null;
	}

	@Override
	public void run() {
		try {
			while (!socket.isClosed()) {
				
				message = in.readLine();
				
				while (message != null && !message.equals("")) {
					messageQueue.add(message);
					message = in.readLine();
				}
			}
		} catch (IOException exIO) {
			try {
				//
				System.out.println(socket + " is disconnected!");
				socket.close();
			} catch (IOException exIO1) {
				exIO1.printStackTrace();
			}
		}
	}

	public void send(String messege) {
		try {
			out.write(messege);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
