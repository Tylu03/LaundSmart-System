package com.c1se22.publiclaundsmartsystem.service;

import com.c1se22.publiclaundsmartsystem.entity.WashingType;

import java.util.List;

public interface WashingTypeService {
    List<WashingType> getAllWashingTypes();
    WashingType addWashingType(WashingType washingType);
    WashingType updateWashingType(Integer washingTypeId, WashingType washingType);
    void deleteWashingType(Integer washingTypeId);
}
