package com.c1se22.publiclaundsmartsystem.controller;

import com.c1se22.publiclaundsmartsystem.payload.request.MachineCreateDto;
import com.c1se22.publiclaundsmartsystem.payload.response.MachineAndTimeDto;
import com.c1se22.publiclaundsmartsystem.payload.request.MachineDto;
import com.c1se22.publiclaundsmartsystem.service.MachineService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.c1se22.publiclaundsmartsystem.util.AppConstants.*;

@RestController
@RequestMapping("/api/machines")
@AllArgsConstructor
public class MachineController {
    MachineService machineService;

    @GetMapping
    public ResponseEntity<List<MachineDto>> getMachines(@RequestParam(defaultValue = DEFAULT_PAGE_NUMBER) int page,
                                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
                                                        @RequestParam(defaultValue = DEFAULT_SORT_BY) String sortBy,
                                                        @RequestParam(defaultValue = DEFAULT_SORT_DIR) String sortDir) {
        return ResponseEntity.ok(machineService.getMachines(page, size, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MachineDto> getMachineById(@PathVariable Integer id) {
        return ResponseEntity.ok(machineService.getMachineById(id));
    }

    @PostMapping
    public ResponseEntity<MachineDto> addMachine(@RequestBody @Valid MachineCreateDto machineCreateDto) {
        return ResponseEntity.ok(machineService.addMachine(machineCreateDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<MachineDto> updateMachine(@PathVariable Integer id, @RequestBody @Valid MachineDto machineDto) {
        return ResponseEntity.ok(machineService.updateMachine(id, machineDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<String> deleteMachine(@PathVariable Integer id) {
        machineService.deleteMachine(id);
        return ResponseEntity.ok("Machine deleted successfully!");
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<MachineDto> updateMachineStatus(@PathVariable Integer id, @RequestParam String status) {
        return ResponseEntity.ok(machineService.updateMachineStatus(id, status));
    }

    @GetMapping("/user")
    public ResponseEntity<List<MachineAndTimeDto>> getMachinesAreBeingUsedByUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(machineService.getMachinesAreBeingUsedByUser(userDetails.getUsername()));
    }

    @GetMapping("/user/pending")
    public ResponseEntity<MachineDto> getPendingMachinesByUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(machineService.getMachineAreBeingReservedByUser(userDetails.getUsername()));
    }

    @PostMapping("/error/{id}")
    public ResponseEntity<Boolean> updateMachineErrorStatus(@PathVariable String id) {
        machineService.updateMachineErrorStatus(id);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/active/{id}")
    public ResponseEntity<Boolean> updateMachineActiveStatus(@PathVariable String id) {
        machineService.updateMachineActiveStatus(id);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/owner/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<List<MachineDto>> getMachinesByOwnerId(@PathVariable Integer id) {
        return ResponseEntity.ok(machineService.getMachinesByOwnerId(id));
    }

    @GetMapping("/owner/current")
    @PreAuthorize("hasAnyRole('ROLE_OWNER')")
    public ResponseEntity<List<MachineDto>> getMachinesByCurrentOwner(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(machineService.getMachinesForCurrentOwner(userDetails.getUsername()));
    }

    @PostMapping("/check")
    public ResponseEntity<Boolean> checkMachineHashKey(@RequestBody ObjectNode objectNode) {
        return ResponseEntity.ok(machineService.checkMachineHashKey(objectNode.get("hashKey").asText()));
    }

    @PostMapping("/check-active/{id}")
    public ResponseEntity<Boolean> checkMachineStatus(@PathVariable Integer id) {
        return ResponseEntity.ok(machineService.checkMachineRTStatus(id));
    }
}
