package sia5;

import org.junit.Test;
import reactor.core.publisher.Mono;

public class MonoFirstExampleTest {

    @Test
    public void MonoExampleTest(){
        Mono.just("Craig")
                .map(t -> t.toUpperCase())
                .map(gt -> "Hello " + gt)
                .subscribe(System.out::println);
    }
}
