package com.poke.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="\"user\"")
@JsonInclude(JsonInclude.Include.NON_NULL) // Coment: Excluye campos nulos del JSON
@SQLDelete(sql = "UPDATE \"user\" SET deleted_at = CURRENT_TIMESTAMP, deleted_by = CURRENT_USER WHERE id=?") // Coment: Define la consulta SQL para el borrado lógico
@Where(clause = "deleted_at IS NULL") // Coment: Filtra las entidades que no han sido borradas lógicamente
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String username;
    String password;
    String email;
    String name;
    boolean active;

    // Coment: Campos para soft delete
    @CreationTimestamp // Coment: Anotación para establecer automáticamente la fecha de creación
    @Column(name = "created_at")
    LocalDateTime createdAt;
    @Column(name = "created_by")
    String createdBy;
    @UpdateTimestamp // Coment: Anotación para actualizar automáticamente la fecha de modificación
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
    @Column(name = "updated_by")
    String updatedBy;
    @Column(name = "deleted_at")
    LocalDateTime deletedAt;
    @Column(name = "deleted_by")
    String deletedBy;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}