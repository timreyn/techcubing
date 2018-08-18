package org.cubingusa.techcubing.framework;

import com.google.protobuf.Descriptors.Descriptor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ProtoRegistry {
  private Map<String, Descriptor> descriptors;

  public ProtoRegistry() {
    this.descriptors = new HashMap<>();
  }

  void registerProto(Descriptor descriptor) {
    descriptors.put(descriptor.getFullName(), descriptor);
  }

  Descriptor getProto(String name) {
    return descriptors.get(name);
  }

  Collection<Descriptor> allProtos() {
    return descriptors.values();
  }
}
