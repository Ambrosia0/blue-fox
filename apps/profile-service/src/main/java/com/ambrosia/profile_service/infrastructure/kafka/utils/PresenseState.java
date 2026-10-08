package com.ambrosia.profile_service.infrastructure.kafka.utils;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class PresenseState implements Serializable{
    public byte delta = 0;
    public Long timestamp;
}