package com.example.demo.Domain;

import com.example.demo.Security.Dto.MyAuthority;
import lombok.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.User;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
    @Id
    private String id;

    private String email;

    private String image;

    /* Security 전용 필드 */
    @OneToMany(mappedBy = "people")
    private Set<MyAuthority> authorities;
}
