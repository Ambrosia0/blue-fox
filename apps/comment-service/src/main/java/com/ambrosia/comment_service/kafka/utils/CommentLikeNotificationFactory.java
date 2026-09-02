package com.ambrosia.comment_service.kafka.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.ambrosia.comment_service.kafka_events.CommentDelta;
import com.ambrosia.comment_service.kafka_events.CommentDeltas;
import com.ambrosia.comment_service.kafka_events.CommentLikeNotification;
import com.ambrosia.comment_service.like.service.impl.LikeAggregationServiceImpl.CommentPostKey;

public class CommentLikeNotificationFactory {
    public static CommentLikeNotification create(Map<CommentPostKey, int[]> incrementMap){
        var nofificationBuilder = CommentLikeNotification.newBuilder();
        
        var grouped = new HashMap<Long, CommentDeltas.Builder>();
        for(Entry<CommentPostKey, int[]> e: incrementMap.entrySet()){
            var key = e.getKey();
            grouped.computeIfAbsent(
                key.postId(),
                id -> CommentDeltas.newBuilder())
            .addDeltas(
                CommentDelta.newBuilder()
                    .setCommentId(key.commentId())
                    .setDelta(e.getValue()[0])
                    .build()
            );
        }
        grouped.forEach((postId, builder) ->
            nofificationBuilder.putChanges(postId, builder.build())
        );
        return nofificationBuilder.build();
    }
}
