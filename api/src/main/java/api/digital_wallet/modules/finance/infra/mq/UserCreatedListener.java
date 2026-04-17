package api.digital_wallet.modules.finance.infra.mq;

import api.digital_wallet.modules.finance.application.cmd.WalletCreateCmd;
import api.digital_wallet.modules.finance.infra.mq.event.UserCreatedEvent;
import api.digital_wallet.modules.finance.application.usecase.WalletUserCase;
import api.digital_wallet.modules.finance.domain.enums.CurrencyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCreatedListener {

    private final WalletUserCase walletUserCase;

    @RabbitListener(queues = "user.created.wallet-service")
    public void onUserCreated(UserCreatedEvent event) {
        log.info("Evento recebido: Usuário criado com ID {}", event.userId());

        try {
            CurrencyType currency = event.baseCurrency() != null ?
                    CurrencyType.valueOf(event.baseCurrency()) :
                    CurrencyType.BRL;

            WalletCreateCmd cmd = new WalletCreateCmd(currency, event.userId());

            walletUserCase.create(cmd);

            log.info("Carteira criada com sucesso para o usuário {}", event.userId());
        } catch (Exception e) {
            log.error("Erro ao processar criação de carteira para usuário {}: {}",
                    event.userId(), e.getMessage());

            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}