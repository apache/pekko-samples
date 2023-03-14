package sample.cluster.client.grpc;

import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.serialization.Serialization;
import org.apache.pekko.serialization.SerializationExtension;
import org.apache.pekko.serialization.Serializer;
import org.apache.pekko.serialization.Serializers;
import com.google.protobuf.ByteString;

class ClusterClientSerialization {
  private final ActorSystem system;
  private final Serialization serialization;

  ClusterClientSerialization(ActorSystem system) {
    this.system = system;
    this.serialization = SerializationExtension.get(system);
  }

  Payload serializePayload(Object message) {
    Serializer msgSerializer = serialization.findSerializerFor(message);
    String manifest = Serializers.manifestFor(msgSerializer, message);
    int serializerId = msgSerializer.identifier();

    return Payload.newBuilder()
      .setEnclosedMessage(ByteString.copyFrom(serialization.serialize(message).get()))
    .setSerializerId(serializerId)
    .setMessageManifest(manifest)
      .build();
  }

  Object deserializePayload(Payload payload) {
    return serialization.deserialize(
      payload.getEnclosedMessage().toByteArray(),
      payload.getSerializerId(),
      payload.getMessageManifest()).get();
  }

}
