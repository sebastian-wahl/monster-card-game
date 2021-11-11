package card.game.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Pack {
    private List<CardBase> packCards;
}
