package ee.ioc.phon.tsab.cli;

import java.io.PrintStream;

import ee.ioc.phon.tsab.common.TsabException;
import ee.ioc.phon.tsab.dao.TsabDaoService;

public class CatDelCLIProvider implements CLIProvider {

  @Override
  public void printUsage(PrintStream out) {
    out.println("  cat-del CategoryId");
  }

  @Override
  public String[] getCommands() {
    return new String[]{"cat-del"};
  }

  @Override
  public int handle(String[] args) {
    try {
      internalHandle(args);
      return 0;
    } catch (TsabException e) {
      e.printStackTrace();
      return -1;
    }
    
  }

  private void internalHandle(String[] args) throws NumberFormatException, TsabException {
    TsabDaoService.getDao().deleteCategory(args[1]);
  }

}
