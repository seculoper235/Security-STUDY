package com.example.demo.Domain;

import com.example.demo.Security.Dto.MyAuthority;
import lombok.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.User;

import javax.persistence.*;
import java.util.Set;


// DB 데이터를 받는 객체
@Entity
@Getter
@NoArgsConstructor
@Table(name = "people")
@EqualsAndHashCode(of = "id")
public class People {
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private static final Log logger = LogFactory.getLog(User.class);

    /* OAuth 범위 필드 */
    private String email;

    private String image;

    /* Security 전용 필드 */
    @Id
    private String id;

    private String username;

    private String password;

    private String description;

    @OneToMany(mappedBy = "people", fetch = FetchType.EAGER)
    private Set<MyAuthority> authorities;

    @Builder
    public People(String email, String image, String id, String username, String password, String description, Set<MyAuthority> authorities) {
        this.email = email;
        this.image = image;
        this.id = id;
        this.username = username;
        this.password = password;
        this.description = description;
        this.authorities = authorities;
    }

    public People oAuthData(String image, String username) {
        this.image = image;
        this.username = username;

        return this;
    }
}
