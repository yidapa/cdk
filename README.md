# The Chemical Development Kit (CDK)
 
Copyright 1997-2014 The CDK Development Team
License: LGPL v2, see doc/lgpl.license

## Introduction

You are currently reading the README file for the Chemistry Development Project (CDK).
This project is hosted under http://cdk.sourceforge.net/
Please refer to these pages for updated information and the latest version of the CDK.

The CDK is an open-source library of algorithms for structural chemo- and bioinformatics, implemented in 
the programming language Java(tm). The library is published under terms of the the 
GNU Lesser General Public License v2. This has implications on what you can do with sources and
binaries of the CDK library. For details, please refer to the file LICENSE, which should have been
provided with this distribution.

PLEASE NOTE: This is a library of useful data structures and algorithms to manipulate them 
from the area of structural chemo- and bioinformatics. As such, it is intended for the use by
programmers, who wish to save some effort by reusing code. It is not intended for the enduser. 
If you consider yourself to be more like user, you might not find what you wanted. 
Please refer to other projects like the JChemPaint project (http://jchempaint.github.com/)
or the Jmol project (http://www.jmol.org/) for programs that actually take advantage of the 
CDK library.

## Compiling

Compiling the library is performed with Apache Maven and requires Java 1.6.0 or later:

cdk/$ ls pom.xml
pom.xml
cdk/$ mvn compile

This will produce a 'jar' file for each module located in each modules 'target/' directory.

## Creating the JavaDoc documentation for the API

The JavaDoc documentation for the API describes all of the CDK classes in detail. It functions as
the user manual for the CDK, although you should also look at the list of examples and tutorials
below. 
This documentation is created by 'mvn' from the Java source code for the CDK as follows:

cdk/$ ls pom.xml
pom.xml
cdk/$ mvn javadoc:aggregate

The documenation is created as a series of .html pages in target/site/apidocs. If you use firefox, you can read
the documentation using the following command:

cdk/$ firefox target/site/apidocs/index.html

## Running tests

IMPORTANT: this requires the Git version of the sources, because the test files are not included in
the source code distribution.

After you compiled the code, you can do "mvn test" to run the test suite of non-interactive, automated
tests.
Upon "mvn test", you should see something like:

test:
Running org.openscience.cdk.test.CDKTests
Tests run: 1065, Failures: 7, Errors: 1, Time elapsed: 27,55 sec

As you can see, the vast majority of tests ran successfully, but that there
are failures and errors. 

You can run the tests for an individual module by changing to the module directory:

cdk/$ cd descriptor/fingerprint
cdk/descriptor/fingerprint/$ mvn test

Tutorials on building the project in integrated development enviroments (IDEs) are avaialble on the wiki:
https://github.com/cdk/cdk/wiki/Building-CDK

## Using CDK

CDK is a class library intended to be used by other programs. It will not run 
as a stand-alone program, although it contains some GUI- and command
line applications. If your project is also using maven you can install the 
library in your local repository (~/.m2/repository) as follows:

cdk/$ mvn install -Dmaven.test.failure.ignore=true

A large bundled jar with all dependencies can also be built. If you have locally
made modifications to the source code you will need to install these to your
local repository. The jar will in the target directory of the 'bundle' module.

cdk/$ mvn install -DskipTests=true
cdk/$ ls bundle/target/cdk-{version}.jar

If you have not made any changes you need only package the bundle module. The other
modules will be automatically downloaded.

cdk/$ cd bundle
cdk/$ mvn package
cdk/$ ls target/cdk-{version}.jar


## Examples and tutorials

To get started using the CDK, you may be interested in the following websites which contain
examples and tutorials:

* http://pele.farmbio.uu.se/planetcdk/
* http://rguha.net/code/java/
* http://www.redbrick.dcu.ie/~noel/CDKJython.html

To keep up with the latest news on CDK development and usage

* Google Plus - https://plus.google.com/103703750118158205464/posts
* Mailing Lists - https://sourceforge.net/p/cdk/mailman/