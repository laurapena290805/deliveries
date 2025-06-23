package com.nova.deliveries.pruebas_Unitarias;


import com.nova.deliveries.client.UserClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserClient userClient;

    private final String userServiceUrl = "http://user-service";

    @BeforeEach
    void setUp() {
        userClient = new UserClient(restTemplate, userServiceUrl);
    }

    @Test
    void isRepartidor_WhenUserIsRepartidor_ShouldReturnTrue() {
        Long userId = 1L;
        String url = userServiceUrl + "/api/users/" + userId + "/has-role?role=REPARTIDOR";

        when(restTemplate.getForEntity(url, Boolean.class))
                .thenReturn(ResponseEntity.ok(true));

        boolean result = userClient.isRepartidor(userId);

        assertTrue(result);
        verify(restTemplate).getForEntity(url, Boolean.class);
    }

    @Test
    void isRepartidor_WhenUserIsNotRepartidor_ShouldReturnFalse() {
        Long userId = 1L;
        String url = userServiceUrl + "/api/users/" + userId + "/has-role?role=REPARTIDOR";

        when(restTemplate.getForEntity(url, Boolean.class))
                .thenReturn(ResponseEntity.ok(false));

        boolean result = userClient.isRepartidor(userId);

        assertFalse(result);
    }

    @Test
    void getUserName_ShouldReturnUserName() {
        Long userId = 1L;
        String expectedName = "John Doe";
        String url = userServiceUrl + "/api/users/" + userId + "/name";

        when(restTemplate.getForEntity(url, String.class))
                .thenReturn(ResponseEntity.ok(expectedName));

        String result = userClient.getUserName(userId);

        assertEquals(expectedName, result);
        verify(restTemplate).getForEntity(url, String.class);
    }
}