package sia5;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class FluxCreationTests {

    @Test
    public void createFluxJust() {
        Flux<String> names = Flux.just(
                "Stas", "Vitya", "Maks"
        );

        //lambda which is used here is Consumer, which is used to create Subscriber. I can also pass Subscriber
        names.subscribe(
                System.out::println
        );
    }

    @Test
    public void createFluxWithVerifier() {
        Flux<String> names = Flux.just(
                "Stas", "Vitya", "Maks"
        );

        //StepVerifier subscribes to the Publisher(Flux)
        StepVerifier.create(names)
                .expectNext("Stas")
                .expectNext("Vitya")
                .expectNext("Maks")
                .verifyComplete();
    }

    @Test
    public void createFluxFromArray() {
        Flux<String> names = Flux.fromArray(new String[]{"Stas", "Vitya", "Maks"});
        StepVerifier.create(names)
                //можно выбрать что-то одно, нельзя одновременно. expectNextCount/expectNext
                .expectNextCount(3)
                //.expectNext("Stas", "Vitya", "Maks")
                .verifyComplete();
    }

    @Test
    public void createFluxFromIterable() {
        List<String> list = Arrays.asList("Stas", "Vitya", "Maks");
        Flux<String> names = Flux.fromIterable(list);
        StepVerifier.create(names)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void createFluxFromStream() {
        Flux<String> names = Flux.fromStream(Stream.of("Stas", "Vitya", "Maks"));
        StepVerifier.create(names)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void createFluxFromRange() {
        Flux<Integer> intervalFlux = Flux.range(1, 5);

        StepVerifier.create(intervalFlux)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .expectNext(5)
                .verifyComplete();
    }

    @Test
    public void createFluxFromInterval() {
        //insert long with increment 1 starting from 0.
        Flux<Long> intervalFlux = Flux.interval(Duration.ofSeconds(1))
                .take(5);

        StepVerifier.create(intervalFlux)
                .expectNext(0L)
                .expectNext(1L)
                .expectNext(2L)
                .expectNext(3L)
                .expectNext(4L)
                .verifyComplete();
    }
}
