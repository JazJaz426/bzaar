package edu.brown.cs.student.main.caches;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CachedGeo implements Searcher<Map<String, Object>, GeoQuery> {
  private final Searcher<Map<String, Object>, GeoQuery> wrappedSearcher;
  private final LoadingCache<GeoQuery, Collection<Map<String, Object>>> cache;
  private CachedACSInfo cachedGeo;

  public CachedGeo(
      Searcher<Map<String, Object>, GeoQuery> toWrap, int maxCacheSize, int cacheMinutesDuration) {
    this.wrappedSearcher = toWrap;
    this.cache =
        CacheBuilder.newBuilder()
            .maximumSize(maxCacheSize)
            .expireAfterWrite(cacheMinutesDuration, TimeUnit.MINUTES)
            .recordStats()
            .build(
                new CacheLoader<>() {
                  @Override
                  public Collection<Map<String, Object>> load(GeoQuery geoQuery)
                      throws IOException, URISyntaxException, InterruptedException {
                    Collection<Map<String, Object>> originalResults =
                        wrappedSearcher.search(geoQuery);
                    Collection<Map<String, Object>> unmodifiableMaps =
                        originalResults.stream()
                            .map(Collections::unmodifiableMap)
                            .collect(Collectors.toList());
                    return Collections.unmodifiableCollection(unmodifiableMaps);
                  }
                });
  }

  public CacheStats getCacheStats() {
    return cache.stats();
  }

  @Override
  public Collection<Map<String, Object>> search(GeoQuery geoQuery)
      throws IOException, URISyntaxException, InterruptedException {
    CacheStats stats = cache.stats();
    System.out.println("Cache hit count: " + stats.hitCount());
    System.out.println("Cache miss count: " + stats.missCount());
    System.out.println("Cache hit rate: " + stats.hitRate());
    System.out.println("Cache miss rate: " + stats.missRate());
    System.out.println("Load success count: " + stats.loadSuccessCount());
    System.out.println("Load exception count: " + stats.loadExceptionCount());
    System.out.println("Total load time (ns): " + stats.totalLoadTime());
    System.out.println("Average load penalty (ns): " + stats.averageLoadPenalty());
    System.out.println("Eviction count: " + stats.evictionCount());
    return cache.getUnchecked(geoQuery);
  }
}
