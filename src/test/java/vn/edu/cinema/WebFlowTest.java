package vn.edu.cinema;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Kiểm tra giao diện web: trang công khai, i18n, cookie và chặn truy cập trái quyền. */
@SpringBootTest
@AutoConfigureMockMvc
class WebFlowTest {

    @Autowired
    MockMvc mvc;

    @Test
    void homePageRendersVietnameseByDefault() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Phim đang chiếu")));
    }

    @Test
    void langParamSwitchesToEnglishAndSetsCookie() throws Exception {
        mvc.perform(get("/").param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("lang"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Now showing")));
    }

    @Test
    void movieDetailSetsRecentMoviesCookie() throws Exception {
        mvc.perform(get("/movies/1"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("recentMovies"));
    }

    @Test
    void seatSelectionRedirectsToLoginWhenAnonymous() throws Exception {
        mvc.perform(get("/showtimes/1/seats"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void normalUserCannotOpenAdminPages() throws Exception {
        mvc.perform(get("/admin/movies")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCanOpenAdminPages() throws Exception {
        mvc.perform(get("/admin/movies")).andExpect(status().isOk());
        mvc.perform(get("/admin/showtimes")).andExpect(status().isOk());
    }
}
