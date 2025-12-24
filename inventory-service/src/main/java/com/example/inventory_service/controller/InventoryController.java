package com.example.inventory_service.controller;

import com.example.inventory_service.dto.InventoryItemResponseDTO;
import com.example.inventory_service.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactoryFriend;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@Tag(name = "Inventory", description = "Управление запасами товаров")
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);
    InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }


    @Operation(summary = "Проверить наличие", description = "Возвращает доступное количество товара")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Продукт найден"),
            @ApiResponse(responseCode = "400", description = "Нет продукта с данным id"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> itemAvailability(@Parameter(description = "ID товара", required = true)
                                              @PathVariable("id") String id) {
        log.info("/inventory/{id} request");
        try {
            Integer quantity = inventoryService.itemAvailability(id);
            return ResponseEntity.ok(quantity);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Продукты на складе", description = "Возвращает все продукты, имеющиеся на складе")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
    })
    @GetMapping("")
    public ResponseEntity<List<InventoryItemResponseDTO>> alItemsAvailability() {
        log.info("/inventory request");
        List<InventoryItemResponseDTO> responseDTOList = inventoryService.alItemsAvailability();
        return ResponseEntity.ok(responseDTOList);
    }


}
