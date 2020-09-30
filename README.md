[ ![Download](https://api.bintray.com/packages/wjtan/maven/tr-recode-cache/images/download.svg) ](https://bintray.com/wjtan/maven/tr-recode-cache/_latestVersion)

# tr-recode-cache - Aspect-Oriented Cache

Aspect-Oriented (AOP) cache based on [Caffeine cache](https://github.com/ben-manes/caffeine) using [Guice](https://github.com/google/guice) for dependency injection.

AOP is implemented using either Guice or compile-time bytecode generation using [ByteBuddy](http://bytebuddy.net).
It also supports thread local caching.

## Maven Dependency
```
<dependency>
  <groupId>com.reincarnation.cache</groupId>
  <artifactId>cache</artifactId>
  <version>0.4.0</version>
</dependency>
```

For compile-time bytecode generation, you need to include the `interceptor-annotation` and `cache-enhancer` as optional dependencies.

```
<dependency>
  <groupId>com.reincarnation.cache</groupId>
  <artifactId>interceptor-annotation</artifactId>
  <version>0.4.0</version>
  <optional>true</optional>
</dependency>

<dependency>
  <groupId>com.reincarnation.cache</groupId>
  <artifactId>cache-enhancer</artifactId>
  <version>0.4.0</version>
  <optional>true</optional>
</dependency>
```

## Usage

To bind the `CacheAdapter` for Caffeine Cache, install `com.reincarnation.cache.caffeine.CaffeineCacheModule` as Guice Module.

### Annotations

`@Cached` enables cache for function. If the cache key is not defined or empty, the method name will be used.
`timeToLiveSeconds` defines the seconds before the cache value is invalidated.
`predicate` is a `Supplier<Boolean>` class, which return true to continue to cache values, and false to disable the cache.

```
@Cached( value = "cachekey", timeToLiveSeconds = seconds, predicate = Predicate.class )
public getValue(){
...
```

`@CacheKey` defines the parameters that will also be used as part of the cache key.
```
@Cached
public getValue(@CacheKey int id){
```

`@CacheWrite` write the value with the cache key. The last parameter of the function will be used as the cache value.
`timeToLiveSeconds` is the optional setting to defines the duration of the cache value.
```
@CacheWrite( value = "cachekey", timeToLiveSeconds = seconds)
public void setValue(int value){
...
```

`@CacheValue` is used to select another parameter as the cache value.
Similarily, `@CacheKey` can be used to add additional parameter as part of the cache key.
```
@CacheWrite( value = "cachekey")
public void setValue(@CacheValue int value, @CacheKey int id){
```

`@CacheRemove` invalidates the value with the key. `@CacheKey` can also be used here.
```
@CacheRemove(value = "cachekey")
public void clear(){
```

`@ThreadLocalCached` enable caching for current thread.
Require to start the cache, inject `ThreadLocalCacheAdapter`, and call `cache.start()`.
Remember to end the cache by calling `cache.end()`.
```
@ThreadLocalCached( value = "cachekey")
public getValue(){
...
```

### Guice AOP

To enable Guice AOP, install `com.reincarnation.cache.guice.GuiceInceptorModule` as Guice Module.

### Compile-time AOP

The compile-time AOP can be executed using either ByteBuddy maven plugin or [sbt-byte-buddy plugin](https://github.com/wjtan/sbt-byte-buddy).

Using ByteBuddy:
```
<plugin>
  <groupId>net.bytebuddy</groupId>
  <artifactId>byte-buddy-maven-plugin</artifactId>
  <version>1.10.16</version>
  <executions>
    <execution>
      <goals>
        <goal>transform</goal>
      </goals>
    </execution>
  </executions>
  <configuration>
    <transformations>
      <transformation>
        <plugin>com.reincarnation.cache.enhancer.CachePlugin</plugin>
        <groupId>com.reincarnation.cache</groupId>
        <artifactId>cache-enhancer</artifactId>
        <version>0.4.0</version>
      </transformation>
    </transformations>
  </configuration>
</plugin>
```

## Benchmarks
[Benchmark](https://github.com/wjtan/tr-recode-cache/blob/master/benchmark) is executed on an Intel Core i5-3570 CPU @ 3.40GHz, using Oracle Java 1.8.0_151.

The [benchmark results](https://github.com/wjtan/tr-recode-cache/blob/master/benchmark/results.txt) compares Guice AOP against compile-based AOP.

[Thread local cache results](https://github.com/wjtan/tr-recode-cache/blob/master/benchmark/threadlocal-results.txt) compares the overhead of using thread local caching.

## License
This project is released under terms of the [Apache 2.0](https://opensource.org/licenses/Apache-2.0).