package by.free.home.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountDTO {
    @Pattern(regexp = "^[A-Za-z]{3,16}$",
            message = "Wrong format! Use characters (3 - 16)!")
    private String username;
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Za-z])(?=\\S+$).{3,16}$",
            message = "From 3 to 16 symbols, min 1 digit, min 1 symbol, without whitespace!")
    private String password;
    @Pattern(regexp = "^[A-Z]?[a-z]{1,16}$",
            message = "Wrong format! Only characters(1 - 16)!")
    private String firstName;
    @Pattern(regexp = "^[A-Z]?[a-z]{1,16}$",
            message = "Wrong format! Only characters(1 - 16)!")
    private String lastName;
    private Role role;
    private UserStatus userStatus;
    private String createdAt;
}
