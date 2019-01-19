# scala-macros-usage-with-gradle
An example of using Scala macros with gradle

I also have a related [blogpost](https://medium.com/@damreev_2001/using-scala-plugins-from-gradle-180ece77b6bd) on this topic.

# Using Scala Compiler Plugins From Gradle
The Scala compiler like just about every other modern programming language compiler has a plugin/extensibility story. It universally accepted that sbt is the default build tool for Scala. So users of sbt benefit from sbt’s first class support of Scala compiler plugins.

For those of us who choose (or are forced to use) other build tools, life is a little more difficult. It is more likely than not, that the ReadMe of your favorite compiler plugin does not include instructions on how to use the plugin outside of sbt. Alas, all hope is not gone. Repeat after me, “Yes we can!” You too can join in with the cool kids, and use compiler plugins.

## NewType
The scalamacro.paradise plugin is a fairly popular plugin that many other plugins leverage. In this post, we are going to use macro paradise to enable scala-newtype macro expansion. The scala-newtype package includes a @newtype annotation that leverages scalamacro.paradise to allow you to generate new types that map to a wrapped type with zero runtime overhead.

``` scala
import io.estatico.newtype.macros.newtype

package object types {
  @newtype case class OrderId(value: String)
}
```

This is similar to the newtype keyword in Haskell, from which the library derives its name.

``` haskell
newtype OrderId = OrderId String
The scala-newtype package/library is also very similar to theopaque type alias feature which is coming in Scala 3.0.
```

``` scala
//Scala 3.0 / Dotty opaque type
opaque type OrderId = String
```

## Gradle Paradise
I’m going to outline how you would go about enabling macro-paradise so you can use newtype in all its glory.

First, lets add a custom configuration to our gradle file.

We can achieve this by simply adding the following:

``` gradle
configurations {    
   scalaCompilerPlugin
}
```

With that configuration in place, we can now add the macro paradise compiler plugin.

``` gradle
dependencies {
  ...
  compile 'org.scala-lang:scala-library:2.12.6'    
  compile 'io.estatico:newtype_2.12:0.4.2'    
  scalaCompilerPlugin 'org.scalamacros:paradise_2.12.6:2.1.1'
  ...
}
```

At this point you should have something that looks like below:

``` gradle
configurations {
    scalaCompilerPlugin
}

dependencies {
    // Use Scala 2.11 in our library project
    compile 'org.scala-lang:scala-library:2.12.6'
    compile 'io.estatico:newtype_2.12:0.4.2'
    scalaCompilerPlugin 'org.scalamacros:paradise_2.12.6:2.1.1'


    // Use Scalatest for testing our library
    testCompile 'junit:junit:4.12'
    testCompile 'org.scalatest:scalatest_2.12:3.0.5'

    // Need scala-xml at test runtime
    testRuntime 'org.scala-lang.modules:scala-xml_2.12:1.1.0'
}
```

Okay, we now have the plugin available, but that’s not enough. Now we need to make the Scala compiler aware of the scalamacro.paradise plugin by adding it to the scalaCompileOptions of the ScalaCompile task.


## Adding Plugin to scalaCompileOptions
With this in place, you should now be able to use scala-newtype in anger with successful macro generation.

## Wrapping Up
Once done you should have a build.gradle file that looks something like below:

``` gradle
plugins {
    id 'scala'
}

configurations {
    scalaCompilerPlugin
}

dependencies {
    // Use Scala 2.11 in our library project
    compile 'org.scala-lang:scala-library:2.12.6'
    compile 'io.estatico:newtype_2.12:0.4.2'
    scalaCompilerPlugin 'org.scalamacros:paradise_2.12.6:2.1.1'


    // Use Scalatest for testing our library
    testCompile 'junit:junit:4.12'
    testCompile 'org.scalatest:scalatest_2.12:3.0.5'

    // Need scala-xml at test runtime
    testRuntime 'org.scala-lang.modules:scala-xml_2.12:1.1.0'
}


repositories {
    jcenter()
}

tasks.withType(ScalaCompile){
    // Map plugin jars to -Xplugin parameter
    List<String> parameters =
    configurations.scalaCompilerPlugin.files.collect {
        '-Xplugin:'+ it.absolutePath
    }

    // Add existing parameters
    List<String> existingParameters = scalaCompileOptions.additionalParameters
    if (existingParameters) {
        parameters.addAll(existingParameters)
    }

    // Add whatever flags you typically add
    parameters += [
            '-language:implicitConversions',
            '-language:higherKinds'
    ]
    
    // Finally set the additionalParameters
    scalaCompileOptions.additionalParameters = parameters
}
```

## Full code example
Though this post outlined adding the scalamacro.paradise plugin, you can use this same procedure to include other Scala compiler plugins. Enjoy!
