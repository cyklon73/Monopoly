package de.cyklon.monopoly;

import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

public class Action {

    private static final Random random = new Random();

    public static final List<Action> ACTIONS = List.of(
            new Action("Du hast dein Berichtsheft nicht abgegeben, Zahle %s€ Strafe", payAction(30, 40, 50, 60)),
            new Action("Du hast dass Einstandsessen nicht bezahlt, Zahle an alle %s€", payAllAction(40, 50, 60, 70, 80)),
            new Action("Du hast deine Stempelkarte vergessen, Zahle %s€ Strafe", payAction(50)),
            new Action("Deine Schaltung wurde wegen einem unsauberen rundbund abgezwickt, zurück auf Start", moveAction(Field.START)),
            new Action("Du hast die Betriebsversammlung verpasst, Zahle %s€ Strafe", payAction(40, 50, 60, 70, 80)),
            new Action("Du hast dich nicht zum Rauchen abgestempelt, du wurdest ins Ausbilderbüro geschickt", moveAction(Field.PRISON))
    );

    private final String description;
    private final BiFunction<List<Player>, Player, Object[]> action;

    private String displayDescription;

    private Action(String description, BiFunction<List<Player>, Player, Object[]> action) {
        this.description = description;
        this.displayDescription = description;
        this.action = action;
    }

    public String getRawDescription() {
        return description;
    }

    public String getDescription() {
        return displayDescription;
    }

    public void run(List<Player> players, Player player) {
        displayDescription = description.formatted(action.apply(players, player));
    }

    private static BiFunction<List<Player>, Player, Object[]> payAction(int... prices) {
        if (prices.length == 0) throw new IllegalArgumentException("prices cannot be empty");
        return (players, player) -> {
            int price = prices.length==1 ? prices[0] : prices[random.nextInt(prices.length)];
            player.addCurrent(-price);
            return new Object[] {price};
        };
    }

    private static BiFunction<List<Player>, Player, Object[]> payAllAction(int... prices) {
        if (prices.length == 0) throw new IllegalArgumentException("prices cannot be empty");
        return (players, player) -> {
            int price = prices.length==1 ? prices[0] : prices[random.nextInt(prices.length)];
            players.stream()
                    .filter(p -> !p.equals(player))
                    .forEach(p -> {
                        p.addCurrent(price);
                        player.addCurrent(-price);
            });
            return new Object[] {price};
        };
    }

    private static BiFunction<List<Player>, Player, Object[]> moveAction(int field) {
        return (players, player) -> {
            player.setCurrentField(field);
            return new Object[0];
        };
    }
}
