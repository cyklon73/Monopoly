package de.cyklon.monopoly.server;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class Server implements Closeable {

	private final ServerSocket server;
	@Getter
	private boolean running = true;

	private final List<ClientHandler> clients = new ArrayList<>();

	public Server(int port) throws IOException {
		this.server = new ServerSocket(port);

		while (running) {
			try {
				Socket socket = server.accept();
				try {
					log.debug("[Server] initializing Client %s".formatted(socket.getInetAddress()));
					ClientHandler client = new ClientHandler(this, socket);
					log.info("[Server] Client %s (%s) successfully connected!".formatted(client.getId(), socket.getInetAddress()));
					clients.add(client);
				} catch (IOException e) {
					log.warn("[Server] Exception while connecting client %s".formatted(socket.getInetAddress()), e);
				}
			} catch (IOException e) {
				log.warn("[Server] Exception while listening for connections", e);
			}
		}
	}

	public GameServer getServer(UUID gameId) {
		return null;
	}


	@Override
	public void close() throws IOException {
		if (!running) return;
		running = false;
		server.close();
	}
}
