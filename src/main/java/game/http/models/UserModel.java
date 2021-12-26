package game.http.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserModel implements HttpModel {
    @JsonProperty("Username")
    private String username;

    @JsonProperty("Password")
    private String password;

    @JsonProperty("Name")
    private String displayName;
    @JsonProperty("Bio")
    private String bio;

    @JsonProperty("Image")
    private String image;

}
