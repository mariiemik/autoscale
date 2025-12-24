package com.example.order_service.repository;

import com.example.order_service.model.OrderIdAndInventoryItemIdPK;
import com.example.order_service.model.OrderItemModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItemModel, OrderIdAndInventoryItemIdPK> {

}
