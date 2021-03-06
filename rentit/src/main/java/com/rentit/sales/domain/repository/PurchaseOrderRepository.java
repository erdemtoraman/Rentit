package com.rentit.sales.domain.repository;

import com.rentit.inventory.domain.model.PlantInventoryEntry;
import com.rentit.sales.domain.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, String> {
    @Query("select p from PurchaseOrder p where p.rentalPeriod.startDate =?1 and p.status = com.rentit.sales.domain.model.POStatus.ACCEPTED")
    List<PurchaseOrder> findToBeDispatchedOn(LocalDate startDate);

    @Query("select p from PurchaseOrder  p where  p.plant.id = ?1")
    List<PurchaseOrder> findAllByPlant(String plantInventoryEntry);
}
