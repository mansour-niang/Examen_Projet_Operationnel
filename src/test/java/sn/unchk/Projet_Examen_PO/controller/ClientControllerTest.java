package sn.unchk.Projet_Examen_PO.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClientControllerTest {

    private static final String URL = "/api/v1/clients";

    @Autowired
    private MockMvc mockMvc;

    private static String json(String firstname, String lastname, String email) {
        return """
                {"firstname":"%s","lastname":"%s","email":"%s"}
                """.formatted(firstname, lastname, email);
    }

    private String createClient(String email) throws Exception {
        MvcResult result = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Awa", "Diop", email)))
                .andExpect(status().isCreated())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        return body.replaceAll(".*\"id\":(\\d+).*", "$1");
    }

    @Test
    void create_returns201WithBody() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Awa", "Diop", "awa@test.sn")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstname").value("Awa"))
                .andExpect(jsonPath("$.lastname").value("Diop"))
                .andExpect(jsonPath("$.email").value("awa@test.sn"));
    }

    @Test
    void create_withInvalidData_returns400WithFieldErrors() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(" ", "", "pas-un-email")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.firstname").exists())
                .andExpect(jsonPath("$.errors.lastname").exists())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    void create_withMalformedJson_returns400() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{pas du json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_withDuplicateEmail_returns409() throws Exception {
        createClient("doublon@test.sn");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Autre", "Personne", "doublon@test.sn")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void findById_returnsClient() throws Exception {
        String id = createClient("find@test.sn");

        mockMvc.perform(get(URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("find@test.sn"));
    }

    @Test
    void findById_unknownId_returns404() throws Exception {
        mockMvc.perform(get(URL + "/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void findById_withNonNumericId_returns400() throws Exception {
        mockMvc.perform(get(URL + "/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void list_containsCreatedClient() throws Exception {
        createClient("liste@test.sn");

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.email == 'liste@test.sn')]").isNotEmpty());
    }

    @Test
    void update_changesFields() throws Exception {
        String id = createClient("avant@test.sn");

        mockMvc.perform(put(URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Fatou", "Ndiaye", "apres@test.sn")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("Fatou"))
                .andExpect(jsonPath("$.email").value("apres@test.sn"));
    }

    @Test
    void update_keepingOwnEmail_returns200() throws Exception {
        String id = createClient("meme@test.sn");

        mockMvc.perform(put(URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Nouveau", "Nom", "meme@test.sn")))
                .andExpect(status().isOk());
    }

    @Test
    void update_withEmailOfAnotherClient_returns409() throws Exception {
        createClient("premier@test.sn");
        String secondId = createClient("second@test.sn");

        mockMvc.perform(put(URL + "/" + secondId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Awa", "Diop", "premier@test.sn")))
                .andExpect(status().isConflict());
    }

    @Test
    void update_unknownId_returns404() throws Exception {
        mockMvc.perform(put(URL + "/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Awa", "Diop", "inconnu@test.sn")))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_removesClient() throws Exception {
        String id = createClient("suppr@test.sn");

        mockMvc.perform(delete(URL + "/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(URL + "/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_unknownId_returns404() throws Exception {
        mockMvc.perform(delete(URL + "/999999"))
                .andExpect(status().isNotFound());
    }
}
