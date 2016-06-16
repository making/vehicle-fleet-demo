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

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import demo.model.CurrentPosition;
import org.springframework.stereotype.Component;

@Component
public class FleetLocationWs {

    @Autowired
    SimpMessagingTemplate template;
    @Autowired
    ObjectMapper objectMapper;

    @RabbitListener(queues = "ingest.fleet-location.outcome")
    public void receiveFleetLocation(byte[] input) throws Exception {
        CurrentPosition payload = this.objectMapper.readValue(input, CurrentPosition.class);
        this.template.convertAndSend("/topic/vehicles", payload);
    }

}
