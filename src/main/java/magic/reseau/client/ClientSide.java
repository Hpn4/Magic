package magic.reseau.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import magic.logic.utils.Paint;
import magic.reseau.AbstractGameMaster;
import magic.reseau.Packet;

public class ClientSide extends AbstractGameMaster {

	private final int port;

	private String address;

	public ClientSide() {
		port = 2112;
		try {
			address = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			address = "";
			e.printStackTrace();
		}
	}

	public ClientSide(final int port, final String address) {
		this.port = port;
		this.address = address;
	}

	public void run() {
		try {
			final Socket socket = new Socket(InetAddress.getByName(address), port);

			oos = new ObjectOutputStream(socket.getOutputStream());
			final ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

			doEvent(0);
			while (true) {
				final Object stream = ois.readObject();

				if (stream instanceof Packet part) {
					System.out.println(Paint.MAGENTA + "[CLIENT] : reçue : " + stream.toString() + Paint.RESET);
					doPart(part, false, null);
				} else if (stream.toString().equals("exit")) {
					oos.writeObject("exit");
					doEvent(1);
					break;
				}
			}

			ois.close();
			oos.close();
			socket.close();

			System.out.println(Paint.MAGENTA + "[CLIENT] : connexion fermé" + Paint.RESET);
		} catch (final IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
