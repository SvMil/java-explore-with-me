package ru.practicum.ewm.category.mapper;

import org.springframework.stereotype.Component;
import org.mapstruct.Mapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.dto.CategoryDto;

@Mapper(componentModel = "spring")
@Component
public interface CategoryMapper {

    Category toEntity(CategoryDto dto);

    CategoryDto toDto(Category category);
}
