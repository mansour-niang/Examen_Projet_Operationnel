package sn.unchk.Projet_Examen_PO.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderDetailRequestDTO(
        @NotNull(message = "Le produit est obligatoire")
        Long productId,

        @NotNull(message = "La quantité est obligatoire")
        @Min(value = 1, message = "La quantité doit être au moins 1")
        Integer quantite
) {
}
