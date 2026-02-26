package ru.practicum.ewm.compilation;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequestMapping("/compilations")
@RestController
@RequiredArgsConstructor
public class CompilationControllerAll {
    private final CompilationService service;


    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable long compId) {
        return service.getById(compId);
    }

    @GetMapping
    public List<CompilationDto> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getAll(pinned, from, size);
    }
}
