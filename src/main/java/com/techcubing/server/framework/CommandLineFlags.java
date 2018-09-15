package com.techcubing.server.framework;

import com.google.devtools.common.options.EnumConverter;
import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

import com.techcubing.proto.WcaEnvironmentProto.WcaEnvironment;

public class CommandLineFlags extends OptionsBase {
  @Option(
    name = "help",
    abbrev = 'h',
    help = "Prints usage info.",
    defaultValue = "false"
  )
  public boolean help;

  public static class WcaEnvironmentConverter
      extends EnumConverter<WcaEnvironment> {
    public WcaEnvironmentConverter() {
      super(WcaEnvironment.class, "WcaEnvironment");
    }
  }

  @Option(
    name = "wca",
    help = "Which WCA environment to use.",
    defaultValue = "PROD",
    converter = WcaEnvironmentConverter.class
  )
  public WcaEnvironment wca;
}
