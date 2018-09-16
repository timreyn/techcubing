package com.techcubing.server.framework;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProtoRegistry {
  public class RegistryEntry<T extends Message> {
    public final Class<T> clazz;
    public final Descriptor descriptor;
    public final Parser<T> parser;

    RegistryEntry(T instance) {
      this.clazz = (Class<T>) instance.getClass();
      this.parser = (Parser<T>) instance.getParserForType();
      this.descriptor = instance.getDescriptorForType();
    }
  }

  private Map<String, RegistryEntry> entriesByName;
  private Map<Class, RegistryEntry> entriesByClass;

  public ProtoRegistry() {
    this.entriesByName = new HashMap<>();
    this.entriesByClass = new HashMap<>();
  }

  <T extends Message> void register(T instance) {
    RegistryEntry<T> entry = new RegistryEntry(instance);
    entriesByName.put(entry.descriptor.getFullName(), entry);
    entriesByClass.put(entry.clazz, entry);
  }

  public <T extends Message> RegistryEntry<T> get(String name) {
    return (RegistryEntry<T>) entriesByName.get(name);
  }

  public <T extends Message> RegistryEntry<T> get(Class<T> clazz) {
    return (RegistryEntry<T>) entriesByClass.get(clazz);
  }

  public List<Class<Message>> allProtos() {
    List<Class<Message>> allProtos = new ArrayList<>();
    for (RegistryEntry entry : entriesByName.values()) {
      allProtos.add(entry.clazz);
    }
    return allProtos;
  }
}
