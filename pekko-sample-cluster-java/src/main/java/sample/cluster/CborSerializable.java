/*
 * Copyright (C) 2009-2019 Lightbend Inc. <https://www.lightbend.com>
 */
package sample.cluster;

/**
 * Marker trait to tell Pekko to serialize messages into CBOR using Jackson for sending over the network
 * See application.conf where it is bound to a serializer.
 * For more details see the docs https://pekko.apache.org/docs/pekko/current/serialization-jackson.html
 */
public interface CborSerializable {  }
