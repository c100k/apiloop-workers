organization            := "io.apiloop"

name                    := "workers"

version                 := "0.1.0"

scalaVersion            := "2.11.7"

offline                 := true

fork in Test            := false

// Always order dependencies by domain and artifactId (since groupId is often weird)
libraryDependencies ++= Seq(
    // JSON
    "com.fasterxml.jackson.core"        %           "jackson-core"              %                       "2.7.1",
    "com.fasterxml.jackson.core"        %           "jackson-databind"          %                       "2.7.1",
    "com.fasterxml.jackson.dataformat"  %           "jackson-dataformat-xml"    %                       "2.7.1",
    "com.fasterxml.jackson.datatype"    %           "jackson-datatype-json-org" %                       "2.7.1",
    // Logging
    "org.slf4j"                         %           "slf4j-api"                 %                       "1.7.21",
    // SDK
    "com.algolia"                       %           "algoliasearch-async"       %                       "2.3.1",
    "com.algolia"                       %           "algoliasearch-common"      %                       "2.3.1",
    // Testing
    "org.assertj"                       %           "assertj-core"              %                       "3.1.0" % "test",
    "com.novocode"                      %           "junit-interface"           %                       "0.11" % "test",
    // Utilities
    "javax.inject"                      %           "javax.inject"              %                       "1",
    "com.google.guava"                  %           "guava"                     %                       "19.0",
    "org.apache.commons"                %           "commons-jexl"              %                       "2.1.1",
    "org.apache.commons"                %           "commons-lang3"             %                       "3.4",
    "org.mindrot"                       %           "jbcrypt"                   %                       "0.3m",
    "org.jdom"                          %           "jdom2"                     %                       "2.0.6",
    "org.projectlombok"                 %           "lombok"                    %                       "1.16.10",
    "com.github.slugify"                %           "slugify"                   %                       "2.1.3",
    "org.springframework"               %           "spring-core"               %                       "4.2.4.RELEASE"
)

jacoco.settings

parallelExecution in jacoco.Config := false
