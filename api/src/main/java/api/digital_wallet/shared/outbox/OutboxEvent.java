package api.digital_wallet.shared.outbox;

import api.digital_wallet.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class OutboxEvent extends BaseEntity {

    @Column(nullable = false)
    private String aggregateType;

    @Column(nullable = false)
    private String eventType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;

    private LocalDateTime processedAt;


    public void markAsProcessed() {
        this.processedAt = LocalDateTime.now();
    }
}