package sn.unchk.Projet_Examen_PO.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClientRequestDTO(
        @NotBlank(message = "Le prénom est obligatoire")
        @Size(max = 150, message = "Le prénom ne doit pas dépasser 150 caractères")
        String firstname,

        @NotBlank(message = "Le nom est obligatoire")
        String lastname,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "L'email n'est pas valide")
        String email
) {
}
