package sn.unchk.Projet_Examen_PO.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductRequestDTO(
        @NotBlank(message = "Le nom du produit est obligatoire")
        @Size(max = 255, message = "Le nom ne doit pas dépasser 255 caractères")
        String name,

        @NotNull(message = "Le prix est obligatoire")
        @Positive(message = "Le prix doit être strictement positif")
        @Digits(integer = 12, fraction = 2, message = "Le prix doit avoir au plus 2 décimales")
        BigDecimal price
) {
}
