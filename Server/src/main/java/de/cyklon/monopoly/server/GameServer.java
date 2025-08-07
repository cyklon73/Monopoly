package de.cyklon.monopoly.server;

import java.util.UUID;

public interface GameServer {

	void showDice(UUID id, int num1, int num2);

	void move(UUID id, int field);

	void startTurn(UUID id);

	void showEndTurnBtn(UUID id);

	void buyDialog(UUID id, int field);

	void sendDescription(UUID id, int field, String description);

}
