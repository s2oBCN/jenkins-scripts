# jenkins-scripts
Groovy scripts for Jenkins

Groovy scripts in this directory are made available via the
[Scriptler](https://wiki.jenkins-ci.org/display/JENKINS/Scriptler+Plugin)
catalog to be published at jenkins-ci.org.

In order for a script to be included in the catalog, metadata
describing it must be included in its file, like the below:

    /*** BEGIN META {
      "name" : "Whiskey in the Jar",
      "comment" : "print some cool <b>stuff</b>",
      "parameters" : [],
      "core": "1.600",
      "authors" : [
        { name : "..." }
      ]
    } END META**/
    println("Suspicious minds")


To validate the format of the META information, please run the provided script:

	&> groovy testMetaFormat.groovy
