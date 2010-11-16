package ee.ioc.phon.tsab.cli;

import java.io.File;
import java.io.PrintStream;
import java.util.Iterator;

import org.apache.log4j.Logger;

import ee.ioc.phon.tsab.common.Constants;
import ee.ioc.phon.tsab.common.Search;
import ee.ioc.phon.tsab.common.Tools;
import ee.ioc.phon.tsab.common.TsabException;
import ee.ioc.phon.tsab.dao.TsabDaoService;
import ee.ioc.phon.tsab.domain.Transcription;

public class ReindexCLIProvider implements CLIProvider {

  private final static Logger log = Logger.getLogger(ReindexCLIProvider.class);

  @Override
  public void printUsage(PrintStream out) {
    out.println("  reindex");
  }

  @Override
  public String[] getCommands() {
    return new String[] { "reindex" };
  }

  @Override
  public int handle(String[] args) {
    try {
      return internalHandle(args);
    } catch (TsabException e) {
      e.printStackTrace();
      return -1;
    }
  }

  private int internalHandle(String[] args) throws TsabException {
    Search.clearIndex();
    Iterator<Transcription> it = TsabDaoService.getDao().getAllTranscriptions().iterator();
    while (it.hasNext()) {
      Transcription t = (Transcription) it.next();
      log.info("Indexing "+t);      
      Search.indexTranscription(t);
      Long len = t.getAudioLength();
      if (len==null || len.intValue()==0) {
        Tools.updateAudioLength(t);
        TsabDaoService.getDao().updateTranscription(t);
      }
    }    
    return 0;
  }


}
