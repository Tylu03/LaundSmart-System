package com.c1se22.publiclaundsmartsystem.service;

import com.c1se22.publiclaundsmartsystem.payload.request.MachineCreateDto;
import com.c1se22.publiclaundsmartsystem.payload.response.MachineAndTimeDto;
import com.c1se22.publiclaundsmartsystem.payload.request.MachineDto;

import java.util.List;

public interface MachineService {
    List<MachineDto> getAllMachines();
    List<MachineDto> getMachines(int page, int size, String sortBy, String sortDir);
    MachineDto getMachineById(Integer id);
    MachineDto addMachine(MachineCreateDto machineDto);
    MachineDto updateMachine(Integer id, MachineDto machineDto);
    void deleteMachine(Integer id);
    MachineDto updateMachineStatus(Integer id, String status);
    MachineDto getMachineAreBeingReservedByUser(String username);
    List<MachineAndTimeDto> getMachinesAreBeingUsedByUser(String username);
    List<MachineDto> getMachinesByOwnerId(Integer id);
    List<MachineDto> getMachinesForCurrentOwner(String username);
    void updateMachineErrorStatus(String secretId);
    void updateMachineActiveStatus(String secretId);
    boolean checkMachineHashKey(String hashKey);
    boolean checkMachineRTStatus(Integer id);
}
