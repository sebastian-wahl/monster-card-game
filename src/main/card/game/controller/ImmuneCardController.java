package card.game.controller;

import card.game.objects.CardBase;
import card.game.objects.monstercards.Kraken;

public class ImmuneCardController {
    public static boolean isCardImmuneAgainstSpells(CardBase card) {
        if (card instanceof Kraken) return true;
        // Possibility to add new immune monsters
        return false;
    }
}
