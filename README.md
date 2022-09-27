# KtCache

## What is KtCache ?

This is a simple ThreadLocal Cache, thought to acoit calling multiple times your most common repetitive immutable calls
in a single thread request.

So... what? Simple :point_down:

```kotlin
fun fooFinder(): Foo {
    cached("cache-key") {
        retrieveFoo()
    }
}

fun main() {
    cacheContext {
        fooFinder()
        fooFinder()
    }
}
```

`retrieveFoo()` wil be called just once, the second call will be cached and will return the same value as the first
call.

## What can I do with it?

Use it in applications that needs to query for the same data in different places, and this data is "immutable" for the
duration of the request. You can call the same function, repository, client... in multiple places, and it will avoid
multiple calls. And when the request is over, the cache es cleaned. No TTL, no eviction policy, simple and easy!

## Simple API

```kotlin 
    KtCache.cacheContext(block: () -> Any)
```

- Used to create a new cache.
- Whenever the block is finished, the cache will be cleaned!
- It supports nested cacheContext!
- Anything outside a cacheContext won't be cached!

```kotlin 
    KtCache.cached(key: String, block: () -> Any )
```

- The main block used to cache whatever you need
- The key is the most important part!

```kotlin 
    KtCache.stats(key: String, block: () -> Any )
```

- Returns the stats of the current cache.
- Done outside the cache context, will returns always zero (_as the cache is already cleaned_)
- You have also a `totalStats` function to retrieve the accumulated stats!

## Key Selection

The most complex part of the whole project, is the key selection!

If you want to cache a repository call, you should use not just the repository function call, but also the parameters
sent!

```kotlin
    fun find(id: UUID) {
    cached("find") {
        repository.find(id)
    }
}
```

This key is wrong and shouldn't be used, as any request you do to find, will return always the same value! A correct
approach could be:

```kotlin
    fun find(id: UUID) {
    cached("find-${id.toString()}") {
        repository.find(id)
    }
}
```

Always thinking that there is no other find with the same cached key around your code! What usually works is to have a
simple KeyGenerator that writes the `package+class+method+params` as key!

## Motivation

I used it in multiple applications, mainly where avoiding multiple calls to the same thing caused readability problems
in our code! So, after building it for all this projects, I decided to create a simple repo and use it from here
whenever I need it!
