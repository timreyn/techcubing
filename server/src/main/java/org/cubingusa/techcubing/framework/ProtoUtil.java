package org.cubingusa.techcubing.framework;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DescriptorProtos.FieldOptions;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import java.util.ArrayList;
import java.util.List;
import org.cubingusa.techcubing.proto.OptionsProto;

public class ProtoUtil {
  public static String getId(MessageOrBuilder message) {
    Descriptor descriptor = message.getDescriptorForType();
    for (FieldDescriptor field : descriptor.getFields()) {
      FieldOptions options = field.getOptions();
      if (options.getExtension(OptionsProto.primaryKey)) {
        return String.valueOf(message.getField(field));
      }
    }
    return null;
  }

  // Returns a list of elements in this field.  This provides a common interface
  // for both repeated and singular fields.
  public static List<Object> getList(
      MessageOrBuilder message, FieldDescriptor descriptor) {
    List<Object> values = new ArrayList<>();
    if (descriptor.isRepeated()) {
      for (int i = 0; i < message.getRepeatedFieldCount(descriptor); i++) {
        values.add(message.getRepeatedField(descriptor, i));
      }
    } else {
      values.add(message.getField(descriptor));
    }
    return values;
  }

  // Returns a list of builders in this field.
  public static List<Message.Builder> getBuilderList(
      Message.Builder builder, FieldDescriptor descriptor) {
    List<Message.Builder> values = new ArrayList<>();
    if (descriptor.isRepeated()) {
      for (int i = 0; i < builder.getRepeatedFieldCount(descriptor); i++) {
        values.add(builder.getRepeatedFieldBuilder(descriptor, i));
      }
    } else {
      values.add(builder.getFieldBuilder(descriptor));
    }
    return values;
  }

  public static void setOrAdd(
      Message.Builder builder, FieldDescriptor descriptor, Object object) {
    if (descriptor.isRepeated()) {
      builder.addRepeatedField(descriptor, object);
    } else {
      builder.setField(descriptor, object);
    }
  }
}
