package com.ambrosia.profile_service.keycloak.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleRepresentation{
    @JsonInclude(value = Include.NON_NULL)
    private String id;

    @JsonInclude(value = Include.NON_NULL)
    private String name;

    @JsonInclude(value = Include.NON_NULL)
    private String description;

    @JsonInclude(value = Include.NON_NULL)
    private Boolean scopeParamRequired;

    @JsonInclude(value = Include.NON_NULL)
    private Boolean composite;

    @JsonInclude(value = Include.NON_NULL)
    private Boolean clientRole;
    
    @JsonInclude(value = Include.NON_NULL)
    private String containerId;
    
    @JsonInclude(value = Include.NON_NULL)
    private Map<String, List<String>> attributes;
}