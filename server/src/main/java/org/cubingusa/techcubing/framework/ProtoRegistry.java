package org.cubingusa.techcubing.framework;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Message;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ProtoRegistry {
  private Map<String, Descriptor> descriptors;
  private Map<String, Message.Builder> builders;

  public ProtoRegistry() {
    this.descriptors = new HashMap<>();
    this.builders = new HashMap<>();
  }

  void registerProto(Message.Builder builder) {
    Descriptor descriptor = builder.getDescriptorForType();
    descriptors.put(descriptor.getFullName(), descriptor);
    builders.put(descriptor.getFullName(), builder);
  }

  public Descriptor getProto(String name) {
    return descriptors.get(name);
  }

  public Collection<Descriptor> allProtos() {
    return descriptors.values();
  }

  public Message.Builder getBuilder(String name) {
    return builders.get(name).clone();
  }
}
