package sn.unchk.Projet_Examen_PO.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sn.unchk.Projet_Examen_PO.dto.ClientRequestDTO;
import sn.unchk.Projet_Examen_PO.dto.ClientResponseDTO;
import sn.unchk.Projet_Examen_PO.service.ClientService;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    // POST /api/v1/clients
    @PostMapping
    public ResponseEntity<ClientResponseDTO> createClient(@RequestBody @Valid ClientRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.addClient(dto));
    }

    // GET /api/v1/clients
    @GetMapping
    public List<ClientResponseDTO> listClients() {
        return clientService.listClients();
    }

    // GET /api/v1/clients/3
    @GetMapping("/{id}")
    public ClientResponseDTO findById(@PathVariable Long id) {
        return clientService.findById(id);
    }

    // PUT /api/v1/clients/3
    @PutMapping("/{id}")
    public ClientResponseDTO updateClient(@PathVariable Long id, @RequestBody @Valid ClientRequestDTO dto) {
        return clientService.updateClient(id, dto);
    }

    // DELETE /api/v1/clients/3
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
