package views.cache

import com.github.benmanes.caffeine.cache.Cache

import javax.inject.Inject

class CacheStatusTemplate @Inject() (cache: Cache[java.lang.Integer, Any]) {
  def render() = views.html.cache.caffeine.render(cache)
}
