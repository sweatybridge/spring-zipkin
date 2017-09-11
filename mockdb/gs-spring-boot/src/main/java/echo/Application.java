package echo;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.instrument.async.TraceableExecutorService;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableAsync
public class Application extends AsyncConfigurerSupport {

  @Autowired
  private BeanFactory beanFactory;

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Override
  public Executor getAsyncExecutor() {
    return new TraceableExecutorService(beanFactory, Executors.newFixedThreadPool(2));
  }
}
