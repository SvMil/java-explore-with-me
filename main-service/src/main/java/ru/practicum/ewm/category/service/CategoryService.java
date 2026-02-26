package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CreateCategoryDto;
import ru.practicum.ewm.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getAll(int from, int size);

    CategoryDto getById(Long id);

    CategoryDto create(CreateCategoryDto dto);

    CategoryDto update(Long id, CategoryDto dto);

    void delete(Long id);
}
