package org.ambrosia.notification_service.notification.api;

import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.ambrosia.notification_service.core.ActorFactory;
import org.ambrosia.notification_service.notification.api.dto.NotificationFilter;
import org.ambrosia.notification_service.notification.api.dto.NotificationResponse;
import org.ambrosia.notification_service.notification.application.query.NotificationQueryService;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.UUID;

@Authenticated
@Path("/api/v1/me/notification")
public class NotificationController {
    private final NotificationQueryService notificationQueryService;
    
    @Inject
    ActorFactory actorFactory;
    
    public NotificationController(NotificationQueryService notificationQueryService){
        this.notificationQueryService = notificationQueryService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<NotificationResponse>> getNotifications(
            @BeanParam NotificationFilter filter){
        return notificationQueryService.getNotifications(filter, actorFactory.get());
    }

    @GET
    @Path("/new")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Long> countNewNotifications(){
        return notificationQueryService.getNewNotificationsCount(actorFactory.get());
    }
}
