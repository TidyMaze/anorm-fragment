# anorm-fragment

An attempt to bring composable SQL fragments for [anorm](https://github.com/playframework/anorm).
The main idea is to wrap query as String and parameters (a map).

See the [Demo here](https://github.com/TidyMaze/anorm-fragment/blob/master/src/main/scala/Demo.scala) for a quick tour.

Some useful tools should come along such as `addIf(predicate, fragment)`.
