package com.example.inventory_service.repository;

import com.example.inventory_service.dto.InventoryItemResponseDTO;
import com.example.inventory_service.model.InventoryItemModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryItemsRepository extends JpaRepository<InventoryItemModel, String> {
//    @Query("""
//        SELECT new com.yourpackage.InventoryItemResponseDTO(
//            i.inventoryItemId,
//            i.name,
//            (i.quantity - i.reservedQuantity),
//            i.price
//        )
//        FROM InventoryItemModel i
//        """)
//    List<InventoryItemResponseDTO> findAllWithActualQuantity();
//    Optional<InventoryItemModel> findByProductId(String productId);

}
