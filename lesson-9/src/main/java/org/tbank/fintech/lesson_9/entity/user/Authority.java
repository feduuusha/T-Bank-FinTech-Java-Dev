package org.tbank.fintech.lesson_9.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.tbank.fintech.lesson_9.listener.StatisticEntityListener;

@Entity
@Table(name = "authorities")
@Getter
@Setter
@EntityListeners({StatisticEntityListener.class})
@AllArgsConstructor
@NoArgsConstructor
public class Authority implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authorities_seq")
    @SequenceGenerator(name = "authorities_seq", sequenceName = "authorities_seq", allocationSize = 1)
    private Long id;
    @Column(name = "name")
    private String authority;
}
