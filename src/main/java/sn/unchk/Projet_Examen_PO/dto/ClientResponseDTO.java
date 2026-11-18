package sn.unchk.Projet_Examen_PO.dto;

import sn.unchk.Projet_Examen_PO.entity.Client;

public record ClientResponseDTO(
        Long id,
        String firstname,
        String lastname,
        String email
) {

    public static ClientResponseDTO from(Client client) {
        return new ClientResponseDTO(
                client.getId(),
                client.getFirstname(),
                client.getLastname(),
                client.getEmail());
    }
}
