package uk.co.senapt.nats.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;

public class NatsProducer {

  public static void main(final String[] args) {
    final var app = new SpringApplication(NatsTestConfiguration.class);
    app.setWebApplicationType(WebApplicationType.NONE);
    app.setAdditionalProfiles("producer");
    app.run(args);
  }
}
