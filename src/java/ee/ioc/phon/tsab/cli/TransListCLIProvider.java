package ee.ioc.phon.tsab.cli;

import java.io.PrintStream;
import java.util.Iterator;

import ee.ioc.phon.tsab.common.TsabException;
import ee.ioc.phon.tsab.dao.TsabDaoService;
import ee.ioc.phon.tsab.domain.Transcription;

public class TransListCLIProvider implements CLIProvider {

  @Override
  public void printUsage(PrintStream out) {
    out.println("  trans-list");
  }

  @Override
  public String[] getCommands() {
    return new String[]{"trans-list"};
  }

  @Override
  public int handle(String[] args) {
    try {
      internalHandle(args);
    } catch (TsabException e) {
      e.printStackTrace();
      return -1;
    }
    return 0;
  }

  private void internalHandle(String[] args) throws TsabException {
    System.out.println("Listing all transcriptions:");

    Iterator<Transcription> it = TsabDaoService.getDao().getAllTranscriptions().iterator();
    while (it.hasNext()) {
      Transcription t = (Transcription) it.next();
      System.out.println("Category:"+t.getCategory().getTitle()+"; Id:"+t.getId()+"; Name:"+t.getTitle()+"; Fn:"+t.getFn());
    }
  }

}
