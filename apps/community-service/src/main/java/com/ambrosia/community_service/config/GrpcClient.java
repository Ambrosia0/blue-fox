package com.ambrosia.community_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

import com.ambrosia.profile_service.grpc.ProfileServiceGrpc;

@Configuration
public class GrpcClient {
    @Bean
    ProfileServiceGrpc.ProfileServiceBlockingStub stub(GrpcChannelFactory channelFactory){
        return ProfileServiceGrpc.newBlockingStub(channelFactory.createChannel("profile-channel"));
    }
}

