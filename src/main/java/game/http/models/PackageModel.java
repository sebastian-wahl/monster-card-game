package game.http.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PackageModel implements HttpModel {
    List<CardModel> packageCards;
}
