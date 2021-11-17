package game.objects;

import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;
import game.objects.enums.RarityEnum;
import lombok.Data;

import java.util.UUID;


@Data
public abstract class CardBase implements Comparable<CardBase> {
    public static final double STRENGTHEN = 2;
    public static final double WEAKEN = 0.5;

    private UUID id;

    private String name;
    private final double damage;
    private ElementType elementType;

    public final RarityEnum cardRarity;

    public CardBase(String name, double damage, ElementType elementType, RarityEnum cardRarity) {
        this.name = name;
        this.damage = damage;
        this.elementType = elementType;
        this.cardRarity = cardRarity;
    }

    public abstract FightOutcome attack(CardBase competitor);

    public FightOutcome fightFire(CardBase competitor) {
        return switch (getElementType()) {
            case WATER -> getFightOutcomeForDamage(getDamage() * STRENGTHEN, competitor.getDamage() * WEAKEN);
            case FIRE -> getFightOutcomeForDamage(getDamage(), competitor.getDamage());
            case NORMAL -> getFightOutcomeForDamage(getDamage() * WEAKEN, competitor.getDamage() * STRENGTHEN);
        };
    }

    public FightOutcome fightWater(CardBase competitor) {
        return switch (getElementType()) {
            case WATER -> getFightOutcomeForDamage(getDamage(), competitor.getDamage());
            case FIRE -> getFightOutcomeForDamage(getDamage() * WEAKEN, competitor.getDamage() * STRENGTHEN);
            case NORMAL -> getFightOutcomeForDamage(getDamage() * STRENGTHEN, competitor.getDamage() * WEAKEN);
        };
    }

    public FightOutcome fightNormal(CardBase competitor) {
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

    public FightOutcome attackSpell(CardBase competitor) {
        return switch (competitor.getElementType()) {
            case FIRE -> fightFire(competitor);
            case WATER -> fightWater(competitor);
            case NORMAL -> fightNormal(competitor);
        };
    }

    @Override
    public int compareTo(CardBase anotherCards) {
        return this.name.compareTo(anotherCards.getName());
    }

    @Override
    public String toString() {
        return "Name: " + this.name + ", damage: " + this.damage + ", elementType: " + this.elementType.toString();
    }
}
