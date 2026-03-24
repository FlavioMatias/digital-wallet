package api.digital_wallet.modules.identity.domain.profile;

import api.digital_wallet.modules.identity.domain.profile.enums.ProfileType;
import api.digital_wallet.modules.identity.domain.user.User;
import api.digital_wallet.shared.domain.BaseEntity;
import api.digital_wallet.shared.value.Document;
import api.digital_wallet.shared.value.converter.DocumentConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "profiles")
@AllArgsConstructor @NoArgsConstructor
public class Profile extends BaseEntity {

    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    @Convert(converter = DocumentConverter.class)
    @Column(name = "document", nullable = false, unique = true, length = 14)
    private Document document;

    @Enumerated(EnumType.STRING)
    @Column(name = "profile_role", nullable = false, length = 20)
    private ProfileType role;

    @OneToOne
    private User user;
}