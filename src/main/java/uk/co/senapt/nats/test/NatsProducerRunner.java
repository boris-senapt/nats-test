package uk.co.senapt.nats.test;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import uk.co.senapt.crm.processing.path.service.model.IBus;

import java.nio.charset.StandardCharsets;

public class NatsProducerRunner implements ApplicationRunner {
  private static final Logger logger = LoggerFactory.getLogger(NatsProducerRunner.class);
  private final IBus bus;
  private final Tracer tracer;
  private final Propagator propagator;

  public NatsProducerRunner(final Tracer tracer, final Propagator propagator, final IBus bus) {
    this.tracer = tracer;
    this.propagator = propagator;
    this.bus = bus;
  }

  @Override
  public void run(final ApplicationArguments args) throws Exception {
    for (int i = 0; ; i++) {
      final int idx = i;
      bus.publish("test-topic", () -> ("test" + idx).getBytes(StandardCharsets.UTF_8));
    }
  }
}
