package uk.co.senapt.nats.test;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import uk.co.senapt.crm.processing.path.service.model.IBus;

public class NatsConsumerRunner implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(NatsConsumerRunner.class);
    private final IBus bus;
    private final Tracer tracer;
    private final Propagator propagator;

    public NatsConsumerRunner(final Tracer tracer, final Propagator propagator, final IBus bus) {
        this.tracer = tracer;
        this.propagator = propagator;
        this.bus = bus;
    }

    @Override
    public void run(final ApplicationArguments args) throws Exception {
        bus.addSubscriber("test-topic", (topic, message) -> {
            Thread.yield();
        });
    }
}
