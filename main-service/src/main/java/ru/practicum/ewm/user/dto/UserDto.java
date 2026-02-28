package ru.practicum.ewm.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class UserDto {

    @Email
    @Length(max = 254)
    private String email;
    private Long id;

    @NotBlank
    @Size(min = 2, max = 250)
    private String name;
}
