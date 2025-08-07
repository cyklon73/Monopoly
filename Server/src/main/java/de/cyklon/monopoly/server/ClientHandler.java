package de.cyklon.monopoly.server;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

@Slf4j
public class ClientHandler implements Closeable, Runnable {

	private final Server server;
	private final Socket socket;
	private final DataInputStream in;
	private final DataOutputStream out;
	@Getter
	private final UUID id;

	@Getter
	private boolean connected;


	public ClientHandler(Server server, Socket socket) throws IOException {
		this.id = UUID.randomUUID();
		this.server = server;
		this.socket = socket;
		this.in = new DataInputStream(socket.getInputStream());
		this.out = new DataOutputStream(socket.getOutputStream());
	}

	private String getPrefix() {
		return "[Client %s]".formatted(id);
	}

	@Override
	public void run() {
		while (server.isRunning() && isConnected()) {
			try {
				log.debug("%s Waiting for data".formatted(getPrefix()));

				int len = in.readInt();

				//disconnected
				if (len==-1) close();
				if (len>0) {
					byte[] data = new byte[len];
					if (in.read(data, 0, len)==-1) {
						close();
						continue;
					}
					receive(data);
				}

			} catch (IOException e) {
				log.error("%s Exception while waiting for data".formatted(getPrefix()));
			}
		}
	}

	private void receive(byte[] data) {

	}

	public boolean send(byte[] data) {
		try {
			out.writeInt(data.length);
			out.write(data);
			out.flush();
			return true;
		} catch (IOException e) {
			log.warn("Exception while sending to client %s".formatted(id), e);
		}
		return false;
	}

	@Override
	public void close() throws IOException {
		if (!connected) return;
		connected = false;
		in.close();
		out.close();
		socket.close();
		log.info("Client %s disconnected".formatted(id));
	}
}
