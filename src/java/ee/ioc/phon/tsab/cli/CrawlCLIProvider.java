package ee.ioc.phon.tsab.cli;

import java.io.File;
import java.io.PrintStream;

import org.apache.log4j.Logger;

import ee.ioc.phon.tsab.common.TsabException;
import ee.ioc.phon.tsab.dao.TsabDaoService;
import ee.ioc.phon.tsab.domain.Category;

public class CrawlCLIProvider implements CLIProvider {

  private final static Logger log = Logger.getLogger(CrawlCLIProvider.class);

  @Override
  public void printUsage(PrintStream out) {
    out.println("  crawl path/to/dir");
  }

  @Override
  public String[] getCommands() {
    return new String[] { "crawl" };
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

    if (args.length != 2) {
      System.err.println("Wrong number of arguments!");
      printUsage(System.err);
      return -1;
    }

    File dir = new File(args[1]);

    processDir(dir, 0, "", null);

    System.out.println("Successfully clawled dir " + dir);

    return 0;
  }

  private void processDir(File dir, int deep, String padding, Category parentCategory) throws TsabException {

    System.out.println(padding + "File: " + dir + "; cat: " + parentCategory);

    if (!dir.isDirectory()) {
      throw new TsabException("Cannot crawl into file,  must be directory! Affected path:" + dir);
    }

    String catName = dir.getName();

    Category cat = null;

    if (deep > 0) {
      cat = TsabDaoService.getDao().getCategoryByTitle(catName);
      if (cat == null) {
        log.info("Adding category: " + catName);
        
        if (parentCategory==null) {
          cat = TsabDaoService.getDao().addCategory(catName);
        } else {
          cat = TsabDaoService.getDao().addCategory(catName, parentCategory.getId().toString());
        }
        
      } else {
        log.debug("Retrieved existing category: " + catName);
      }

    }

    File[] files = dir.listFiles();
    if (files == null) {
      return;
    }

    for (File f : files) {
      if (f.isDirectory()) {
        processDir(f, deep + 1, padding + "  ", cat);
      }
      if (f.isFile()) {

        if (f.getName().toLowerCase().endsWith(".trs")) {
          processTRS(f, cat);
        }

      }
    }

  }

  private void processTRS(File f, Category cat) throws TsabException {
    System.out.println(".. processing " + f.getAbsolutePath());
    String name = f.getName();
    String basename = name.substring(0, name.length() - 4);
    
    File mp3 = new File(f.getParent()+File.separator+basename+".mp3");
    File ogg = new File(f.getParent()+File.separator+basename+".ogg");
    log.debug(".... mp3 exists?:" + mp3.exists());
    log.debug(".... ogg exists?:" + ogg.exists());
    if (!mp3.exists() && !ogg.exists()) {
      log.warn("Not adding "+f.getAbsoluteFile()+"! Neither mp3 nor ogg found!");
      return;
    }
    
    File audioFile = null;
    if (mp3.exists()) {
      audioFile = mp3;
    } else {
      audioFile = ogg;
    }

    TransAddCLIProvider.addTranscription(audioFile, f, cat, basename);
    
  }

}
