package com.ambrosia.collaboration_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

import com.ambrosia.content_service.grpc.ContentServiceGrpc;

@Configuration 
public class GrpcClient {
    @Bean 
    ContentServiceGrpc.ContentServiceBlockingStub stub(GrpcChannelFactory channelFactory){
        return ContentServiceGrpc.newBlockingStub(channelFactory.createChannel("content-channel"));
    }
}
