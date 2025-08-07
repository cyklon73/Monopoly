package de.cyklon.monopoly;

import java.util.Arrays;
import java.util.List;

public class Field {

    public static final int START = 0;
    public static final int PRISON = 10;
    public static final int VACATION = 20;
    public static final int GOTO_PRISON = 30;

    private final Type type;
    private Player owner;
    private int houses;

    public Field(Type type) {
        this.type = type;
        this.owner = null;
        this.houses = 0;
    }

    public Type getType() {
        return type;
    }

    public Player getOwner() {
        return owner;
    }

    public int getHouses() {
        return houses;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public void setHouses(int houses) {
        this.houses = houses;
    }

    public int getId() {
        return type.ordinal();
    }

    public int getRent() {
        double factor = switch (houses) {
            case 0 -> 0.1;
            case 1 -> 0.6;
            case 2 -> 1.5;
            case 3 -> 4.5;
            case 4 -> 6.25;
            case 5 -> 10;
            default -> throw new IllegalStateException("Unexpected state while rent calculation");
        };
        return (int)Math.round(type.getPrice() * factor);
    }

    public int getHousePrice() {
        return ((int)Math.ceil((type.getPrice()*0.4) / 10)) * 10;
    }


    public static List<Field> getFields() {
        return Arrays.stream(Type.values())
                .map(Field::new)
                .toList();
    }

    public enum Type {

        LWS_MECH("Mechanik Lehrwerkstatt", Group.LWS, 0),
        LWS_ELEK("Elektronik Lehrwerkstatt", Group.LWS, 0),
        LWS_BLECH("Blechbearbeitung Lehrwerkstatt", Group.LWS, 0);

        private final String name;
        private final Group group;
        private final int price;

        Type(String name, Group group, int price) {
            this.name = name;
            this.group = group;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public Group getGroup() {
            return group;
        }

        public int getPrice() {
            return price;
        }

        public enum Group {
            QS,
            LWS,
            SSB,
            ASM, //Montage

            FABRIC,
            ACTION
        }
    }
}
