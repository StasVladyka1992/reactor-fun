package sia5;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Stream;

public class FluxMergingTests {

    @Test
    //it's better to use zip operation
    public void mergedFluxes() {
        Flux<String> brands = Flux.fromStream(Stream.of("Audi", "BMW", "Kia", "Toyota"))
                .delayElements(Duration.ofMillis(500));

        Flux<String> models = Flux.fromIterable(Arrays.asList("A3", "X5", "Rio", "Land Cruiser"))
                .delaySubscription(Duration.ofMillis(250))
                .delayElements(Duration.ofMillis(500));

        Flux<String> mergedFlux = brands.mergeWith(models);

        StepVerifier.create(mergedFlux)
                .expectNext("Audi")
                .expectNext("A3")
                .expectNext("BMW")
                .expectNext("X5")
                .expectNext("Kia")
                .expectNext("Rio")
                .expectNext("Toyota")
                .expectNext("Land Cruiser")
                .verifyComplete();
    }


    @Test
    public void zipFluxes() {
        Flux<String> brands = Flux.just("Audi", "BMW", "Kia", "Toyota");
        Flux<String> models = Flux.fromIterable(Arrays.asList("A3", "X5", "Rio", "Land Cruiser"));

        //создает новый Flux, который содержит tuple, включающий по одному элементу из каждого Flux. Tuple - просто контейнер
        //содержащий элементы из Flux
        Flux<Tuple2<String, String>> zippedFlux = Flux.zip(brands, models);

        StepVerifier.create(zippedFlux)
                .expectNextMatches(p ->
                        p.getT1().equals("Audi") && p.getT2().equals("A3"))
                .expectNextMatches(p ->
                        p.getT1().equals("BMW") && p.getT2().equals("X5"))
                .expectNextMatches(p ->
                        p.getT1().equals("Kia") && p.getT2().equals("Rio"))
                .expectNextMatches(p ->
                        p.getT1().equals("Toyota") && p.getT2().equals("Land Cruiser"))
                .verifyComplete();
    }

    @Test
    public void zipFluxesToOneObject() {
        Flux<String> brands = Flux.just("Audi", "BMW", "Kia", "Toyota");
        Flux<String> models = Flux.fromArray(new String[]{"A3", "X5", "Rio", "Land Cruiser"});

        Flux<String> result = Flux.zip(brands, models, (brand, model) -> "car " + brand + " " + model);

        StepVerifier.create(result)
                .expectNext("car Audi A3");
    }

    @Test
    public void zipSelectFirstTypeToPublish() {
        Flux<String> brands = Flux.just("Audi", "BMW", "Kia", "Toyota");
        Flux<String> models = Flux.fromArray(new String[]{"A3", "X5", "Rio", "Land Cruiser"});

        Flux<String> result = Flux.first(brands, models);

        StepVerifier.create(result)
                .expectNext("Audi")
                .expectNext("BMW")
                .expectNext("Kia")
                .expectNext("Toyota")
                .verifyComplete();
    }

    @Test
    public void fluxSkip3OnCreation() {
        Flux<String> flux = Flux.just("Audi", "BMW", "Kia", "Toyota")
                .skip(3);

        StepVerifier.create(flux)
                .expectNext("Toyota")
                .verifyComplete();
    }

    @Test
    public void fluxSkipOnCreationByTime() {
        Flux<String> flux = Flux.just("Audi", "BMW", "Kia", "Toyota")
                //порядок важен.
                //время, на которое откладывается вызов элемента
                //Стрим открыт -> Ждем одну секунду -> Берется audi (но мы ее пропускаем, т.к. skip на 2 сек, т.е. в течение
                //2 секунд надо игнорить все элементы)
                .delayElements(Duration.ofSeconds(1))
                .skip(Duration.ofSeconds(2));

        StepVerifier.create(flux)
                .expectNext("BMW")
                .expectNext("Kia")
                .expectNext("Toyota")
                .verifyComplete();
    }

    @Test
    public void fluxTakeWithTime() {
        Flux<String> flux = Flux.just("Audi", "BMW", "Kia", "Toyota");
    }
}
