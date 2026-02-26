package ru.practicum.ewm.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRequestDto {

    @NotBlank
    @Email
    @Size(min = 6, max = 255)
    private String email;

    @NotBlank
    @Size(min = 2, max = 255)
    private String name;
}
