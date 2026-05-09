package com.honya.bookstore.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Honya Bookstore API", version = "v1"),
        security = @SecurityRequirement(name = "oauth2AuthCode")
)
@SecurityScheme(
        name = "oauth2AuthCode",
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(
                authorizationCode = @OAuthFlow(
                        authorizationUrl = "http://localhost:8080/realms/honya/protocol/openid-connect/auth",
                        tokenUrl = "http://localhost:8080/realms/honya/protocol/openid-connect/token"
                )
        )
)
public class OpenApiSecurityConfig {
}
