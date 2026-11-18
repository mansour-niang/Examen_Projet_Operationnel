package sn.unchk.Projet_Examen_PO.dto;

import java.math.BigDecimal;

import sn.unchk.Projet_Examen_PO.entity.Product;

public record ProductResponseDTO(
        Long id,
        String name,
        BigDecimal price
) {

    public static ProductResponseDTO from(Product product) {
        return new ProductResponseDTO(product.getId(), product.getName(), product.getPrice());
    }
}
