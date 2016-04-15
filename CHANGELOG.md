## 1.0 (April 15, 2016)

Features:

  - `BandwidthMonitorOutputStream` - An output stream that calculates the bandwidth of the underlying output stream

Improvements:

  - More complete test coverage

Bugfixes:

  - Shutting down internal thread executor in `OutputToInputStream`

## 0.2 (March 31, 2014)

Features:

  - `BandwidthMonitorInputStream` -  An input stream that calculates the bandwidth of the input stream

Improvements:

  - Better exception propagation from `OutputToInputStream`

## 0.1 (August 6, 2013)

Features:

  - `OutputToInputStream` - "converts" an output stream to input stream
  - `DeleteOnCloseFileInputStream` - A `FileInputStream` that deletes the underlying file when this file stream is closed
  - `StringInputStream` - Creates an input stream from a string