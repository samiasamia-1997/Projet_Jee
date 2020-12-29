package org.mrb.authservice.sec.repo;

import org.mrb.authservice.sec.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository  extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
}
