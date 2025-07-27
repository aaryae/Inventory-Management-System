package com.example.inventorymanagementsystem.service.impl;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomOidcUserService extends OidcUserService {
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);

        Map<String, Object> attributes = new HashMap<>(oidcUser.getClaims());
        String username = oidcUser.getPreferredUsername();
        attributes.put("custom_username", username);

        return new DefaultOidcUser(oidcUser.getAuthorities(),
                oidcUser.getIdToken(), oidcUser.getUserInfo(), "email");
    }
}