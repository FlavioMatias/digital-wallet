package api.digital_wallet.modules.identity.infra.repository;
import api.digital_wallet.modules.identity.domain.user.User;
import api.digital_wallet.shared.value.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(Email email);
}