package com.ambrosia.community_service.community.model.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.ambrosia.community_service.community.utils.ScopeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "scope")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Scope implements Serializable{
    @Id
    private Short id;

    @Column(value = "scope_type")
    private ScopeEnum scope;
}
