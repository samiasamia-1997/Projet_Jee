package org.mrb.authservice.sec.service;

import org.mrb.authservice.sec.entities.AppRole;
import org.mrb.authservice.sec.entities.AppUser;

import java.util.List;

public interface AccountService {
    AppUser addNewUser(AppUser appUser);
    AppRole addNewRole(AppRole appRole);
    void addRoleToUser(String username, String roleName);
    AppUser loadUserByUserName(String username);
    List<AppUser> allUsers();
}
