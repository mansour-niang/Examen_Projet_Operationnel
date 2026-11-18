package sn.unchk.Projet_Examen_PO.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sn.unchk.Projet_Examen_PO.dto.OrderDetailRequestDTO;
import sn.unchk.Projet_Examen_PO.dto.OrderDetailResponseDTO;
import sn.unchk.Projet_Examen_PO.service.OrderDetailService;

@RestController
@RequestMapping("/api/v1/orders/{orderId}/details")
@RequiredArgsConstructor
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    // POST /api/v1/orders/3/details
    @PostMapping
    public ResponseEntity<OrderDetailResponseDTO> addDetail(
            @PathVariable Long orderId, @RequestBody @Valid OrderDetailRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDetailService.addDetail(orderId, dto));
    }

    // GET /api/v1/orders/3/details
    @GetMapping
    public List<OrderDetailResponseDTO> listDetails(@PathVariable Long orderId) {
        return orderDetailService.listDetails(orderId);
    }

    // GET /api/v1/orders/3/details/7
    @GetMapping("/{detailId}")
    public OrderDetailResponseDTO findById(@PathVariable Long orderId, @PathVariable Long detailId) {
        return orderDetailService.findById(orderId, detailId);
    }

    // PUT /api/v1/orders/3/details/7
    @PutMapping("/{detailId}")
    public OrderDetailResponseDTO updateDetail(
            @PathVariable Long orderId, @PathVariable Long detailId, @RequestBody @Valid OrderDetailRequestDTO dto) {
        return orderDetailService.updateDetail(orderId, detailId, dto);
    }

    // DELETE /api/v1/orders/3/details/7
    @DeleteMapping("/{detailId}")
    public ResponseEntity<Void> deleteDetail(@PathVariable Long orderId, @PathVariable Long detailId) {
        orderDetailService.deleteDetail(orderId, detailId);
        return ResponseEntity.noContent().build();
    }
}
