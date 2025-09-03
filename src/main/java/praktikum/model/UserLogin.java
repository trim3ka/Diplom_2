package praktikum.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLogin {
    private String email;
    private String name;
    private String password;

    public static UserLogin from(UserCreated userCreated) {
        return new UserLogin(
                userCreated.getEmail(),
                userCreated.getName(),
                userCreated.getPassword()
        );
    }
}
