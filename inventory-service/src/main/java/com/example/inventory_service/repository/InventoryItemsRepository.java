package com.example.inventory_service.repository;

import com.example.inventory_service.model.InventoryItemModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryItemsRepository extends JpaRepository<InventoryItemModel, String> {


}
