package sample.cluster.client.grpc

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.serialization.SerializationExtension
import org.apache.pekko.serialization.Serializers
import com.google.protobuf.ByteString

class ClusterClientSerialization(system: ActorSystem) {

  private val serialization = SerializationExtension(system)

  def serializePayload(message: Any): Payload = {
    val msg = message.asInstanceOf[AnyRef]
    val msgSerializer = serialization.findSerializerFor(msg)
    val manifest = Serializers.manifestFor(msgSerializer, msg)
    val serializerId = msgSerializer.identifier

    Payload(ByteString.copyFrom(serialization.serialize(msg).get), serializerId, manifest)
  }

  def deserializePayload(payload: Payload): AnyRef = {
    serialization.deserialize(payload.enclosedMessage.toByteArray, payload.serializerId, payload.messageManifest).get
  }

}
