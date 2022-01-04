package game.objects;

import game.objects.card.factory.CardFactory;
import game.objects.enums.CardsEnum;
import game.objects.enums.RarityEnum;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A package is a list of 5 cards that cost 20 coins. The list can include duplicates.
 */
public class Package {
    public final List<CardBase> cards;
    public static final int PACKAGE_COST = 4;
    public static final int PACKAGE_SIZE = 5;

    public Package(List<CardBase> cards) {
        if (cards.size() == PACKAGE_SIZE) {
            this.cards = cards;
        } else {
            this.cards = this.generateCardList();
        }
    }

    public Package() {
        this.cards = this.generateCardList();
    }

    /**
     * Generates a list of 5 ({@link Package#PACKAGE_SIZE}) cards. Can include duplicates.
     *
     * @return an unmodifiable list with a size of {@link Package#PACKAGE_SIZE} and random selected cards depending on the {@link RarityEnum}
     */
    private List<CardBase> generateCardList() {
        List<CardBase> generatedCards = new ArrayList<>(PACKAGE_SIZE);

        Random rand = new Random();
        for (int i = 0; i < PACKAGE_SIZE; i++) {
            int rarity = rand.nextInt(RarityEnum.MAX_RARITY + 1);
            // filter cards for rarity
            List<CardsEnum> cardsForRarity = Arrays.stream(CardsEnum.values())
                    .filter(cardsEnum -> cardsEnum.getRarity().getRarityStart() <= rarity && rarity <= cardsEnum.getRarity().getRarityEnd())
                    .collect(Collectors.toList());
            // Create card and add to list (with no id specified)

            generatedCards.add(CardFactory.createCard(cardsForRarity.get(rand.nextInt(cardsForRarity.size())), null));

        }

        return Collections.unmodifiableList(generatedCards);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        if (!this.cards.isEmpty()) {
            sb.append("\"Package\": ");
            sb.append(this.cards.size() > 1 ? "[" : "");
            for (int i = 0; i < this.cards.size(); i++) {
                sb.append(this.cards.get(i).toString());
                if (i < this.cards.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(this.cards.size() > 1 ? "]" : "");
        } else {
            sb.append("\"Package\": \"Empty\"");
        }
        sb.append("}");
        return sb.toString();
    }
}
