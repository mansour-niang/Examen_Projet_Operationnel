package sn.unchk.Projet_Examen_PO.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sn.unchk.Projet_Examen_PO.dto.OrderClientUpdateDTO;
import sn.unchk.Projet_Examen_PO.dto.OrderDetailRequestDTO;
import sn.unchk.Projet_Examen_PO.dto.OrderRequestDTO;
import sn.unchk.Projet_Examen_PO.dto.OrderResponseDTO;
import sn.unchk.Projet_Examen_PO.entity.Order;
import sn.unchk.Projet_Examen_PO.entity.OrderDetails;
import sn.unchk.Projet_Examen_PO.entity.Product;
import sn.unchk.Projet_Examen_PO.exception.ResourceNotFoundException;
import sn.unchk.Projet_Examen_PO.repository.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClientService clientService;
    private final ProductService productService;

    @Transactional
    public OrderResponseDTO addOrder(OrderRequestDTO dto) {
        Order order = new Order();
        order.setClient(clientService.getClient(dto.clientId()));
        order.setDate(LocalDateTime.now());
        for (OrderDetailRequestDTO line : dto.details()) {
            Product product = productService.getProduct(line.productId());
            order.addDetail(OrderDetails.of(product, line.quantite()));
        }
        return OrderResponseDTO.from(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> listOrders() {
        return orderRepository.findAll().stream()
                .map(OrderResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO findById(Long id) {
        return OrderResponseDTO.from(getOrder(id));
    }

    @Transactional
    public OrderResponseDTO updateOrder(Long id, OrderClientUpdateDTO dto) {
        Order order = getOrder(id);
        order.setClient(clientService.getClient(dto.clientId()));
        return OrderResponseDTO.from(orderRepository.save(order));
    }

    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.delete(getOrder(id));
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable : " + id));
    }
}
