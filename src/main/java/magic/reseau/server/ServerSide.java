package magic.reseau.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import magic.logic.utils.Paint;
import magic.reseau.AbstractGameMaster;
import magic.reseau.Packet;

public class ServerSide extends AbstractGameMaster {

	private ServerSocket server;

	private boolean running;

	public ServerSide() {
		try {
			server = new ServerSocket(2112, 10, InetAddress.getLocalHost());
			running = true;
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
	
	public ServerSide(final int port, final String adress) {
		try {
			server = new ServerSocket(port, 10, InetAddress.getByName(adress));
			running = true;
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		System.out.println(Paint.GREEN + "[SERVER] : server lance" + Paint.RESET);
		while (running) {

			try {
				final Socket client = server.accept();

				oos = new ObjectOutputStream(client.getOutputStream());
				final ObjectInputStream ois = new ObjectInputStream(client.getInputStream());

				System.out.println(Paint.GREEN + "[SERVER] : connexion accepter" + Paint.RESET);
				doEvent(0);
				while (true) {
					final Object stream = ois.readObject();

					if (stream instanceof Packet part) {
						System.out.println(Paint.GREEN + "[SERVER] : reçue : " + stream.toString() + Paint.RESET);
						doPart(part, false, null);
					} else if (stream.toString().equals("exit")) {
						doEvent(1);
						break;
					}
				}

				ois.close();
				oos.close();
				client.close();
				server.close();
				running = false;

				System.out.println(Paint.GREEN + "[SERVER] : server stoppé" + Paint.RESET);
			} catch (final IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {
		try {
			oos.writeObject("exit");
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
