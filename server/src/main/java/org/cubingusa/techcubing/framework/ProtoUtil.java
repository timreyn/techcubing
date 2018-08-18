import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DescriptorProtos.FieldOptions;
import com.google.protobuf.Message;
import org.cubingusa.techcubing.proto.OptionsProto;

public class ProtoUtil {
  public static String getId(Message message) {
    Descriptor descriptor = message.getDescriptorForType();
    for (FieldDescriptor field : descriptor.getFields()) {
      FieldOptions options = field.getOptions();
      if (options.getExtension(OptionsProto.primaryKey)) {
        return String.valueOf(message.getField(field));
      }
    }
    return null;
  }
}
