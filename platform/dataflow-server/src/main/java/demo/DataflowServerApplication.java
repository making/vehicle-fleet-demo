package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.dataflow.server.EnableDataFlowServer;

@SpringBootApplication
@EnableDataFlowServer
@EnableDiscoveryClient
public class DataflowServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataflowServerApplication.class, args);
	}
}
