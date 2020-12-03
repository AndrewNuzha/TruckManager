package com.myprojects.truckmanager.truckManagerApp.dto;

import com.myprojects.truckmanager.truckManagerApp.authentication.UniqueNickname;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDTO {

    @NotNull(message = "First Name can not be null")
    @NotEmpty(message = "First Name can not be empty")
    @Size(max = 20, message = "First name must be no more than 20 characters")
    private String firstName;
    @NotNull(message = "Last Name can not be null")
    @NotEmpty(message = "Last Name can not be empty")
    @Size(max = 20, message = "Last name must be no more than 20 characters")
    private String lastName;
    @NotNull(message = "Nickname can not be null")
    @NotEmpty(message = "Nickname can not be empty")
    @Size(min = 5, max = 15, message = "Nickname must be between 5 and 15 characters")
    @UniqueNickname
    private String nickName;
    @NotNull(message = "Email can not be null")
    @NotEmpty(message = "Email can not be empty")
    @Size(max = 25, message = "Email must be no more than 25 characters")
    private String email;
    @NotNull(message = "Password can not be null")
    @NotEmpty(message = "Password can not be empty")
    @Size(min = 5, max = 15, message = "Password must be between 5 and 15 characters")
    private String password;
}
