package ee.ioc.phon.tsab.cli;

import java.io.PrintStream;
import java.io.Serializable;

import ee.ioc.phon.tsab.common.TsabException;
import ee.ioc.phon.tsab.dao.TsabDaoService;

public class CatAddCLIProvider implements CLIProvider {

  @Override
  public void printUsage(PrintStream out) {
    out.println("  cat-add \"CategoryName\" [parentCategoryId]");
  }

  @Override
  public String[] getCommands() {
    return new String[]{"cat-add"};
  }

  @Override
  public int handle(String[] args) {
    try {
      return new Long(internalHandle(args).toString()).intValue();
    } catch (NumberFormatException e) {
      e.printStackTrace();
      return -1;
    } catch (TsabException e) {
      e.printStackTrace();
      return -2;
    }
    
  }

  private Serializable internalHandle(String[] args) throws NumberFormatException, TsabException {
    String catName = args[1];
    System.out.println("Adding category with name: "+catName);
    
    String subCatId = null;
    if (args.length>=3) {
      subCatId = args[2];
    }
    System.out.println("  subCategoryId: "+subCatId);
    
    return TsabDaoService.getDao().addCategory(catName, subCatId).getId();
  }

}
