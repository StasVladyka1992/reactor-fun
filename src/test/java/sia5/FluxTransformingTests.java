package sia5;

import lombok.Data;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class FluxTransformingTests {

    @Test
    public void skipSeveralSeconds() {
        //порядок delay и skip важен
        Flux<Integer> flux = Flux.just(1, 2, 3, 4)
                .delayElements(Duration.ofSeconds(1))
                .skip(Duration.ofSeconds(2));
        StepVerifier.create(flux)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4).verifyComplete();
    }

    @Test
    public void takeSeveralSeconds() {
        Flux<Integer> flux = Flux.just(1, 2, 3, 4)
                .delayElements(Duration.ofSeconds(1))
                .take(Duration.ofSeconds(3));
        StepVerifier.create(flux)
                .expectNext(1)
                .expectNext(2)
                .verifyComplete();
    }

    @Test
    public void filterObjects() {
        Flux<Integer> flux = Flux.range(1, 5)
                .filter(number -> number > 3);

        StepVerifier.create(flux)
                .expectNext(4)
                .expectNext(5)
                .verifyComplete();
    }

    @Test
    public void distinctObjects() {
        Flux<Integer> flux = Flux.just(1, 1, 2, 3)
                .distinct();

        StepVerifier.create(flux)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .verifyComplete();
    }

    @Test
    public void mapObjects() {
        //mapping is performed synchronously as each item is published by the source Flux
        Flux<String> flux = Flux.just(1, 2, 3)
                .map(number -> "Number" + number);

        StepVerifier.create(flux)
                .expectNext("Number1")
                .expectNext("Number2")
                .expectNext("Number3")
                .verifyComplete();
    }

    @Test
    public void flatMap() {
        //с map() асинхронная обработка операций не работала, зато с flatMap в купе с subscribeOn() мы можем получить
        //асинхронную обработку потока данных.
        Flux<Player> playerFlux = Flux.just("Michael Jordan", "Scottie Pippen", "Steve Kerr")
                .flatMap(n -> Mono.just(n))
                .map(p -> {
                    String[] split = p.split("\\s");
                    return new Player(split[0], split[1]);
                })
                .subscribeOn(Schedulers.parallel());

//        Applying common reactive operationsThe upside to using flatMap() and subscribeOn() is that you can increase
//        the throughput of the stream by splitting the work across multiple parallel threads. But because thework
//        is being done in parallel, with no guarantees on which will finish first, there’s noway to know the order of
//        items emitted in the resulting Flux. Therefore, StepVerifieris only able to verify that each item emitted exists
//        in the expected list of Player objectsand that there will be three such items before the Flux completes

        List<Player> playerList = Arrays.asList(new Player("Michael", "Jordan"), new Player("Scottie", "Pippen"), new Player("Steve", "Kerr"));
        StepVerifier.create(playerFlux)
                .expectNextMatches(p -> playerList.contains(p))
                .expectNextMatches(p -> playerList.contains(p))
                .expectNextMatches(p -> playerList.contains(p))
                .verifyComplete();
    }

    @Test
    public void buffer() {
        //каждый элемент stream попадает в ту или иную группу в зависимости от размера буффера.
        //Buffering values from a reactive Flux into non-reactive List collections
        Flux<String> fruitFlux = Flux.just("apple", "orange", "banana", "kiwi", "strawberry");
        Flux<List<String>> bufferedFlux = fruitFlux.buffer(3);
        StepVerifier.create(bufferedFlux)
                .expectNext(Arrays.asList("apple", "orange", "banana"))
                .expectNext(Arrays.asList("kiwi", "strawberry"))
                .verifyComplete();
    }

    @Test
    public void parrallel() {
        Flux.just("apple", "orange", "banana", "kiwi", "strawberry")
                .buffer(3)
                .flatMap(x -> Flux.fromIterable(x)
                        .map(y -> y.toUpperCase())
                        .subscribeOn(Schedulers.parallel()).
                        log())
                .subscribe();
    }


    @Test
    public void collectList() {
        //Cобрать все данные в коллекцию
        Flux<String> fruitFlux = Flux.just("apple", "orange", "banana", "kiwi", "strawberry");
        Mono<List<String>> fruitListMono = fruitFlux.collectList();
        StepVerifier.create(fruitListMono).expectNext(Arrays.asList("apple", "orange", "banana", "kiwi", "strawberry")).verifyComplete();
    }

    @Data
    private static class Player {
        private final String firstName;
        private final String lastName;
    }

}
