package ee.ioc.phon.tsab.cli;

import java.io.File;
import java.io.PrintStream;

import ee.ioc.phon.tsab.common.Constants;
import ee.ioc.phon.tsab.common.Search;
import ee.ioc.phon.tsab.common.TsabException;
import ee.ioc.phon.tsab.dao.TsabDaoService;

public class TransDelCLIProvider implements CLIProvider {

  @Override
  public void printUsage(PrintStream out) {
    out.println("  trans-del TranscriptionId");
  }

  @Override
  public String[] getCommands() {
    return new String[]{"trans-del"};
  }

  @Override
  public int handle(String[] args) {
    try {
      return  internalHandle(args);
    } catch (NumberFormatException e) {
      e.printStackTrace();
      return -1;
    } catch (TsabException e) {
      e.printStackTrace();
      return -2;
    }
  }

  private int internalHandle(String[] args) throws NumberFormatException, TsabException {
    if(args.length != 2) {
      System.err.println("Usage: Delete transId");
      return -3;
    }
    
    String transId = args[1];
    
    String fn = TsabDaoService.getDao().getTranscriptionById(new Long(transId)).getFn();
    
    TsabDaoService.getDao().deleteTranscriptionById(new Long(transId));
    
    Search.removeTranscription(transId);
    
    //new File(Constants.soundUploadFolder+File.separator+code+".mp3").delete();
    //new File(Constants.soundUploadFolder+File.separator+code+".trs").delete();
    
    File folder = Constants.soundUploadFolder;
      File[] listOfFiles = folder.listFiles();
      for(File file : listOfFiles) {
        //System.out.println(file.getName());
        if(file.getName().startsWith(fn+".") || file.getName().startsWith(fn+"##")) {
          //System.out.println("del");
          file.delete();
        }
      }
    
    System.out.println("Successfully deleted transcription");
    return 0;
  }

}
