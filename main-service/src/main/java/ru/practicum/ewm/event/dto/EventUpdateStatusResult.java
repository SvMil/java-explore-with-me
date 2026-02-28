package ru.practicum.ewm.event.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.request.dto.RequestParticipationDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventUpdateStatusResult {

    private List<RequestParticipationDto> confirmedRequests;
    private List<RequestParticipationDto> rejectedRequests;
}
