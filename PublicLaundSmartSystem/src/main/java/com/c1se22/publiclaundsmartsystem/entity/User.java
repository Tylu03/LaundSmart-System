package com.c1se22.publiclaundsmartsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    @Column(name = "password", nullable = false, length = 100)
    private String password;
    @Column(name = "fullname", nullable = false, length = 50)
    private String fullname;
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    @Column(name = "phone", nullable = false, unique = true, length = 10)
    private String phone;
    @Column(name = "balance", precision = 10, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;
    @Column(name = "is_active")
    private Boolean isActive = Boolean.TRUE;
    @Column(name = "created_at")
    private LocalDate createdAt;
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    )
    private Set<Role> roles;
}
