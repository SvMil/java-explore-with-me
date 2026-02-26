package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CreateCompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getAll(Boolean pinned, int from, int size);

    CompilationDto getById(Long id);

    CompilationDto create(CreateCompilationDto dto);

    CompilationDto update(Long id, UpdateCompilationRequest dto);

    void delete(Long id);
}
