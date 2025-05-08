package com.c1se22.publiclaundsmartsystem.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationSummaryDto {
    private Integer id;
    @NotBlank(message = "Location name is not blank")
    @NotNull(message = "Location name is required")
    private String name;
    @NotBlank(message = "Location address is not blank")
    @NotNull(message = "Location address is required")
    private String address;
    @NotBlank(message = "Location city is not blank")
    @NotNull(message = "Location city is required")
    private String city;
    @NotBlank(message = "Location district is not blank")
    @NotNull(message = "Location district is required")
    private String district;
    @NotBlank(message = "Location ward is not blank")
    @NotNull(message = "Location ward is required")
    private String ward;
    private Integer machineCount;
    private Double lat;
    private Double lng;
    private List<Integer> machineIds;
}
