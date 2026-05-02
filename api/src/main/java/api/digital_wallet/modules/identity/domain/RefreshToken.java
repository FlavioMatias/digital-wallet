package api.digital_wallet.modules.identity.domain;


import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.Instant;

@RedisHash(value = "refresh_tokens", timeToLive = 604800)
public record RefreshToken(
        @Id String token,
        @Indexed String userEmail,
        Instant expiryDate
) {}