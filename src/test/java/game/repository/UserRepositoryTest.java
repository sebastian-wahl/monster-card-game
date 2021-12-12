package game.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        this.userRepository = new UserRepository();
    }

    @Test
    void testCalculateNewElo() {
        double eloWinner = this.userRepository.calculateNewEloWinner(2600, 2300);
        double eloLoser = this.userRepository.calculateNewEloLoser(2300, 2600);

        assertThat(eloWinner).isEqualTo(2602.42);
        assertThat(eloLoser).isEqualTo(2298.0);
    }
}