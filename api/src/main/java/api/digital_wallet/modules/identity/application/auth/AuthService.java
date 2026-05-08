package api.digital_wallet.modules.identity.application.auth;

import api.digital_wallet.modules.identity.domain.RefreshToken;
import api.digital_wallet.modules.identity.infra.dto.response.TokenResponse;
import api.digital_wallet.modules.identity.infra.repository.RefreshTokenRepository;
import api.digital_wallet.modules.identity.infra.security.JwtProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserDetailsService userDetailsService;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtProvider jwtProvider,
                       RefreshTokenRepository refreshTokenRepository,
                       UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userDetailsService = userDetailsService;
    }

    public TokenResponse login(String email, String password) {
        var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        String accessToken = jwtProvider.generateToken(auth);
        String refreshToken = UUID.randomUUID().toString();

        refreshTokenRepository.deleteByUserEmail(email);
        refreshTokenRepository.save(new RefreshToken(refreshToken, email, Instant.now().plusSeconds(604800)));

        return new TokenResponse(accessToken, refreshToken);
    }

    public TokenResponse refresh(String refreshToken) {
        return refreshTokenRepository.findById(refreshToken)
                .map(token -> {
                    refreshTokenRepository.deleteById(token.token());

                    var userDetails = userDetailsService.loadUserByUsername(token.userEmail());
                    var newAccessToken = jwtProvider.generateToken(userDetails);
                    String newRefreshToken = UUID.randomUUID().toString();
                    refreshTokenRepository.save(new RefreshToken(newRefreshToken, token.userEmail(), Instant.now().plusSeconds(604800)));

                    return new TokenResponse(newAccessToken, newRefreshToken);
                })
                .orElseThrow(() -> new RuntimeException("Refresh Token inválido ou expirado"));
    }
}