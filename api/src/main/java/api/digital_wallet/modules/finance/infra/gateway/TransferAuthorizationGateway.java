package api.digital_wallet.modules.finance.infra.gateway;

import api.digital_wallet.modules.finance.domain.port.transfer.TransferAuthorization;
import api.digital_wallet.modules.finance.domain.port.transfer.response.AuthorizationResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TransferAuthorizationGateway implements TransferAuthorization {

    private final RestClient restClient;

    public TransferAuthorizationGateway(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("https://util.devi.tools/api").build();
    }

    @Override
    public Boolean authorize() {
        try {
            AuthorizationResponse response = restClient.get()
                    .uri("/v2/authorize")
                    .retrieve()
                    .body(AuthorizationResponse.class);

            return response != null &&
                    response.data() != null &&
                    response.data().authorization();

        } catch (Exception e) {
            return false;
        }
    }
}