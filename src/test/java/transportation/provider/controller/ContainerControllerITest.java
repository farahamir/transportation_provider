package transportation.provider.controller;

import transportation.provider.model.ContainerGood;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import transportation.provider.config.Constants;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContainerControllerITest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @BeforeEach
    void reset() {
        var result = this.restTemplate.postForObject("http://localhost:" + port + "/api/container/unload", null, ContainerGood.class);
        while (result != null) {
            result = this.restTemplate.postForObject("http://localhost:" + port + "/api/container/unload", null, ContainerGood.class);
        }
    }

    @Test
    void should_load_goods() {
        var result = this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(1, 2, 100.0), String.class);
        assertThat(result).isEqualTo(Constants.GOOD_LOADED_SUCCESSFULLY);

    }

    private static HttpEntity<ContainerGood> createGoodRequest(int id, int weight, double price) {
        return new HttpEntity<>(new ContainerGood(id, weight, price));
    }

    @Test
    void should_not_load_goods() {
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(1, 2, 100.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(2, 5, 200.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(3, 3, 150.0), String.class);
        var result = this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(4, 3, 130.0), String.class);
        assertThat(result).isEqualTo(Constants.CANNOT_LOAD_GOOD_EXCEEDS_MAXIMUM_GROSS_LOAD);
    }

    @Test
    void should_unload_goods() {
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(1, 2, 100.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(2, 5, 200.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(3, 3, 150.0), String.class);
        var result = this.restTemplate.postForObject("http://localhost:" + port + "/api/container/unload", null, ContainerGood.class);
        assertEquals(3, result.id());
    }

    @Test
    void should_get_TotalWeight() {
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(1, 2, 100.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(2, 5, 200.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(3, 3, 150.0), String.class);
        var result = this.restTemplate.getForObject("http://localhost:" + port + "/api/container/total-weight", Integer.class);
        assertEquals(10, result);

    }

    @Test
    void should_get_HighestPricedGood() {
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(1, 2, 100.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(2, 5, 200.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(3, 3, 150.0), String.class);
        var result = this.restTemplate.getForObject("http://localhost:" + port + "/api/container/highest-priced-good", ContainerGood.class);
        assertEquals(2, result.id());
        assertEquals(200, result.price());
    }

    @Test
    void should_get_updated_HighestPricedGood_after_unload() {
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(1, 2, 100.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(2, 5, 200.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(3, 1, 150.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/unload", null, ContainerGood.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/unload", null, ContainerGood.class);
        var result = this.restTemplate.getForObject("http://localhost:" + port + "/api/container/highest-priced-good", ContainerGood.class);
        assertEquals(1, result.id());
        assertEquals(100, result.price());
    }

    @Test
    void should_get_updated_HighestPricedGood_after_unload_and_loading() {
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(1, 2, 100.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(2, 5, 200.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(3, 1, 150.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/unload", null, ContainerGood.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/unload", null, ContainerGood.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(4, 1, 210.0), String.class);
        var result = this.restTemplate.getForObject("http://localhost:" + port + "/api/container/highest-priced-good", ContainerGood.class);
        assertEquals(4, result.id());
        assertEquals(210, result.price());
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(5, 2, 200.0), String.class);
        result = this.restTemplate.getForObject("http://localhost:" + port + "/api/container/highest-priced-good", ContainerGood.class);
        assertEquals(4, result.id());
        assertEquals(210, result.price());
    }

    @Test
    void should_get_HighestPricedGood_null_when_container_empty() {
        var result = this.restTemplate.postForObject("http://localhost:" + port + "/api/container/unload", null, ContainerGood.class);
        assertNull(result);

    }

    @Test
    void should_get_updated_HighestPricedGood_to_null_after_unloading_all() {
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(1, 2, 100.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(2, 5, 200.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(3, 1, 150.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/unload", null, ContainerGood.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/unload", null, ContainerGood.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/unload", null, ContainerGood.class);
        var result = this.restTemplate.getForObject("http://localhost:" + port + "/api/container/highest-priced-good", ContainerGood.class);
        assertNull(result);

    }

    @Test
    void should_get_updated_TotalWeight_after_unload() {
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(1, 2, 100.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(2, 5, 200.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(3, 1, 150.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/unload", null, ContainerGood.class);
        var result = this.restTemplate.getForObject("http://localhost:" + port + "/api/container/total-weight", Integer.class);
        assertEquals(7, result);

    }

    @Test
    void should_get_updated_TotalWeight_to_zero_after_unloading_all() {
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(1, 2, 100.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(2, 5, 200.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(3, 1, 150.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/unload", null, ContainerGood.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/unload", null, ContainerGood.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/unload", null, ContainerGood.class);
        var result = this.restTemplate.getForObject("http://localhost:" + port + "/api/container/total-weight", Integer.class);
        assertEquals(0, result);

    }

    @Test
    void should_get_updated_TotalWeight_after_unloading_and_loading() {
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(1, 2, 100.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(2, 5, 200.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(3, 1, 150.0), String.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/unload", null, ContainerGood.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/unload", null, ContainerGood.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(4, 1, 210.0), String.class);
        var result = this.restTemplate.getForObject("http://localhost:" + port + "/api/container/total-weight", Integer.class);
        assertEquals(3, result);
        this.restTemplate.postForObject("http://localhost:" + port + "/api/container/load", createGoodRequest(5, 2, 200.0), String.class);
        result = this.restTemplate.getForObject("http://localhost:" + port + "/api/container/total-weight", Integer.class);
        assertEquals(5, result);
    }
}