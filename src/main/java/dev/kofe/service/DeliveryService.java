package dev.kofe.service;

import dev.kofe.model.Delivery;
import java.util.List;

public interface DeliveryService {

    void saveDelivery(Delivery delivery);

    List<Delivery> getAllDeliverOptions();

    void deleteDelivery(Delivery delivery);

    Delivery getDeliveryById(Long id);

}
