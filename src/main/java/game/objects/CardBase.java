package game.objects;

import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;
import game.objects.enums.RarityEnum;
import lombok.Data;

import java.util.Objects;
import java.util.UUID;


@Data
public abstract class CardBase implements Comparable<CardBase> {
    public static final double STRENGTHEN = 2;
    public static final double WEAKEN = 0.5;

    private UUID id;

    private String name;
    private String desc;
    private final double damage;
    private ElementType elementType;

    private int adminPackageNumber = 0;
    private boolean inTradeInvolved = false;

    public final RarityEnum cardRarity;

    public CardBase(String name, double damage, ElementType elementType, RarityEnum cardRarity) {
        this(name, damage, elementType, cardRarity, "No description available");
    }

    public CardBase(String name, double damage, ElementType elementType, RarityEnum cardRarity, String desc) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.damage = damage;
        this.elementType = elementType;
        this.cardRarity = cardRarity;
        this.desc = desc;
    }

    public abstract FightOutcome attack(CardBase competitor);

    public void setId(String id) {
        this.id = UUID.fromString(id);
    }

    protected FightOutcome fightFire(CardBase competitor) {
        return switch (getElementType()) {
            case WATER -> getFightOutcomeForDamage(getDamage() * STRENGTHEN, competitor.getDamage() * WEAKEN);
            case FIRE -> getFightOutcomeForDamage(getDamage(), competitor.getDamage());
            case NORMAL -> getFightOutcomeForDamage(getDamage() * WEAKEN, competitor.getDamage() * STRENGTHEN);
        };
    }

    protected FightOutcome fightWater(CardBase competitor) {
        return switch (getElementType()) {
            case WATER -> getFightOutcomeForDamage(getDamage(), competitor.getDamage());
            case FIRE -> getFightOutcomeForDamage(getDamage() * WEAKEN, competitor.getDamage() * STRENGTHEN);
            case NORMAL -> getFightOutcomeForDamage(getDamage() * STRENGTHEN, competitor.getDamage() * WEAKEN);
        };
    }

    protected FightOutcome fightNormal(CardBase competitor) {
        return switch (getElementType()) {
            case WATER -> getFightOutcomeForDamage(getDamage() * WEAKEN, competitor.getDamage() * STRENGTHEN);
            case FIRE -> getFightOutcomeForDamage(getDamage() * STRENGTHEN, competitor.getDamage() * WEAKEN);
            case NORMAL -> getFightOutcomeForDamage(getDamage(), competitor.getDamage());
        };
    }

    private FightOutcome getFightOutcomeForDamage(double damageAttacker, double damageDefender) {
        if (damageAttacker == damageDefender) return FightOutcome.TIE;
        return damageAttacker > damageDefender ? FightOutcome.ATTACKER : FightOutcome.DEFENDER;
    }

    protected FightOutcome attackSpell(CardBase competitor) {
        return switch (competitor.getElementType()) {
            case FIRE -> fightFire(competitor);
            case WATER -> fightWater(competitor);
            case NORMAL -> fightNormal(competitor);
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CardBase)) return false;
        CardBase cardBase = (CardBase) o;
        return Objects.equals(id.toString(), cardBase.id.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public int compareTo(CardBase anotherCards) {
        return this.name.compareTo(anotherCards.getName());
    }

    public String toPlainString() {
        return "{\"Card\": {\"Name\": \"" + this.name + "\"}}";
    }

    @Override
    public String toString() {
        return "{\"Card\": {\"Id\": \"" + this.id.toString() + "\", \"Name\": \"" + this.name + "\", \"Damage\": " + this.damage + ", \"Elementtype\": \"" + this.elementType.toString() + "\", \"Description\": \"" + this.desc + "\", \"Rarity\": \"" + this.cardRarity.toString() + "\"} }";
    }
}
