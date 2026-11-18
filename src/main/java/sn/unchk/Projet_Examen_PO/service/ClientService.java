package sn.unchk.Projet_Examen_PO.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sn.unchk.Projet_Examen_PO.dto.ClientRequestDTO;
import sn.unchk.Projet_Examen_PO.dto.ClientResponseDTO;
import sn.unchk.Projet_Examen_PO.entity.Client;
import sn.unchk.Projet_Examen_PO.exception.ConflictException;
import sn.unchk.Projet_Examen_PO.exception.ResourceNotFoundException;
import sn.unchk.Projet_Examen_PO.repository.ClientRepository;
import sn.unchk.Projet_Examen_PO.repository.OrderRepository;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public ClientResponseDTO addClient(ClientRequestDTO dto) {
        if (clientRepository.existsByEmail(dto.email())) {
            throw new ConflictException("Un client avec cet email existe déjà");
        }
        Client client = new Client();
        applyFields(client, dto);
        return ClientResponseDTO.from(clientRepository.save(client));
    }

    @Transactional(readOnly = true)
    public List<ClientResponseDTO> listClients() {
        return clientRepository.findAll().stream()
                .map(ClientResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClientResponseDTO findById(Long id) {
        return ClientResponseDTO.from(getClient(id));
    }

    @Transactional
    public ClientResponseDTO updateClient(Long id, ClientRequestDTO dto) {
        Client client = getClient(id);
        if (clientRepository.existsByEmailAndIdNot(dto.email(), id)) {
            throw new ConflictException("Un client avec cet email existe déjà");
        }
        applyFields(client, dto);
        return ClientResponseDTO.from(clientRepository.save(client));
    }

    @Transactional
    public void deleteClient(Long id) {
        Client client = getClient(id);
        if (orderRepository.existsByClientId(id)) {
            throw new ConflictException("Ce client a des commandes et ne peut pas être supprimé");
        }
        clientRepository.delete(client);
    }

    public Client getClient(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable : " + id));
    }

    private void applyFields(Client client, ClientRequestDTO dto) {
        client.setFirstname(dto.firstname());
        client.setLastname(dto.lastname());
        client.setEmail(dto.email());
    }
}
