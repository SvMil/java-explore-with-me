package ru.practicum.ewm.event.dto;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ru.practicum.ewm.enums.RequestStatusUpdateAction;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventUpdateStatusRequest {

    @NotEmpty
    private List<Long> requestIds;

    @NotNull
    private RequestStatusUpdateAction status;
}
