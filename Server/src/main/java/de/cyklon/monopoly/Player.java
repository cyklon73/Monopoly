package de.cyklon.monopoly;

import java.util.UUID;

public class Player {

    private final UUID id;
    private final String name;
    private final boolean spectator;
    private int current;
    private int currentField = 0;


    public Player(UUID id, String name, boolean spectator, int current) {
        this.id = id;
        this.name = name;
        this.spectator = spectator;
        this.current = current;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isSpectator() {
        return spectator;
    }

    public int getCurrent() {
        return current;
    }

    public int getCurrentField() {
        return currentField;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public void addCurrent(int current) {
        this.current += current;
    }

    public void setCurrentField(int currentField) {
        this.currentField = currentField;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Player && ((Player) obj).id.equals(id);
    }
}
