package com.c1se22.publiclaundsmartsystem.controller;

import com.c1se22.publiclaundsmartsystem.entity.WashingType;
import com.c1se22.publiclaundsmartsystem.service.WashingTypeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/washing-types")
@AllArgsConstructor
public class WashingTypeController {
    WashingTypeService washingTypeService;
    @GetMapping
    public ResponseEntity<List<WashingType>> getAllWashingTypes(){
        return ResponseEntity.ok(washingTypeService.getAllWashingTypes());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<WashingType> addWashingType(@RequestBody @Valid WashingType washingType){
        return ResponseEntity.ok(washingTypeService.addWashingType(washingType));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<WashingType> updateWashingType(@PathVariable Integer id,
                                                         @RequestBody @Valid WashingType washingType){
        return ResponseEntity.ok(washingTypeService.updateWashingType(id, washingType));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<String> deleteWashingType(@PathVariable Integer id){
        washingTypeService.deleteWashingType(id);
        return ResponseEntity.ok("Washing type deleted successfully");
    }
}
