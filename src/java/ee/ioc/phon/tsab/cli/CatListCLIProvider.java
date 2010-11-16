package ee.ioc.phon.tsab.cli;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import ee.ioc.phon.tsab.common.TsabException;
import ee.ioc.phon.tsab.dao.TsabDaoService;
import ee.ioc.phon.tsab.domain.Category;
import ee.ioc.phon.tsab.domain.Transcription;

public class CatListCLIProvider implements CLIProvider {

  @Override
  public void printUsage(PrintStream out) {
    out.println("  cat-list");
  }

  @Override
  public String[] getCommands() {
    return new String[]{"cat-list"};
  }

  @Override
  public int handle(String[] args) {
    try {
      internalHandle(args);
    } catch (TsabException e) {      
      e.printStackTrace();
      return -100;
    }
    return 0;
  }

  private void internalHandle(String[] args) throws TsabException {
    System.out.println("Listing all Categories:");

    printCats(TsabDaoService.getDao().getRootCategories().iterator(), "");
    
  }

  private void printCats(Iterator<Category> it, String padding) throws TsabException {
    while (it.hasNext()) {
      Category t = (Category) it.next();
      System.out.println(padding+"CategoryId:"+t.getId()+"; Title:"+t.getTitle());
        List<Category> subs = TsabDaoService.getDao().getSubCategories(t.getId());
        if (subs.size()>0) {
          printCats(subs.iterator(), padding+"  ");
        }
    }
  }

}
