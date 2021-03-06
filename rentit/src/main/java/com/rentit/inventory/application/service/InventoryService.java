package com.rentit.inventory.application.service;

import com.rentit.common.application.exceptions.PlantNotFoundException;
import com.rentit.common.domain.model.BusinessPeriod;
import com.rentit.common.service.MaintenanceService;
import com.rentit.inventory.application.dto.PlantInventoryEntryDTO;
import com.rentit.inventory.domain.model.PlantInventoryEntry;
import com.rentit.inventory.domain.model.PlantInventoryItem;
import com.rentit.inventory.domain.model.PlantReservation;
import com.rentit.inventory.domain.repository.InventoryRepository;
import com.rentit.inventory.domain.repository.PlantReservationRepository;
import com.rentit.inventory.infrastructure.InventoryIdentifierFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InventoryService {
    @Value("${maintenanceURL}")
    String maintenanceURL;
    @Autowired
    InventoryRepository inventoryRepository;
    @Autowired
    PlantReservationRepository plantReservationRepository;
    @Autowired
    MaintenanceService maintenanceService;

    @Autowired
    PlantInventoryEntryAssembler plantInventoryEntryAssembler;

    @Autowired
    InventoryIdentifierFactory identifierFactory;

    public List<PlantInventoryEntryDTO> findAvailablePlants(String name, LocalDate startDate, LocalDate endDate) {
        return plantInventoryEntryAssembler.toResources(inventoryRepository.findAvailable(name, startDate, endDate));
    }

    public boolean isAvailable(String plantInventoryID, LocalDate startDate, LocalDate endDate){
       return inventoryRepository.isAvailable(plantInventoryID, startDate, endDate);
    }

    public PlantReservation createPlantReservation(PlantInventoryEntry plantInventoryEntry, BusinessPeriod schedule) throws PlantNotFoundException {
        List<PlantInventoryItem> items = inventoryRepository.findAvailableInventoryItems(plantInventoryEntry, schedule.getStartDate(), schedule.getEndDate());
        if (items.size() == 0)
            throw new PlantNotFoundException("Requested plant is unavailable");

        boolean isThereAnyAvailablePlantFromMaintenance = maintenanceService.isAvailable(items, schedule);
        if(!isThereAnyAvailablePlantFromMaintenance)      {
            throw new PlantNotFoundException("Requested plant is unavailable");
        }

        PlantReservation plantReservation = PlantReservation.of(identifierFactory.nextPlantInventoryEntryID(), items.get(0), schedule);
        plantReservationRepository.save(plantReservation);
        return plantReservation;
    }

    public PlantInventoryEntryDTO findPlant(String id) {
        return plantInventoryEntryAssembler.toResource(inventoryRepository.findOne(id));
    }


}
