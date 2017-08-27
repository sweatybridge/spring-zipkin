package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

// notice there is no tracing code in this class
@RestController
public class HelloController {

  private final EchoService echoService;
  private final RestTemplate restTemplate;
  private final Executor executor;

  @Autowired
  public HelloController(EchoService echoService, RestTemplate restTemplate, Executor executor) {
    this.echoService = echoService;
    this.restTemplate = restTemplate;
    this.executor = executor;
  }

  @RequestMapping("/hello")
  public String hello() throws ExecutionException, InterruptedException {

    CompletableFuture<String> r1 = echoService.findUser("Imperial");
    CompletableFuture<String> r2 = echoService.findUser("College");
    CompletableFuture<String> r3 = echoService.findUser("London");

    CompletableFuture.allOf(r1, r2, r3).join();

    return String.format("My school is %s %s %s.", r1.get(), r2.get(), r3.get());
  }

  @RequestMapping("/search")
  public String search(@RequestParam Integer echo) throws ExecutionException, InterruptedException {
    CompletableFuture<String>[] rs = new CompletableFuture[echo];
    for (int i = 0; i < echo; i++) {
      String url = String.format("http://mockdb:8080/echo?name=%d", i);
      rs[i] = CompletableFuture.supplyAsync(() -> restTemplate.getForObject(url, String.class), executor);
    }
    CompletableFuture.allOf(rs).join();
    return "done";
  }
}
