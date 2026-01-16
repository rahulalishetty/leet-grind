import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class ExecutorsLatch {
  public List<String> crawl(String startUrl, HtmlParser htmlParser) {
    String hostname = getHostname(startUrl);

    Set<String> visited = ConcurrentHashMap.newKeySet();
    visited.add(startUrl);

    int threads = Math.max(4, Runtime.getRuntime().availableProcessors());
    ExecutorService executor = Executors.newFixedThreadPool(threads);

    AtomicInteger inFlight = new AtomicInteger(1);
    CountDownLatch done = new CountDownLatch(1);

    // Submit work without blocking in worker threads
    submit(executor, htmlParser, hostname, visited, startUrl, inFlight, done);

    try {
      done.await(); // wait for all scheduled tasks to finish
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } finally {
      executor.shutdownNow();
    }

    return new ArrayList<>(visited);
  }

  private void submit(
      ExecutorService executor,
      HtmlParser htmlParser,
      String hostname,
      Set<String> visited,
      String url,
      AtomicInteger inFlight,
      CountDownLatch done) {
    executor.execute(() -> {
      try {
        for (String next : htmlParser.getUrls(url)) {
          if (!isSameHostname(next, hostname))
            continue;

          // schedule exactly once
          if (visited.add(next)) {
            inFlight.incrementAndGet();
            submit(executor, htmlParser, hostname, visited, next, inFlight, done);
          }
        }
      } finally {
        if (inFlight.decrementAndGet() == 0) {
          done.countDown();
        }
      }
    });
  }

  // Returns "http://example.org" (scheme + host) per your original style
  private String getHostname(String url) {
    int idx = url.indexOf('/', 7); // skip "http://"
    return (idx != -1) ? url.substring(0, idx) : url;
  }

  private boolean isSameHostname(String url, String hostname) {
    if (!url.startsWith(hostname))
      return false;
    return url.length() == hostname.length() || url.charAt(hostname.length()) == '/';
  }
}
