package ee.ioc.phon.tsab.cli;

import java.io.PrintStream;

public interface CLIProvider {

  void printUsage(PrintStream out);

  String[] getCommands();

  int handle(String[] args);

}
