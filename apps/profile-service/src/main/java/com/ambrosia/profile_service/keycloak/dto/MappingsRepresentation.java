package com.ambrosia.profile_service.keycloak.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @see https://www.keycloak.org/docs-api/latest/rest-api/index.html#MappingsRepresentation
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MappingsRepresentation {
    @JsonInclude(value = Include.NON_NULL)
    private List<RoleRepresentation> realmMappings;

    @JsonInclude(value = Include.NON_NULL)
    private List<ClientMappingsRepresentation> clientMappings;
}
