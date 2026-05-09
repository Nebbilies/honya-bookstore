package com.honya.bookstore.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OpenApiSecurityConfigTest {

    @Test
    void openApiSecurityConfigUsesOAuth2AuthorizationCodeFlowForSwaggerLogin() {
        SecurityScheme scheme = OpenApiSecurityConfig.class.getAnnotation(SecurityScheme.class);
        assertNotNull(scheme);
        assertEquals("oauth2AuthCode", scheme.name());
        assertEquals(SecuritySchemeType.OAUTH2, scheme.type());

        OAuthFlows flows = scheme.flows();
        assertNotNull(flows);

        OAuthFlow authorizationCode = flows.authorizationCode();
        assertNotNull(authorizationCode);
        assertEquals("http://localhost:8080/realms/honya/protocol/openid-connect/auth", authorizationCode.authorizationUrl());
        assertEquals("http://localhost:8080/realms/honya/protocol/openid-connect/token", authorizationCode.tokenUrl());

        SecurityRequirement requirement = OpenApiSecurityConfig.class
                .getAnnotation(io.swagger.v3.oas.annotations.OpenAPIDefinition.class)
                .security()[0];
        assertEquals("oauth2AuthCode", requirement.name());
    }
}
