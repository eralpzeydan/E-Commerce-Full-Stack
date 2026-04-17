package com.eralp.ecommerce.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnUnauthorizedOrForbiddenWhenTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/orders/secure-test"))
                .andExpect(result ->
                        assertThat(result.getResponse().getStatus()).isIn(401, 403)
                );
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = {"USER"})
    void shouldAllowAccessToProtectedEndpointWithAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/v1/orders/secure-test"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = {"USER"})
    void shouldReturnForbiddenForUserOnAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isForbidden());
    }
}
