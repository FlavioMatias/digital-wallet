package api.digital_wallet.modules.identity.infra.repository;

import api.digital_wallet.modules.identity.domain.profile.Profile;
import api.digital_wallet.shared.value.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    boolean existsByDocument(Document document);
}
