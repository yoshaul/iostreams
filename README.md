iostreams
=========
[![Build Status](https://travis-ci.org/yoshaul/iostreams.svg?branch=master)](https://travis-ci.org/yoshaul/iostreams)
[![Coverage Status](https://coveralls.io/repos/yoshaul/iostreams/badge.svg?branch=master)](https://coveralls.io/r/yoshaul/iostreams?branch=master)

Set of useful, production ready, Java input and output streams.

### Requirements
------------

JDK version 1.6 or above

### Installation
------------
Add the following dependency to Maven based builds:

```xml
<dependency>
    <groupId>org.iostreams</groupId>
    <artifactId>iostreams</artifactId>
    <version>0.2</version>
</dependency>
```
### Usage
------------
One of the most useful classes in iostreams library is the `OutputToInputStream` stream.
This class allows you easily convert an `OutputSteam` to sources that expect an `InputStream`.

`OutputToInputStream` abstracts the complexities of piped streams and takes care of running the output in a separate thread. It's in use in enterprise production systems for several years.

A commons example is XML and JSON serializers that write to an `OutputStream` and you need to pass it to method that works with `InputStream`.

Here is a sample usage of the `OutputToInputStream` class:

```java
InputStream in = new OutputToInputStream() {
    protected void write(OutputStream sink) throws IOException {
        JacksonFactory.createJsonGenerator(sink).writeObject(toSerialize);
    }
};

// class that expects an input stream
FileUtils.write(targetPath, in);
```

### License
------------

iostreams is licensed under the [Apache 2 License](http://www.apache.org/licenses/LICENSE-2.0.html).
