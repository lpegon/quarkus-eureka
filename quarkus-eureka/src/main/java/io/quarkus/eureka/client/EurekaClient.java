/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.quarkus.eureka.client;

import io.quarkus.eureka.client.loadBalancer.LoadBalancer;
import io.quarkus.eureka.exception.EurekaServiceNotFoundException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class EurekaClient {

    private final LoadBalancer loadBalancer;
    private final Client client;

    public EurekaClient(final LoadBalancer loadBalancer) {
        this.client =  ClientBuilder.newBuilder().executorService(Executors.newFixedThreadPool(50)).build();
        this.loadBalancer = loadBalancer;
    }

    public WebTarget app(final String appId) {
        String target = loadBalancer.getHomeUrl(appId).orElseThrow(serviceNotFound(appId));
        return client.target(target);
    }

    private Supplier<EurekaServiceNotFoundException> serviceNotFound(final String appId) {
        return () -> new EurekaServiceNotFoundException(appId.toUpperCase());
    }
}
