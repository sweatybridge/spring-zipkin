package echo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EchoController {

  private static final int count = Integer.parseInt(System.getProperty("noop.count", "1"));

  private final Tracer tracer;

  @Autowired
  public EchoController(Tracer tracer) {
    this.tracer = tracer;
  }

  @RequestMapping("/echo")
  public String echo(@RequestParam String name) {
    Span parent = tracer.createSpan("echo");
    try {
      for (int i = 0; i < count; i++) {
        Span child = tracer.createSpan("noop");
        try {
          noop();
        } finally {
          tracer.close(child);
        }
      }
    } finally {
      tracer.close(parent);
    }

    return name;
  }

  private void noop() {
  }
}
