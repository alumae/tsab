package ee.ioc.phon.tsab.cli;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class TsabCLI {

  private final static CLIProvider[] providers = new CLIProvider[]{
      new CatListCLIProvider(),
      new CatAddCLIProvider(),
      new CatDelCLIProvider(),
      new TransListCLIProvider(),
      new TransAddCLIProvider(),
      new TransDelCLIProvider(),
      new CrawlCLIProvider(),
      new ReindexCLIProvider(),
      new TransReAddCLIProvider()
  };
  
  private final static Map<String, CLIProvider> commandHandlers = new HashMap<String, CLIProvider>();
  
  static {
    
    for (CLIProvider p: providers) {
      String[] cmds = p.getCommands();
      for (String c: cmds) {
        CLIProvider ex = commandHandlers.get(c);
        if (ex!=null) {
          System.err.println("Illegal CLI configuration! Duplicate CLI handler! "+p+" vs "+ex);
          System.exit(-3);
        }
        commandHandlers.put(c, p);
      }
    }
    
  }
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    int ret = new TsabCLI().handleMain(args);
    System.out.println("Exiting with code: "+ret);
    System.exit(ret);
  }

  int handleMain(String[] args) {
    
    System.out.println("TSAB Command Line Interface.");
    
    if (args.length==0) {
      System.err.println("No arguments! Usage:");
      for (CLIProvider p: providers) {
        p.printUsage(System.err);
      }
      return -1;
    }

    String cmd = args[0];
    
    for (CLIProvider p: providers) {
      if (supports(p, cmd)) {
        int ret = p.handle(args);
        if (ret == -2) {
          System.out.println("Illegal arguments. Usage:");
          p.printUsage(System.err);          
        }
        return ret;
      }
    }
    
    System.err.println("No handler found for command "+cmd);
    return -3;
    
  }

  private static boolean supports(CLIProvider p, String cmd) {
    Iterator<Entry<String, CLIProvider>> it = commandHandlers.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<java.lang.String,ee.ioc.phon.tsab.cli.CLIProvider> e = (Map.Entry<java.lang.String,ee.ioc.phon.tsab.cli.CLIProvider>) it
          .next();
      if (e.getKey().equalsIgnoreCase(cmd) && e.getValue().equals(p)) {
        return true;
      }
    }
    return false;
  }

}
