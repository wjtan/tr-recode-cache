package com.reincarnation.cache

import com.google.inject.AbstractModule

class CacheStatusModule extends AbstractModule {
  def configure(): Unit = {
    bind(classOf[views.cache.CacheStatusTemplate])
  }
}
