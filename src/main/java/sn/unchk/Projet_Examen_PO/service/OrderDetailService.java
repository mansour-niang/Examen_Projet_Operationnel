package sn.unchk.Projet_Examen_PO.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sn.unchk.Projet_Examen_PO.dto.OrderDetailRequestDTO;
import sn.unchk.Projet_Examen_PO.dto.OrderDetailResponseDTO;
import sn.unchk.Projet_Examen_PO.entity.Order;
import sn.unchk.Projet_Examen_PO.entity.OrderDetails;
import sn.unchk.Projet_Examen_PO.entity.Product;
import sn.unchk.Projet_Examen_PO.exception.ConflictException;
import sn.unchk.Projet_Examen_PO.exception.ResourceNotFoundException;
import sn.unchk.Projet_Examen_PO.repository.OrderDetailsRepository;

@Service
@RequiredArgsConstructor
public class OrderDetailService {

    private final OrderDetailsRepository orderDetailsRepository;
    private final OrderService orderService;
    private final ProductService productService;

    @Transactional
    public OrderDetailResponseDTO addDetail(Long orderId, OrderDetailRequestDTO dto) {
        Order order = orderService.getOrder(orderId);
        Product product = productService.getProduct(dto.productId());
        OrderDetails detail = OrderDetails.of(product, dto.quantite());
        order.addDetail(detail);
        return OrderDetailResponseDTO.from(orderDetailsRepository.save(detail));
    }

    @Transactional(readOnly = true)
    public List<OrderDetailResponseDTO> listDetails(Long orderId) {
        return orderService.getOrder(orderId).getDetails().stream()
                .map(OrderDetailResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDetailResponseDTO findById(Long orderId, Long detailId) {
        return OrderDetailResponseDTO.from(getDetail(orderService.getOrder(orderId), detailId));
    }

    @Transactional
    public OrderDetailResponseDTO updateDetail(Long orderId, Long detailId, OrderDetailRequestDTO dto) {
        OrderDetails detail = getDetail(orderService.getOrder(orderId), detailId);
        if (!detail.getProduct().getId().equals(dto.productId())) {
            Product product = productService.getProduct(dto.productId());
            detail.setProduct(product);
            detail.setPrix(product.getPrice());
        }
        detail.setQuantite(dto.quantite());
        return OrderDetailResponseDTO.from(orderDetailsRepository.save(detail));
    }

    @Transactional
    public void deleteDetail(Long orderId, Long detailId) {
        Order order = orderService.getOrder(orderId);
        OrderDetails detail = getDetail(order, detailId);
        if (order.getDetails().size() == 1) {
            throw new ConflictException("Une commande doit contenir au moins une ligne");
        }
        order.removeDetail(detail);
    }

    private OrderDetails getDetail(Order order, Long detailId) {
        return order.getDetails().stream()
                .filter(detail -> detail.getId().equals(detailId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ligne introuvable : " + detailId + " (commande " + order.getId() + ")"));
    }
}
