package com.ffreitas.taskmaster.configuration;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Taskmaster API",
                version = "1.0.0",
                description = "API for Taskmaster",
                contact = @Contact(
                        name = "Francisco Freitas",
                        email = "francisco.freitas.ff@gmail.com",
                        url = "https://www.linkedin.com/in/francisco-freitas-a289b91b3"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                ),
                termsOfService = "https://www.apache.org/licenses/LICENSE-2.0.html"
        ),
        servers = {
                @Server(
                        description = "Local Server",
                        url = "http://localhost:8080"
                )
        },
        externalDocs = @ExternalDocumentation(
                description = "Spring Boot Documentation",
                url = "https://spring.io/projects/spring-boot"
        )
)
@SecurityScheme(
        name = "JSON Web Token (JWT)",
        description = "JSON Web Token (JWT) is an open standard (RFC 7519) that " +
                "defines a compact and self-contained way for securely transmitting information " +
                "between parties as a JSON object. This information can be verified and trusted " +
                "because it is digitally signed. JWTs can be signed using a secret (with the HMAC algorithm)" +
                " or a public/private key pair using RSA or ECDSA.",
        scheme = "Bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfiguration {
}
