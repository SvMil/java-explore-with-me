package ru.practicum.ewm.category;


import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.category.dto.CategoryDto;

import java.util.List;


@RequestMapping("/categories")
@RestController
@RequiredArgsConstructor
public class CategoryControllerAll {
    private final CategoryService service;

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(
            @PathVariable long catId
    ) {
        return service.getById(catId);
    }

    @GetMapping
    public List<CategoryDto> getCategories(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getAll(from, size);
    }
}
