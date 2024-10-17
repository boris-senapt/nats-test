package uk.co.senapt.nats.test;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import io.nats.client.Nats;
import io.nats.client.api.DiscardPolicy;
import io.nats.client.api.RetentionPolicy;
import io.nats.client.api.StorageType;
import io.nats.client.api.StreamConfiguration;
import io.nats.spring.boot.autoconfigure.NatsProperties;
import java.time.Duration;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import uk.co.senapt.crm.common.ext.eventStore.NoopEventStore;
import uk.co.senapt.crm.common.ext.nats.js.BaseNatsJetStreamServerEventBus;
import uk.co.senapt.crm.processing.path.service.model.IBus;

@SpringBootApplication
@EnableConfigurationProperties(NatsProperties.class)
public class NatsTestConfiguration {

  @Bean(destroyMethod = "stopSubscribers")
  public BaseNatsJetStreamServerEventBus bus(
      @Value("${spring.application.name}") final String serviceName,
      final NatsProperties natsProperties)
      throws Exception {
    final var options = natsProperties.toOptionsBuilder().build();
    final var streamName = "SENAPT";
    try (final var nats = Nats.connect(options)) {
      StreamConfiguration streamConfig =
          StreamConfiguration.builder()
              .name(streamName)
              .subjects("*")
              .retentionPolicy(RetentionPolicy.Limits)
              .storageType(StorageType.File)
              .discardPolicy(DiscardPolicy.Old)
              .maxMessages(1_000_000L)
              .maxAge(Duration.ofDays(7))
              .replicas(3)
              .build();
      final var streams = nats.jetStreamManagement().getStreamNames();
      if (!streams.contains(streamName)) {
        nats.jetStreamManagement().addStream(streamConfig);
      }
    }
    return new BaseNatsJetStreamServerEventBus(
        "_",
        "-",
        natsProperties.getServer(),
        serviceName,
        streamName,
        NoopEventStore.INSTANCE,
        10,
        600,
        1000,
        natsProperties.getNkey(),
        false,
        List.of());
  }

  @Bean
  @Profile("consumer")
  public NatsConsumerRunner natsConsumerRunner(
      final Tracer tracer, final Propagator propagator, final IBus bus) throws Exception {
    return new NatsConsumerRunner(tracer, propagator, bus);
  }

  @Bean
  @Profile("producer")
  public NatsProducerRunner natsProducerRunner(
      final Tracer tracer, final Propagator propagator, final IBus bus) throws Exception {
    return new NatsProducerRunner(tracer, propagator, bus);
  }
}
