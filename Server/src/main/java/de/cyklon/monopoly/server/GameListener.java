package de.cyklon.monopoly.server;

import java.util.UUID;

public interface GameListener {

	boolean onJoin(String name);

	void onRoll(UUID id);

	void onStart(UUID id);

	void onEndTurn(UUID id);

	void onBuy(UUID id, int field);
}
