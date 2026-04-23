package api.digital_wallet.modules.identity.domain.user;

import api.digital_wallet.modules.identity.domain.profile.Profile;
import api.digital_wallet.shared.domain.BaseEntity;
import api.digital_wallet.shared.value.Email;
import api.digital_wallet.shared.value.converter.EmailConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "users")
@AllArgsConstructor @NoArgsConstructor
public class User extends BaseEntity {

    @Convert(converter = EmailConverter.class)
    @Column(name = "email", nullable = false, unique = true)
    private Email email;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToOne(mappedBy = "user")
    private Profile profile;
}
