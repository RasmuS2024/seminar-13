package seminars.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.lang.Nullable;

public class RedisCacheErrorHandler implements CacheErrorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RedisCacheErrorHandler.class);

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        LOG.warn("Cache GET error for cache={}, key={}: {}", cache.getName(), key, exception.getMessage());
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, @Nullable Object value) {
        LOG.warn("Cache PUT error for cache={}, key={}: {}", cache.getName(), key, exception.getMessage());
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        LOG.warn("Cache EVICT error for cache={}, key={}: {}", cache.getName(), key, exception.getMessage());
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        LOG.warn("Cache CLEAR error for cache={}: {}", cache.getName(), exception.getMessage());
    }
}
