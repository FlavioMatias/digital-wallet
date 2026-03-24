package api.digital_wallet.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("VaultPay API")
                        .version("1.0.0")
                        .description("Plataforma de carteira digital e transferências resilientes.")
                        .contact(new Contact()
                                .name("Suporte Técnico")
                                .email("contato@vaultpay.com")));
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