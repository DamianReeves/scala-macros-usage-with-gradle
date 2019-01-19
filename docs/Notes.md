# Make Gradle More Scala Friendly

The following snippet makes it a little easier to work with scala dependencies in gradle.

``` gradle
  project.ext {
    scalaBaseVersion = '2.12'
    scalaVersion = "${scalaBaseVersion}.6"

    dep = { String dependency  ->
      dependency
        .replaceFirst(/^([a-zA-Z0-9._+-]+)(?::::)([a-zA-Z0-9._+-]+)(?::)([a-zA-Z0-9._+-]+)$/,'$1:$2_'+scalaVersion+':$3')
        .replaceFirst(/^([a-zA-Z0-9._+-]+)(?::::)([a-zA-Z0-9._+-]+)(?::)([a-zA-Z0-9._+-]+)(?::)([a-zA-Z0-9._+-]+)$/,'$1:$2_'+scalaVersion+':$3:$4')
        .replaceFirst(/^([a-zA-Z0-9._+-]+)(?:::)([a-zA-Z0-9._+-]+)(?::)([a-zA-Z0-9._+-]+)$/,'$1:$2_'+scalaBaseVersion+':$3')
        .replaceFirst(/^([a-zA-Z0-9._+-]+)(?:::)([a-zA-Z0-9._+-]+)(?::)([a-zA-Z0-9._+-]+)(?::)([a-zA-Z0-9._+-]+)$/,'$1:$2_'+scalaBaseVersion+':$3:$4')
    }
    ivy = dep

  }
```  
