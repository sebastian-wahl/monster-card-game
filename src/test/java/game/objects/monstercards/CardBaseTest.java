package game.objects.monstercards;

import game.objects.CardBase;
import game.objects.enums.FightOutcome;
import game.objects.spellcards.DarkSpell;
import game.objects.spellcards.FireSpell;
import game.objects.spellcards.SlownessSpell;
import game.objects.spellcards.WaterSpell;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CardBaseTest {
    // winner, loser
    private static Stream<Arguments> provideMonstersMonsterFight() {
        return Stream.of(
                Arguments.of(new DarkEnt(), new DarkBat()),
                Arguments.of(new Dragon(), new Ork()),
                Arguments.of(new Knight(), new FireElf()),
                Arguments.of(new Dragon(), new WaterWitch())
        );
    }

    @ParameterizedTest(name = "{0} VS {1}")
    @DisplayName("Test if monster fights work")
    @MethodSource("provideMonstersMonsterFight")
    void attackTestMonsterFights(CardBase winner, CardBase loser) {
        FightOutcome fo = winner.attack(loser);
        assertThat(fo).isEqualTo(FightOutcome.ATTACKER);
        fo = loser.attack(winner);
        assertThat(fo).isEqualTo(FightOutcome.DEFENDER);
    }

    @Test
    void attackTestMonsterFightsTie() {
        CardBase card1 = new Dragon();
        CardBase card2 = new Dragon();
        assertThat(card1.attack(card2)).isEqualTo(FightOutcome.TIE);
    }

    @Test
    void attackTestSpellFightsTie() {
        CardBase card1 = new FireSpell();
        CardBase card2 = new FireSpell();
        assertThat(card1.attack(card2)).isEqualTo(FightOutcome.TIE);
    }

    @Test
    void attackTestElements() {
        CardBase fireCard = new FireWizard();
        CardBase waterCard = new WaterSpell();
        CardBase normalCard = new DarkSpell();

        // Normal VS Water -> normal
        FightOutcome fo = normalCard.attack(waterCard);
        assertThat(fo).isEqualTo(FightOutcome.ATTACKER);
        fo = waterCard.attack(normalCard);
        assertThat(fo).isEqualTo(FightOutcome.DEFENDER);

        // Fire VS Water -> water
        fo = fireCard.attack(waterCard);
        assertThat(fo).isEqualTo(FightOutcome.DEFENDER);
        fo = waterCard.attack(fireCard);
        assertThat(fo).isEqualTo(FightOutcome.ATTACKER);

        // Fire VS Normal -> fire
        fo = fireCard.attack(normalCard);
        assertThat(fo).isEqualTo(FightOutcome.ATTACKER);
        fo = normalCard.attack(fireCard);
        assertThat(fo).isEqualTo(FightOutcome.DEFENDER);
    }

    // Arguments.of(winning card, losing card)
    private static Stream<Arguments> provideMonstersForSpecifications() {
        return Stream.of(
                Arguments.of(new DarkBat(), new SlownessSpell()),
                Arguments.of(new DarkEnt(), new Ork()),
                Arguments.of(new DarkEnt(), new Knight()),
                Arguments.of(new Dragon(), new GreyGoblin()),
                Arguments.of(new FireElf(), new Dragon()),
                Arguments.of(new FireWizard(), new Ork()),
                Arguments.of(new Kraken(), new WaterSpell()),
                Arguments.of(new Kraken(), new FireSpell()),
                Arguments.of(new WaterWitch(), new FireWizard()),
                Arguments.of(new WaterWitch(), new WaterSpell()),
                Arguments.of(new WaterWitch(), new FireSpell()),
                Arguments.of(new WaterSpell(), new Knight())
        );
    }

    @ParameterizedTest(name = "{0} VS {1}")
    @DisplayName("Test if monster specifications work")
    @MethodSource("provideMonstersForSpecifications")
    void attackTestMonsterSpecifications(CardBase winner, CardBase loser) {
        // Card1 is always the winner
        FightOutcome fo = winner.attack(loser);
        assertThat(fo).isEqualTo(FightOutcome.ATTACKER);
        fo = loser.attack(winner);
        assertThat(fo).isEqualTo(FightOutcome.DEFENDER);
    }
}