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
package org.acme.schooltimetabling;

import static org.awaitility.Awaitility.await;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.activemq.artemis.core.security.CheckType;
import org.apache.activemq.artemis.core.security.Role;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.spi.core.security.ActiveMQSecurityManager;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class ActiveMQEmbeddedBroker implements QuarkusTestResourceLifecycleManager {

    private final EmbeddedActiveMQ server = new EmbeddedActiveMQ();

    public Map<String, String> start() {
        try {
            server.setSecurityManager(new ActiveMQSecurityManager() {
                @Override
                public boolean validateUser(String user, String password) {
                    return true;
                }

                @Override
                public boolean validateUserAndRole(String user, String password, Set<Role> roles, CheckType checkType) {
                    return true;
                }
            });
            server.start();
            await().timeout(30, TimeUnit.SECONDS).until(() -> server.getActiveMQServer().isStarted());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to start embedded ActiveMQ broker.", e);
        }
        return Collections.emptyMap();
    }

    public void stop() {
        try {
            if (server != null) {
                server.stop();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to stop embedded ActiveMQ broker.", e);
        }
    }

}
