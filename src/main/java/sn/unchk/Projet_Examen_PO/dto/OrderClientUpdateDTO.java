package sn.unchk.Projet_Examen_PO.dto;

import jakarta.validation.constraints.NotNull;

public record OrderClientUpdateDTO(
        @NotNull(message = "Le client est obligatoire")
        Long clientId
) {
}
