package com.github.novamage.svalidator.utils

object Utils {

  def mergeMaps(a: Map[String, List[Any]], b: Map[String, List[Any]]): Map[String, List[Any]] = {
    val allKeys = a.keySet ++ b.keySet
    allKeys.map { key =>
      if (a.contains(key) && b.contains(key)) {
        key -> a.apply(key).:::(b.apply(key))
      } else if (a.contains(key)) {
        key -> a(key)
      } else {
        key -> b(key)
      }
    }.toMap
  }

}
