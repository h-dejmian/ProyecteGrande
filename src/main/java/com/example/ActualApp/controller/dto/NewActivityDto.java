package com.example.ActualApp.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;

public record NewActivityDto(
        @NotBlank
        String description,
        @Range(min = 0, max = 1440, message = "Time spent should not be less than 0 and greater than 1440")
        long timeSpentInMinutes,
        @NotNull
        LocalDate date,
        boolean completed,
        String categoryName
) {
}
