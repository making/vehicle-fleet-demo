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
package demo.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.model.DirectionInput;
import demo.model.GpsSimulatorRequest;
import demo.model.Point;
import demo.model.VehicleStatus;
import demo.model.ServiceLocation;
import demo.model.SimulatorFixture;
import demo.service.GpsSimulatorFactory;
import demo.service.KmlService;
import demo.service.PathService;
import demo.support.FaultCodeUtils;
import demo.support.NavUtils;
import demo.task.GpsSimulator;
import demo.task.GpsSimulatorInstance;

/**
 *
 * @author Gunnar Hillert
 *
 */
@RestController
@RequestMapping("/api")
public class RestApi {

	@Autowired
	private PathService pathService;

	@Autowired
	private KmlService kmlService;

	@Autowired
	private GpsSimulatorFactory gpsSimulatorFactory;

	@Autowired
	private AsyncTaskExecutor taskExecutor;

	@Autowired
	ObjectMapper objectMapper;

	private Map<Long, GpsSimulatorInstance> taskFutures = new HashMap<>();

	@RequestMapping("/dc")
	public List<GpsSimulatorInstance>dc(HttpServletRequest request) {
		final SimulatorFixture fixture = this.pathService.loadSimulatorFixture();

		final List<GpsSimulatorInstance> instances = new ArrayList<>();
		final List<Point> lookAtPoints = new ArrayList<>();

		final Set<Long> instanceIds = new HashSet<>(taskFutures.keySet());

		for (GpsSimulatorRequest gpsSimulatorRequest : fixture.getGpsSimulatorRequests()) {

			final GpsSimulator gpsSimulator = gpsSimulatorFactory.prepareGpsSimulator(gpsSimulatorRequest);
			lookAtPoints.add(gpsSimulator.getStartPoint());
			instanceIds.add(gpsSimulator.getId());

			final Future<?> future = taskExecutor.submit(gpsSimulator);
			final GpsSimulatorInstance instance = new GpsSimulatorInstance(gpsSimulator.getId(), gpsSimulator, future);
			taskFutures.put(gpsSimulator.getId(), instance);
			instances.add(instance);
		}

		if (fixture.usesKmlIntegration()) {
			kmlService.setupKmlIntegration(instanceIds, NavUtils.getLookAtPoint(lookAtPoints), getKmlUrl(request));
		}

		return instances;
	}

	@RequestMapping("/status")
	public Collection<GpsSimulatorInstance> status() {
		return taskFutures.values();
	}

	@RequestMapping("/cancel")
	public int cancel() {
		int numberOfCancelledTasks = 0;
		for (Map.Entry<Long, GpsSimulatorInstance> entry : taskFutures.entrySet()) {
			GpsSimulatorInstance instance = entry.getValue();
			instance.getGpsSimulator().cancel();
			boolean wasCancelled = instance.getGpsSimulatorTask().cancel(true);
			if (wasCancelled) {
				numberOfCancelledTasks++;
			}
		}
		taskFutures.clear();
		this.kmlService.clearKmlInstances();
		return numberOfCancelledTasks;
	}

	@RequestMapping("/directions")
	public List<DirectionInput> directions() {
		return pathService.loadDirectionInput();
	}

	@RequestMapping("/service-locations")
	public List<ServiceLocation> serviceLocations() {
		return pathService.getServiceStations();
	}

	public JsonNode fleet() {
		final InputStream is = this.getClass().getResourceAsStream("/fleet.json");
		try {
			return objectMapper.readValue(is, JsonNode.class);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@RequestMapping("/fixture")
	public SimulatorFixture fixture() {

		final List<DirectionInput> directions = this.pathService.loadDirectionInput();
		final SimulatorFixture fixture = new SimulatorFixture();
		final JsonNode fleet = fleet();
		int i = 0;
		for (DirectionInput directionInput : directions) {

			final GpsSimulatorRequest gpsSimulatorRequest = new GpsSimulatorRequest();
			gpsSimulatorRequest.setExportPositionsToKml(true);
			gpsSimulatorRequest.setExportPositionsToMessaging(true);
			gpsSimulatorRequest.setMove(true);

			String polyline = this.pathService.getCoordinatesFromGoogleAsPolyline(directionInput);
			gpsSimulatorRequest.setVin(fleet.get(i++).get("vin").asText());
			gpsSimulatorRequest.setPolyline(polyline);
			gpsSimulatorRequest.setReportInterval(1000);
			gpsSimulatorRequest.setSpeedInKph(50d + new Random().nextDouble() * 100);
			gpsSimulatorRequest.setExportPositionsToMessaging(true);
			gpsSimulatorRequest.setSecondsToError( new Random().nextInt(60));
			gpsSimulatorRequest.setVehicleStatus(VehicleStatus.NONE);
			gpsSimulatorRequest.setFaultCode(FaultCodeUtils.getRandomFaultCode());
			fixture.getGpsSimulatorRequests().add(gpsSimulatorRequest);
		}

		return fixture;
	}

	@RequestMapping("/kml/{instanceId}")
	public byte[] getKmlInstance(@PathVariable Long instanceId) {
		return kmlService.getKmlInstance(instanceId);
	}

	@RequestMapping("/gps.kml")
	public byte[] getKmlBootstrapKml() {
		return kmlService.getKmlBootstrap();
	}

	private String getKmlUrl(HttpServletRequest request) {

		final String scheme = request.getScheme();
		final String serverName = request.getServerName();
		final int serverPort = request.getServerPort();
		final String contextPath = request.getContextPath();

		StringBuilder url =  new StringBuilder();
		url.append(scheme).append("://").append(serverName);

		if ((serverPort != 80) && (serverPort != 443)) {
			url.append(":").append(serverPort);
		}

		url.append(contextPath).append("/api/kml/");
		return url.toString();
	}
}
