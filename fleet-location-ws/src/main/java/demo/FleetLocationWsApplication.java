/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package demo;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Main entry point for the Fleet Location Updater application.
 *
 * @author Gunnar Hillert
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
public class FleetLocationWsApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(FleetLocationWsApplication.class, args);
    }

    @Bean
    Queue fleetQueue() {
        return new Queue("ingest.fleet-location.outcome");
    }

    @Bean
    TopicExchange fleetExchange() {
        return new TopicExchange("fleet");
    }

    @Bean
    Binding binding() {
        return BindingBuilder.bind(fleetQueue()).to(fleetExchange()).with("fleet");
    }

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
