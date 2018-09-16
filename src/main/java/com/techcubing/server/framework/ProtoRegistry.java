package com.techcubing.server.framework;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Message;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ProtoRegistry {
  private Map<String, Class> classesByName;
  private Map<Class, Descriptor> descriptors;
  private Map<Class, Message.Builder> builders;

  public ProtoRegistry() {
    this.descriptors = new HashMap<>();
    this.builders = new HashMap<>();
    this.classesByName = new HashMap<>();
  }

  void registerProto(Message.Builder builder) {
    Descriptor descriptor = builder.getDescriptorForType();
    Class clazz = builder.build().getClass();
    descriptors.put(clazz, descriptor);
    builders.put(clazz, builder);
    classesByName.put(descriptor.getFullName(), clazz);
  }

  public Class getClassForName(String name) {
    return classesByName.get(name);
  }

  public Descriptor getDescriptorForType(Class clazz) {
    return descriptors.get(clazz);
  }

  public Message.Builder getBuilder(Class clazz) {
    return builders.get(clazz).clone();
  }

  public Collection<Descriptor> allProtos() {
    return descriptors.values();
  }
}
