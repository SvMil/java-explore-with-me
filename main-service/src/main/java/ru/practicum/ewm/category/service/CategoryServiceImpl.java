package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CreateCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.repository.CategoryRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;

    @Override
    public List<CategoryDto> getAll(int from, int size) {
        Pageable pageable = PageRequest.of(
                from / size,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        return categoryRepository.findAll(pageable).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        return mapper.toDto(findById(id));
    }

    @Override
    @Transactional
    public CategoryDto create(CreateCategoryDto dto) {
        String name = dto.getName();

        if (categoryRepository.existsByName(name)) {
            throw new ConflictException("Категория с заданным именем уже существует");
        }

        Category category = new Category();
        category.setName(name);

        return mapper.toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDto update(Long id, CategoryDto dto) {
        Category category = findById(id);
        String newName = dto.getName();
        category.setName(newName);
        return mapper.toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = findById(id);

        if (eventRepository.findByCategoryId(id, PageRequest.of(0, 1)).hasContent()) {
            throw new ConflictException("Удаление категории с привязанными событиями запрещено");
        }

        categoryRepository.delete(category);
    }

    private Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с заданным id не найдена"));
    }
}
