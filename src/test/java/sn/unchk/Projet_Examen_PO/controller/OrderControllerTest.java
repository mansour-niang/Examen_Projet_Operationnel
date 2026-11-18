package sn.unchk.Projet_Examen_PO.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderControllerTest {

    private static final String ORDERS = "/api/v1/orders";

    @Autowired
    private MockMvc mockMvc;

    private String clientId;
    private String riz;      // price 1000
    private String sucre;    // price 500

    @BeforeEach
    void setUp() throws Exception {
        clientId = create("/api/v1/clients",
                "{\"firstname\":\"Awa\",\"lastname\":\"Diop\",\"email\":\"awa.cmd@test.sn\"}");
        riz = create("/api/v1/products", "{\"name\":\"Riz\",\"price\":1000}");
        sucre = create("/api/v1/products", "{\"name\":\"Sucre\",\"price\":500}");
    }

    private String create(String url, String body) throws Exception {
        MvcResult result = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();
        return String.valueOf((Object) JsonPath.read(result.getResponse().getContentAsString(), "$.id"));
    }

    private String orderJson(String client, String... lines) {
        return "{\"clientId\":" + client + ",\"details\":[" + String.join(",", lines) + "]}";
    }

    private String line(String productId, int quantite) {
        return "{\"productId\":" + productId + ",\"quantite\":" + quantite + "}";
    }

    /** Creates an order with 2 x riz (1000) and 3 x sucre (500): total 3500. */
    private MvcResult createOrder() throws Exception {
        return mockMvc.perform(post(ORDERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson(clientId, line(riz, 2), line(sucre, 3))))
                .andExpect(status().isCreated())
                .andReturn();
    }

    private String orderId() throws Exception {
        return String.valueOf((Object) JsonPath.read(createOrder().getResponse().getContentAsString(), "$.id"));
    }

    private String firstDetailId(String orderId) throws Exception {
        MvcResult result = mockMvc.perform(get(ORDERS + "/" + orderId + "/details"))
                .andExpect(status().isOk())
                .andReturn();
        return String.valueOf((Object) JsonPath.read(result.getResponse().getContentAsString(), "$[0].id"));
    }

    // ---------- Orders ----------

    @Test
    void create_returns201WithLinesAndTotal() throws Exception {
        mockMvc.perform(post(ORDERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson(clientId, line(riz, 2), line(sucre, 3))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.date").isString())
                .andExpect(jsonPath("$.client.firstname").value("Awa"))
                .andExpect(jsonPath("$.details.length()").value(2))
                .andExpect(jsonPath("$.details[0].productName").value("Riz"))
                .andExpect(jsonPath("$.details[0].prix").value(1000.0))
                .andExpect(jsonPath("$.details[0].quantite").value(2))
                .andExpect(jsonPath("$.details[0].sousTotal").value(2000.0))
                .andExpect(jsonPath("$.details[1].sousTotal").value(1500.0))
                .andExpect(jsonPath("$.total").value(3500.0));
    }

    @Test
    void create_withoutClientAndLines_returns400() throws Exception {
        mockMvc.perform(post(ORDERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"details\":[]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.clientId").exists())
                .andExpect(jsonPath("$.errors.details").exists());
    }

    @Test
    void create_withInvalidLine_returns400() throws Exception {
        mockMvc.perform(post(ORDERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson(clientId, "{\"quantite\":0}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors['details[0].productId']").exists())
                .andExpect(jsonPath("$.errors['details[0].quantite']").exists());
    }

    @Test
    void create_withUnknownClient_returns404() throws Exception {
        mockMvc.perform(post(ORDERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson("999999", line(riz, 1))))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_withUnknownProduct_returns404() throws Exception {
        mockMvc.perform(post(ORDERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson(clientId, line("999999", 1))))
                .andExpect(status().isNotFound());
    }

    @Test
    void orderLinePrice_isNotAffectedByLaterProductPriceChange() throws Exception {
        String orderId = orderId();

        mockMvc.perform(put("/api/v1/products/" + riz)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Riz\",\"price\":2000}"))
                .andExpect(status().isOk());

        mockMvc.perform(get(ORDERS + "/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details[0].prix").value(1000.0))
                .andExpect(jsonPath("$.total").value(3500.0));
    }

    @Test
    void findById_returnsOrder() throws Exception {
        String orderId = orderId();

        mockMvc.perform(get(ORDERS + "/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(Long.parseLong(orderId)))
                .andExpect(jsonPath("$.total").value(3500.0));
    }

    @Test
    void findById_unknownId_returns404() throws Exception {
        mockMvc.perform(get(ORDERS + "/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void list_containsCreatedOrder() throws Exception {
        String orderId = orderId();

        mockMvc.perform(get(ORDERS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + orderId + ")]").isNotEmpty());
    }

    @Test
    void update_changesClient() throws Exception {
        String orderId = orderId();
        String otherClient = create("/api/v1/clients",
                "{\"firstname\":\"Moussa\",\"lastname\":\"Fall\",\"email\":\"moussa.cmd@test.sn\"}");

        mockMvc.perform(put(ORDERS + "/" + orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clientId\":" + otherClient + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.client.firstname").value("Moussa"))
                .andExpect(jsonPath("$.details.length()").value(2));
    }

    @Test
    void update_withUnknownClient_returns404() throws Exception {
        String orderId = orderId();

        mockMvc.perform(put(ORDERS + "/" + orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clientId\":999999}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_withoutClient_returns400() throws Exception {
        String orderId = orderId();

        mockMvc.perform(put(ORDERS + "/" + orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_removesOrderAndItsLines() throws Exception {
        String orderId = orderId();

        mockMvc.perform(delete(ORDERS + "/" + orderId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(ORDERS + "/" + orderId))
                .andExpect(status().isNotFound());
        mockMvc.perform(get(ORDERS + "/" + orderId + "/details"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_unknownId_returns404() throws Exception {
        mockMvc.perform(delete(ORDERS + "/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteClientWithOrders_returns409() throws Exception {
        orderId();

        mockMvc.perform(delete("/api/v1/clients/" + clientId))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteProductUsedInOrder_returns409() throws Exception {
        orderId();

        mockMvc.perform(delete("/api/v1/products/" + riz))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteClientAndProductAfterOrderDeleted_succeeds() throws Exception {
        String orderId = orderId();
        mockMvc.perform(delete(ORDERS + "/" + orderId)).andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/v1/clients/" + clientId)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/v1/products/" + riz)).andExpect(status().isNoContent());
    }

    // ---------- Order lines ----------

    @Test
    void addDetail_returns201AndUpdatesTotal() throws Exception {
        String orderId = orderId();

        mockMvc.perform(post(ORDERS + "/" + orderId + "/details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(line(riz, 1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.prix").value(1000.0))
                .andExpect(jsonPath("$.sousTotal").value(1000.0));

        mockMvc.perform(get(ORDERS + "/" + orderId))
                .andExpect(jsonPath("$.details.length()").value(3))
                .andExpect(jsonPath("$.total").value(4500.0));
    }

    @Test
    void addDetail_withUnknownOrder_returns404() throws Exception {
        mockMvc.perform(post(ORDERS + "/999999/details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(line(riz, 1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void addDetail_withUnknownProduct_returns404() throws Exception {
        String orderId = orderId();

        mockMvc.perform(post(ORDERS + "/" + orderId + "/details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(line("999999", 1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void addDetail_withInvalidQuantity_returns400() throws Exception {
        String orderId = orderId();

        mockMvc.perform(post(ORDERS + "/" + orderId + "/details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(line(riz, 0)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.quantite").exists());
    }

    @Test
    void listDetails_returnsAllLines() throws Exception {
        String orderId = orderId();

        mockMvc.perform(get(ORDERS + "/" + orderId + "/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void findDetail_returnsLine() throws Exception {
        String orderId = orderId();
        String detailId = firstDetailId(orderId);

        mockMvc.perform(get(ORDERS + "/" + orderId + "/details/" + detailId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Riz"));
    }

    @Test
    void findDetail_belongingToAnotherOrder_returns404() throws Exception {
        String firstOrder = orderId();
        String detailId = firstDetailId(firstOrder);
        String secondOrder = orderId();

        mockMvc.perform(get(ORDERS + "/" + secondOrder + "/details/" + detailId))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateDetail_changesQuantityAndTotal() throws Exception {
        String orderId = orderId();
        String detailId = firstDetailId(orderId);

        mockMvc.perform(put(ORDERS + "/" + orderId + "/details/" + detailId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(line(riz, 5)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantite").value(5))
                .andExpect(jsonPath("$.sousTotal").value(5000.0));

        mockMvc.perform(get(ORDERS + "/" + orderId))
                .andExpect(jsonPath("$.total").value(6500.0));
    }

    @Test
    void updateDetail_withOtherProduct_copiesItsPrice() throws Exception {
        String orderId = orderId();
        String detailId = firstDetailId(orderId);

        mockMvc.perform(put(ORDERS + "/" + orderId + "/details/" + detailId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(line(sucre, 4)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Sucre"))
                .andExpect(jsonPath("$.prix").value(500.0))
                .andExpect(jsonPath("$.sousTotal").value(2000.0));
    }

    @Test
    void updateDetail_unknownDetail_returns404() throws Exception {
        String orderId = orderId();

        mockMvc.perform(put(ORDERS + "/" + orderId + "/details/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(line(riz, 1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteDetail_removesLineAndUpdatesTotal() throws Exception {
        String orderId = orderId();
        String detailId = firstDetailId(orderId);

        mockMvc.perform(delete(ORDERS + "/" + orderId + "/details/" + detailId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(ORDERS + "/" + orderId))
                .andExpect(jsonPath("$.details.length()").value(1))
                .andExpect(jsonPath("$.total").value(1500.0));
    }

    @Test
    void deleteDetail_lastLine_returns409() throws Exception {
        MvcResult result = mockMvc.perform(post(ORDERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson(clientId, line(riz, 1))))
                .andExpect(status().isCreated())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        String orderId = String.valueOf((Object) JsonPath.read(body, "$.id"));
        String detailId = String.valueOf((Object) JsonPath.read(body, "$.details[0].id"));

        mockMvc.perform(delete(ORDERS + "/" + orderId + "/details/" + detailId))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteDetail_unknownDetail_returns404() throws Exception {
        String orderId = orderId();

        mockMvc.perform(delete(ORDERS + "/" + orderId + "/details/999999"))
                .andExpect(status().isNotFound());
    }
}
