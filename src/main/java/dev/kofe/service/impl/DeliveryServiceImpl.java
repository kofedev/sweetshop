package dev.kofe.service.impl;

import dev.kofe.model.Delivery;
import dev.kofe.repo.DeliveryRepository;
import dev.kofe.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Transactional
    public void saveDelivery (Delivery delivery) {
        deliveryRepository.save(delivery);
    }

    public List<Delivery> getAllDeliverOptions () {
        return deliveryRepository.findAll();
    }

    @Transactional
    public void deleteDelivery(Delivery delivery) {
        deliveryRepository.delete(delivery);
    }

    public Delivery getDeliveryById (Long id) {
        return deliveryRepository.getById(id);
    }

}
