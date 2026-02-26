package ru.practicum.ewm.stats.exception;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ResponseError {
    String error;
}
