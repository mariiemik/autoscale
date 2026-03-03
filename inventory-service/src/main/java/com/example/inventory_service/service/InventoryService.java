package com.example.inventory_service.service;

import com.example.common.events.OrderItemEvent;
import com.example.inventory_service.dto.InventoryItemResponseDTO;
import com.example.inventory_service.exception.InvalidItemQuantityException;
import com.example.inventory_service.model.InventoryItemModel;
import com.example.inventory_service.repository.InventoryItemsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    InventoryItemsRepository inventoryItemsRepository;

    public InventoryService(InventoryItemsRepository inventoryItemsRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.inventoryItemsRepository = inventoryItemsRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Cacheable(value = "inventory", key = "#id")
    public Integer itemAvailability(String id) {
        Optional<InventoryItemModel> item = inventoryItemsRepository.findById(id);
        return item.orElseThrow().getQuantity();
    }

    @Cacheable(value = "inventory_all")
    public List<InventoryItemResponseDTO> alItemsAvailability() {
        log.info("alItemsAvailability() method in InventoryService");
        return inventoryItemsRepository.findAll().stream().map(x -> new InventoryItemResponseDTO(x.getInventoryItemId(), x.getName(), x.getQuantity(), x.getPrice())).toList();
    }



    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "inventory", key = "#id"),
            @CacheEvict(value = "inventory_all", allEntries = true)
    })
    public void reserveItem(String id, int reservedQuantity) {
        log.info("reserveItem() method in InventoryService");
        Optional<InventoryItemModel> inventoryItemModel = inventoryItemsRepository.findById(id);
        inventoryItemModel.orElseThrow(() -> new NoSuchElementException("No element with id = " + id));

        var item = inventoryItemModel.get();

        if (reservedQuantity > item.getQuantity()) {
            throw new InvalidItemQuantityException("reservedQuantity more than available quantity");
        }
        item.setReservedQuantity(item.getReservedQuantity() + reservedQuantity);
        item.setQuantity(item.getQuantity() - reservedQuantity);

    }


    @Transactional
    public void reserveOrderItems(List<OrderItemEvent> items) {
        log.info("reserveOrderItems() method in InventoryService");
        for (OrderItemEvent item : items) {

            reserveItem(item.getItemId(), item.getQuantity());
        }
    }


    @Caching(evict = {
            @CacheEvict(value = "inventory", key = "#id"),
            @CacheEvict(value = "inventory_all", allEntries = true)
    })
    @Transactional
    public void cancelItemReserve(String id, int reservedQuantity) {
        log.info("cancelItemReserve() method in InventoryService");

        Optional<InventoryItemModel> inventoryItemModel = inventoryItemsRepository.findById(id);
        inventoryItemModel.orElseThrow(() -> new NoSuchElementException("No element with id = " + id));
        var item = inventoryItemModel.get();
        if (reservedQuantity > item.getReservedQuantity()) {
            throw new InvalidItemQuantityException("Cannot cancel more than reserved");
        }

        item.setQuantity(reservedQuantity + item.getQuantity());
        item.setReservedQuantity(item.getReservedQuantity() - reservedQuantity);
        log.info("Reserved {} of item {}, remaining quantity={}, reservedQuantity={}", reservedQuantity, id, item.getQuantity(), item.getReservedQuantity());

    }

    @Caching(evict = {
            @CacheEvict(value = "inventory", key = "#id"),
            @CacheEvict(value = "inventory_all", allEntries = true)
    })
    @Transactional
    public void subtractBoughtItem(String id, int quantity) {
        log.info("subtractBoughtItem() method in InventoryService");
        Optional<InventoryItemModel> inventoryItemModel = inventoryItemsRepository.findById(id);
        inventoryItemModel.orElseThrow(() -> new NoSuchElementException("No element with id = " + id));
        var item = inventoryItemModel.get();

        item.setQuantity(item.getQuantity() - quantity);
        item.setReservedQuantity(item.getReservedQuantity() - quantity);

    }
}
