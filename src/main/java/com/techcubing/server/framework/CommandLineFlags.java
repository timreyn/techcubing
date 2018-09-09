package com.techcubing.server.framework;

import com.google.devtools.common.options.EnumConverter;
import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

public class CommandLineFlags extends OptionsBase {
  @Option(
    name = "help",
    abbrev = 'h',
    help = "Prints usage info.",
    defaultValue = "false"
  )
  public boolean help;

  public static class WcaEnvironmentConverter
      extends EnumConverter<ServerState.WcaEnvironment> {
    public WcaEnvironmentConverter() {
      super(ServerState.WcaEnvironment.class, "WcaEnvironment");
    }
  }

  @Option(
    name = "wca",
    help = "Which WCA environment to use.",
    defaultValue = "PROD",
    converter = WcaEnvironmentConverter.class
  )
  public ServerState.WcaEnvironment wca;
}
