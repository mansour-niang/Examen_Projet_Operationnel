package sn.unchk.Projet_Examen_PO.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record OrderRequestDTO(
        @NotNull(message = "Le client est obligatoire")
        Long clientId,

        @NotEmpty(message = "La commande doit contenir au moins une ligne")
        List<@NotNull(message = "Une ligne de commande ne peut pas être vide") @Valid OrderDetailRequestDTO> details
) {
}
