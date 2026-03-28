package api.digital_wallet.unit.modules.finance.application.service.transfer;

import api.digital_wallet.modules.finance.application.cmd.TransferCmd;
import api.digital_wallet.modules.finance.application.service.transfer.TransferAppService;
import api.digital_wallet.modules.finance.domain.enums.CurrencyType;
import api.digital_wallet.modules.finance.domain.port.repository.TransactionRepositoryPort;
import api.digital_wallet.modules.finance.domain.port.repository.WalletRepositoryPort;
import api.digital_wallet.modules.finance.domain.port.service.ExchangeServicePort;
import api.digital_wallet.modules.finance.domain.port.service.TransferServicePort;
import api.digital_wallet.modules.finance.domain.port.transfer.TransferAuthorization;
import api.digital_wallet.modules.finance.domain.wallet.Wallet;
import api.digital_wallet.modules.finance.exception.FinanceDomainException;
import api.digital_wallet.modules.finance.exception.FinanceErrorCode;
import api.digital_wallet.shared.outbox.OutboxServicePort;
import api.digital_wallet.shared.value.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test: Transfer Application Service")
class TransferAppServiceTest {

    @Mock private WalletRepositoryPort walletRepository;
    @Mock private TransactionRepositoryPort transactionRepository;
    @Mock private ExchangeServicePort exchangeService;
    @Mock private OutboxServicePort outboxService;
    @Mock private TransferAuthorization transferAuthorization;
    @Mock private TransferServicePort transferService;

    @InjectMocks
    private TransferAppService transferAppService;

    private Wallet origin;
    private Wallet destination;
    private TransferCmd cmd;

    @BeforeEach
    void setUp() {
        origin = new Wallet();
        ReflectionTestUtils.setField(origin, "id", UUID.randomUUID());
        origin.setCurrency(CurrencyType.BRL);

        destination = new Wallet();
        ReflectionTestUtils.setField(destination, "id", UUID.randomUUID());
        destination.setCurrency(CurrencyType.BRL);

        cmd = new TransferCmd(UUID.randomUUID(), Money.of(100), UUID.randomUUID());
    }

    @Test
    @DisplayName("Should execute transfer successfully when all steps pass")
    void shouldExecuteTransferSuccessfully() {
        // Arrange
        when(walletRepository.findByOwnerIdWithLock(cmd.from())).thenReturn(Optional.of(origin));
        when(walletRepository.findByOwnerId(cmd.to())).thenReturn(Optional.of(destination));
        when(exchangeService.convert(any(), any(), any())).thenReturn(cmd.amount());
        when(transferAuthorization.authorize()).thenReturn(true);

        // Act
        var result = transferAppService.execute(cmd);

        // Assert
        assertThat(result).isNotNull();
        verify(transferService).execute(eq(origin), eq(destination), any());
        verify(walletRepository).save(origin);
        verify(walletRepository).save(destination);
        verify(transactionRepository).saveAll(any());
        verify(outboxService).saveEvent(any(), eq("Finance"));
    }

    @Test
    @DisplayName("Should throw exception when origin wallet is not found")
    void shouldFailWhenOriginNotFound() {
        // Arrange
        when(walletRepository.findByOwnerIdWithLock(cmd.from())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> transferAppService.execute(cmd))
                .isInstanceOf(FinanceDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", FinanceErrorCode.WALLET_NOT_FOUND);

        verifyNoInteractions(transferService, outboxService);
    }

    @Test
    @DisplayName("Should call transactions.fail() and save when authorization is denied")
    void shouldFailAndSaveWhenAuthorizationDenied() {
        // Arrange
        when(walletRepository.findByOwnerIdWithLock(cmd.from())).thenReturn(Optional.of(origin));
        when(walletRepository.findByOwnerId(cmd.to())).thenReturn(Optional.of(destination));
        when(exchangeService.convert(any(), any(), any())).thenReturn(cmd.amount());
        when(transferAuthorization.authorize()).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> transferAppService.execute(cmd))
                .isInstanceOf(FinanceDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", FinanceErrorCode.UNAUTHORIZED_TRANSFER);

        verify(transactionRepository).saveAll(any());
        verifyNoInteractions(transferService);
    }
}