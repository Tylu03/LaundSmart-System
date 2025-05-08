package com.c1se22.publiclaundsmartsystem.service.impl;

import com.c1se22.publiclaundsmartsystem.entity.WashingType;
import com.c1se22.publiclaundsmartsystem.exception.ResourceNotFoundException;
import com.c1se22.publiclaundsmartsystem.repository.WashingTypeRepository;
import com.c1se22.publiclaundsmartsystem.service.WashingTypeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class WashingTypeServiceImpl implements WashingTypeService {
    WashingTypeRepository washingTypeRepository;

    @Override
    public List<WashingType> getAllWashingTypes() {
        return washingTypeRepository.findAll();
    }

    @Override
    public WashingType addWashingType(WashingType washingType) {
        return washingTypeRepository.save(washingType);
    }

    @Override
    public WashingType updateWashingType(Integer washingTypeId, WashingType washingType) {
        WashingType washingTypeToUpdate = washingTypeRepository.findById(washingTypeId).orElseThrow(
                () -> new ResourceNotFoundException("WashingType", "id", washingTypeId.toString())
        );
        washingTypeToUpdate.setTypeName(washingType.getTypeName());
        washingTypeToUpdate.setDefaultDuration(washingType.getDefaultDuration());
        washingTypeToUpdate.setDefaultPrice(washingType.getDefaultPrice());
        return washingTypeRepository.save(washingTypeToUpdate);
    }

    @Override
    public void deleteWashingType(Integer washingTypeId) {
        washingTypeRepository.deleteById(washingTypeId);
    }
}
