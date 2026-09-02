package com.ambrosia.content_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

import com.ambrosia.community_service.grpc.CommunityServiceGrpc;
import com.ambrosia.community_service.grpc.CommunityServiceGrpc.CommunityServiceBlockingStub;
import com.ambrosia.profile_service.grpc.ProfileServiceGrpc;
import com.ambrosia.profile_service.grpc.ProfileServiceGrpc.ProfileServiceBlockingStub;


@Configuration
public class GrpcClient {
    @Bean
    ProfileServiceBlockingStub profileServiceStub(GrpcChannelFactory channelFactory){
        return ProfileServiceGrpc.newBlockingStub(channelFactory.createChannel("profile-channel"));
    }

    @Bean
    CommunityServiceBlockingStub communityServiceStub(GrpcChannelFactory channelFactory){
        return CommunityServiceGrpc.newBlockingStub(channelFactory.createChannel("community-channel"));
    }
}
