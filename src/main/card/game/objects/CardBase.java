package card.game.objects;

import lombok.Data;

import java.util.UUID;


@Data
public abstract class CardBase {
    private UUID id;

    private String name;
    private final int damage;
    private ElementType elementType;
    private CardType cardType;

    public CardBase(String name, int damage, ElementType elementType, CardType cardType) {
        this.name = name;
        this.damage = damage;
        this.elementType = elementType;
        this.cardType = cardType;
    }

    public abstract FightOutcome attack(CardBase competitor);

    public FightOutcome fightFire(CardBase competitor) {
        return switch (getElementType()) {
            case WATER -> getFightOutcomeForDamage(getDamage() * 2, competitor.getDamage() / 2);
            case FIRE -> getFightOutcomeForDamage(getDamage(), competitor.getDamage());
            case NORMAL -> getFightOutcomeForDamage(getDamage() / 2, competitor.getDamage() * 2);
        };
    }

    public FightOutcome fightWater(CardBase competitor) {
        return switch (getElementType()) {
            case WATER -> getFightOutcomeForDamage(getDamage(), competitor.getDamage());
            case FIRE -> getFightOutcomeForDamage(getDamage() / 2, competitor.getDamage() * 2);
            case NORMAL -> getFightOutcomeForDamage(getDamage() * 2, competitor.getDamage() / 2);
        };
    }

    public FightOutcome fightNormal(CardBase competitor) {
        return switch (getElementType()) {
            case WATER -> getFightOutcomeForDamage(getDamage() / 2, competitor.getDamage() * 2);
            case FIRE -> getFightOutcomeForDamage(getDamage() * 2, competitor.getDamage() / 2);
            case NORMAL -> getFightOutcomeForDamage(getDamage(), competitor.getDamage());
        };
    }

    private FightOutcome getFightOutcomeForDamage(int damageAttacker, int damageDefender) {
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
}
