package game.objects.monstercards;

import game.objects.CardBase;
import game.objects.enums.CardsEnum;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;

public class FireElf extends MonsterCard {
    public FireElf() {
        super("Fire Elf", 10, ElementType.FIRE, CardsEnum.FIRE_ELF.getRarity(),
                "Fire elves have known dragons since they were little and can dodge their attacks.");
    }

    @Override
    /*
     * Fire elves have known dragons since they were little and can dodge their attacks.
     */
    public FightOutcome attack(CardBase competitor) {
        if (competitor instanceof Dragon) {
            return FightOutcome.ATTACKER;
        }
        return super.attack(competitor);
    }
}
