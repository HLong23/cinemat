package vn.edu.cinema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Kiểm tra luồng chính của REST API: JWT, phân quyền theo vai trò và đặt/huỷ vé. */
@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    private String login(String username, String password) throws Exception {
        String body = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn().getResponse().getContentAsString();
        return om.readTree(body).get("token").asText();
    }

    @Test
    void publicEndpointsDoNotNeedToken() throws Exception {
        mvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThan(0)));
    }

    @Test
    void wrongPasswordIsRejected() throws Exception {
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"sai-mat-khau\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void writeRequiresAdminRole() throws Exception {
        String movie = "{\"title\":\"Phim test\",\"durationMinutes\":100,\"genre\":\"Test\",\"active\":true}";

        // Không token -> 401
        mvc.perform(post("/api/movies").contentType(MediaType.APPLICATION_JSON).content(movie))
                .andExpect(status().isUnauthorized());

        // Token USER -> 403
        mvc.perform(post("/api/movies").header("Authorization", "Bearer " + login("user", "user123"))
                        .contentType(MediaType.APPLICATION_JSON).content(movie))
                .andExpect(status().isForbidden());

        // Token ADMIN -> 201
        mvc.perform(post("/api/movies").header("Authorization", "Bearer " + login("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON).content(movie))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Phim test"));
    }

    @Test
    void bookAndCancelTicket() throws Exception {
        String token = login("user", "user123");

        String showtimes = mvc.perform(get("/api/showtimes")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode first = om.readTree(showtimes).get(0);
        long showtimeId = first.get("id").asLong();

        String request = "{\"showtimeId\":" + showtimeId + ",\"seats\":[\"C3\",\"C4\"]}";

        String created = mvc.perform(post("/api/bookings").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.seats.length()").value(2))
                .andReturn().getResponse().getContentAsString();
        long bookingId = om.readTree(created).get("id").asLong();

        // Đặt trùng ghế -> 409
        mvc.perform(post("/api/bookings").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isConflict());

        // Ghế đã bị đánh dấu taken trong sơ đồ
        mvc.perform(get("/api/showtimes/" + showtimeId + "/seats"))
                .andExpect(status().isOk());

        // Huỷ vé -> 204, sau đó ghế được đặt lại bình thường
        mvc.perform(delete("/api/bookings/" + bookingId).header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
        mvc.perform(post("/api/bookings").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isCreated());
    }

    @Test
    void userCannotCancelSomeoneElsesTicket() throws Exception {
        String userToken = login("user", "user123");
        String adminToken = login("admin", "admin123");

        String showtimes = mvc.perform(get("/api/showtimes")).andReturn().getResponse().getContentAsString();
        long showtimeId = om.readTree(showtimes).get(1).get("id").asLong();

        String created = mvc.perform(post("/api/bookings").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"showtimeId\":" + showtimeId + ",\"seats\":[\"F1\"]}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long bookingId = om.readTree(created).get("id").asLong();

        mvc.perform(delete("/api/bookings/" + bookingId).header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }
}
