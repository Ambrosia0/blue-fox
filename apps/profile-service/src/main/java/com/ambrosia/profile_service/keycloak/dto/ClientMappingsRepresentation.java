package com.ambrosia.profile_service.keycloak.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientMappingsRepresentation {
    private String id;
    private String client;
    private List<RoleRepresentation> mappings;
}
