/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.acme.demoapp.rest;

import java.util.UUID;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

@Path("events")
@ApplicationScoped
public class EventResource {

    private Sse sse;

    private SseBroadcaster sseBroadcaster;

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void listen(@Context SseEventSink eventSink, @Context Sse sse) {
        if (this.sseBroadcaster == null) {
            this.sse = sse;
            this.sseBroadcaster = sse.newBroadcaster();
        }
        this.sseBroadcaster.register(eventSink);
    }

    public void sendEvent(Long planningId) {
        if (sseBroadcaster != null) {
            sseBroadcaster.broadcast(sse.newEventBuilder()
                    .name("message")
                    .id(UUID.randomUUID().toString())
                    .mediaType(MediaType.TEXT_PLAIN_TYPE)
                    .reconnectDelay(3000)
                    .data(planningId)
                    .comment("solver job update")
                    .build());
        }
    }

    @PreDestroy
    public void closeBroadcaster() {
        if (sseBroadcaster != null) {
            sseBroadcaster.close();
        }
    }
}
