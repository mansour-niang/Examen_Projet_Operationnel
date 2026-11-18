package sn.unchk.Projet_Examen_PO.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import sn.unchk.Projet_Examen_PO.entity.Order;

public record OrderResponseDTO(
        Long id,
        LocalDateTime date,
        ClientSummary client,
        List<OrderDetailResponseDTO> details,
        BigDecimal total
) {

    public record ClientSummary(Long id, String firstname, String lastname) {
    }

    public static OrderResponseDTO from(Order order) {
        List<OrderDetailResponseDTO> lines = order.getDetails().stream()
                .map(OrderDetailResponseDTO::from)
                .toList();
        BigDecimal total = lines.stream()
                .map(OrderDetailResponseDTO::sousTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new OrderResponseDTO(
                order.getId(),
                order.getDate(),
                new ClientSummary(
                        order.getClient().getId(),
                        order.getClient().getFirstname(),
                        order.getClient().getLastname()),
                lines,
                total);
    }
}
