package com.travelagency.domain.dto;

import java.time.LocalDate;

public record TravelerView(
        Long id,
        String name,
        String gender,
        LocalDate birthDate,
        String idType,
        String idNoMasked,
        String phone,
        String emergencyName,
        String emergencyPhone) {
}
