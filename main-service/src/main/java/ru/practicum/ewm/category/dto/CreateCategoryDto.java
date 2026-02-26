package ru.practicum.ewm.category.dto;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateCategoryDto {

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;
}
