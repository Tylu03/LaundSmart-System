package com.c1se22.publiclaundsmartsystem.payload.response;

import com.c1se22.publiclaundsmartsystem.payload.request.MachineDto;
import jakarta.persistence.Column;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationDetailsDto {
    private Integer id;
    private String name;
    private String address;
    private Integer machineCount;
    private Double lat;
    private Double lng;
    private String city;
    private String district;
    private String ward;
    private List<MachineDto> machines;
}
