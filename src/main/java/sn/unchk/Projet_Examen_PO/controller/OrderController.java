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
import sn.unchk.Projet_Examen_PO.dto.OrderClientUpdateDTO;
import sn.unchk.Projet_Examen_PO.dto.OrderRequestDTO;
import sn.unchk.Projet_Examen_PO.dto.OrderResponseDTO;
import sn.unchk.Projet_Examen_PO.service.OrderService;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // POST /api/v1/orders
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody @Valid OrderRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.addOrder(dto));
    }

    // GET /api/v1/orders
    @GetMapping
    public List<OrderResponseDTO> listOrders() {
        return orderService.listOrders();
    }

    // GET /api/v1/orders/3
    @GetMapping("/{id}")
    public OrderResponseDTO findById(@PathVariable Long id) {
        return orderService.findById(id);
    }

    // PUT /api/v1/orders/3 : changes the client of the order
    @PutMapping("/{id}")
    public OrderResponseDTO updateOrder(@PathVariable Long id, @RequestBody @Valid OrderClientUpdateDTO dto) {
        return orderService.updateOrder(id, dto);
    }

    // DELETE /api/v1/orders/3
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
