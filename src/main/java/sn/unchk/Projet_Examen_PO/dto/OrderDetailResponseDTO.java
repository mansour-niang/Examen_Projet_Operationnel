package sn.unchk.Projet_Examen_PO.dto;

import java.math.BigDecimal;

import sn.unchk.Projet_Examen_PO.entity.OrderDetails;

public record OrderDetailResponseDTO(
        Long id,
        Long productId,
        String productName,
        BigDecimal prix,
        Integer quantite,
        BigDecimal sousTotal
) {

    public static OrderDetailResponseDTO from(OrderDetails detail) {
        return new OrderDetailResponseDTO(
                detail.getId(),
                detail.getProduct().getId(),
                detail.getProduct().getName(),
                detail.getPrix(),
                detail.getQuantite(),
                detail.getPrix().multiply(BigDecimal.valueOf(detail.getQuantite())));
    }
}
