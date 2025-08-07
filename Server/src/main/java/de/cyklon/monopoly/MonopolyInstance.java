package de.cyklon.monopoly;

import de.cyklon.monopoly.server.GameListener;
import de.cyklon.monopoly.server.GameServer;
import de.cyklon.monopoly.server.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Game instance
 */
public class MonopolyInstance implements GameListener {

    private static final int startCurrent = 1500;

    private final GameServer server;
    private final Random random = new Random();

    private List<Field> fields;
    private final List<Player> players = new ArrayList<>();
    private boolean running = false;
    private UUID currentPlayer;

    public MonopolyInstance(Server server) {
        this.server = server.getServer(UUID.randomUUID());
    }

    private void reset() {
        this.fields = Field.getFields();
        players.forEach(p -> {
            p.setCurrent(startCurrent);
            p.setCurrentField(0);
        });
    }

    private int nextPlayer(UUID id) {
        int i = players.indexOf(new Player(id, "", false, 0)) + 1;
        if (i < players.size()) return i;
        return 0;
    }

    private Player getPlayer(UUID id) {
        return players.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }

    private boolean checkName(String name) {
        return players.stream().anyMatch(p -> p.getName().equalsIgnoreCase(name));
    }

    @Override
    public boolean onJoin(String name) {
        if (checkName(name)) return false;

        players.add(new Player(UUID.randomUUID(), name, running, 0));

        return true;
    }

    private void movePlayer(Player player, int steps) {
        int f = player.getCurrentField() + steps;
        if (f >= fields.size()) f = f % fields.size();
        player.setCurrentField(f);
        server.move(player.getId(), f);
    }

    @Override
    public void onRoll(UUID id) {
        if (id != currentPlayer) return;

        int num1 = random.nextInt(6)+1;
        int num2 = random.nextInt(6)+1;

        server.showDice(id, num1, num2);

        Player player = getPlayer(id);
        movePlayer(player, num1 + num2);

        Field field = fields.get(player.getCurrentField());
        if (field.getType().getGroup().equals(Field.Type.Group.ACTION)) {
            Action action = Action.ACTIONS.get(random.nextInt(Action.ACTIONS.size()));
            action.run(players, player);
            server.sendDescription(player.getId(), player.getCurrentField(), action.getDescription());
        } else {
            Player owner = field.getOwner();
            if (!player.equals(owner)) {
                if (owner == null) server.buyDialog(player.getId(), field.getId());
                else {
                    int rent = field.getRent();
                    player.addCurrent(rent*-1);
                    owner.addCurrent(rent);
                }
            }
        }

        server.showEndTurnBtn(id);
    }

    @Override
    public void onStart(UUID id) {
        if (running || players.size() < 2) return;
        running = true;
        reset();
        currentPlayer = players.get(0).getId();
        server.startTurn(currentPlayer);
    }

    @Override
    public void onEndTurn(UUID id) {
        if (id != currentPlayer) return;
        currentPlayer = players.get(nextPlayer(id)).getId();
        server.startTurn(currentPlayer);
    }

    @Override
    public void onBuy(UUID id, int fid) {
        Field field = fields.get(fid);
        if (field == null || field.getOwner() != null) return;
        int price = field.getType().getPrice();
        Player player = getPlayer(id);
        if (player.getCurrent() < price) return;
        player.addCurrent(price*-1);
        field.setOwner(player);
    }

}
