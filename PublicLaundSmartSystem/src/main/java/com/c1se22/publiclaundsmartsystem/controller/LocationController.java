package com.c1se22.publiclaundsmartsystem.controller;

import com.c1se22.publiclaundsmartsystem.payload.response.LocationDetailsDto;
import com.c1se22.publiclaundsmartsystem.payload.request.LocationSummaryDto;
import com.c1se22.publiclaundsmartsystem.service.LocationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@AllArgsConstructor
public class LocationController {
    LocationService locationService;
    @GetMapping
    public ResponseEntity<List<LocationSummaryDto>> getAllLocations(){
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDetailsDto> getLocationById(@PathVariable Integer id){
        return ResponseEntity.ok(locationService.getLocationById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<LocationSummaryDto> addLocation(@RequestBody @Valid LocationSummaryDto locationSummaryDto){
        return ResponseEntity.ok(locationService.addLocation(locationSummaryDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<LocationSummaryDto> updateLocation(@PathVariable Integer id,
                                                             @RequestBody @Valid LocationSummaryDto locationSummaryDto){
        return ResponseEntity.ok(locationService.updateLocation(id, locationSummaryDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteLocation(@PathVariable Integer id){
        locationService.deleteLocation(id);
        return ResponseEntity.ok("Delete location successfully!");
    }
}
