package praktikum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Random;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreated {
    private String email;
    private String name;
    private String password;

    //Метод для генерации случайного пользователя
    public  static UserCreated random() {
        var rnd = new Random();
        return new UserCreated(
                "mva" + rnd.nextInt(200) + "@yandex.ru",
                "mva_" +rnd.nextInt(200),
                "P@ssw0rd111"
        );
    }
}
