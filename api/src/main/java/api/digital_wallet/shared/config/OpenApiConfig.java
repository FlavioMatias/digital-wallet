package api.digital_wallet.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Digital Wallet API")
                        .version("1.0.0")
                        .description("Plataforma de carteira digital e transferências resilientes.")
                        .contact(new Contact()
                                .name("Suporte Técnico")
                                .email("contato@vaultpay.com")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Bean
    public OperationCustomizer addCorrelationIdHeader() {
        return (operation, handlerMethod) -> {
            if (operation.getParameters() == null) {
                operation.setParameters(new java.util.ArrayList<>());
            }

            operation.addParametersItem(new Parameter()
                    .in("header")
                    .name("X-Correlation-ID")
                    .description("ID de rastreio da requisição")
                    .required(false)
                    .example(java.util.UUID.randomUUID().toString()));

            return operation;
        };
    }
}