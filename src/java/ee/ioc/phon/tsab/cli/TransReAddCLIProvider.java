package ee.ioc.phon.tsab.cli;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import ee.ioc.phon.tsab.common.Constants;
import ee.ioc.phon.tsab.common.Search;
import ee.ioc.phon.tsab.common.Tools;
import ee.ioc.phon.tsab.common.TsabException;
import ee.ioc.phon.tsab.dao.TsabDaoService;
import ee.ioc.phon.tsab.domain.Category;
import ee.ioc.phon.tsab.domain.Transcription;
import ee.ioc.phon.tsab.domain.TranscriptionFragment;
import ee.ioc.phon.tsab.trs.Entry;
import ee.ioc.phon.tsab.trs.TRSParser;

public class TransReAddCLIProvider implements CLIProvider {

  private final static Logger log = Logger.getLogger(TransReAddCLIProvider.class);
  
  @Override
  public void printUsage(PrintStream out) {
    out.println("  trans-update-trs [path/to/]transcription.trs");
  }

  @Override
  public String[] getCommands() {
    return new String[] { "trans-update-trs" };
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
      printUsage(System.err);
      return -1;
    }

    File transcription = new File(args[1]);

    if (!transcription.canRead()) {
      System.err.println("Can't read transcription file: " + transcription);
      return -3;
    }

    return addTranscription(transcription);
  }

  private int addTranscription(File trs) throws TsabException {
    TRSParser parser = null;

    parser = new TRSParser(trs);

    if (parser.getFn() == null) {
      throw new TsabException("Could not find transcription filename inside .trs file.");
    }

    Transcription v = TsabDaoService.getDao().getTranscriptionByFn(parser.getFn());

      Transcription newt = v;

      newt.setRecorded(new Date());

      TsabDaoService.getDao().deleteFragments(newt);
      
      List<TranscriptionFragment> fragments = new ArrayList<TranscriptionFragment>();
      Iterator<Entry> it = parser.getFragments().iterator();

      while (it.hasNext()) {
        Entry entry = (Entry) it.next();
        TranscriptionFragment fr = entry.generateTranscriptionFragment();
        fr.setTranscription(newt);
        fragments.add(fr);
      }

      TsabDaoService.getDao().addFragments(newt, fragments);


    try {
      Search.removeTranscription(v.getId().toString());
      Search.indexTranscription(v);
    } catch (Exception e) {
      throw new TsabException("Coult not index transcription!", e);
    }

    //TsabDaoService.getDao().updateTranscription(current);

    log.debug("Successfully updated trs "+trs.getAbsolutePath());

    return 0;
  }

  public final static int addTranscription(File audio, File transcription, Category category, String title) throws TsabException {
    TRSParser parser = null;

    parser = new TRSParser(transcription);

    if (parser.getFn() == null) {
      throw new TsabException("Could not find transcription filename inside .trs file.");
    }

    Transcription v = TsabDaoService.getDao().getTranscriptionByFn(parser.getFn());

    Transcription current = null;

    if (v != null) {

      log.info("Transcription already exists for transcription. Not adding! Transcription file: "+transcription.getAbsolutePath());
      return -1;

    } else {

      Transcription newt = new Transcription();
      newt.setTitle(title == null ? "" : title);
      newt.setState(0);
      newt.setViewCount(new Long("0"));
      newt.setCategory(category);
      newt.setFn(parser.getFn());
      newt.setRecorded(new Date());

      List<TranscriptionFragment> fragments = new ArrayList<TranscriptionFragment>();
      Iterator<Entry> it = parser.getFragments().iterator();

      while (it.hasNext()) {
        Entry entry = (Entry) it.next();
        TranscriptionFragment fr = entry.generateTranscriptionFragment();
        fr.setTranscription(newt);
        fragments.add(fr);
      }
      
      current = TsabDaoService.getDao().addTranscription(newt, fragments, parser.getTopics());

    }

    try {
      Search.indexTranscription(current);
    } catch (Exception e) {
      throw new TsabException("Coult not index transcription!", e);
    }

    if (audio.getName().toLowerCase().endsWith(".ogg")) {
      // Convert ogg to mp3 into soundUploadFolder
      Tools.convertOGGtoMP3(audio.getAbsolutePath(), Constants.soundUploadFolder.getAbsolutePath() + File.separator + parser.getFn() + ".mp3");
    } else {
      Tools.copyfile(audio, Constants.soundUploadFolder.getAbsolutePath() + File.separator + parser.getFn() + ".mp3");
    }
    
    Tools.copyfile(transcription, Constants.soundUploadFolder.getAbsolutePath() + File.separator + parser.getFn()
        + ".trs");

    Tools.splitAudio(current);

    Tools.updateAudioLength(current);
    
    current.setState(new Integer(1));
    TsabDaoService.getDao().updateTranscription(current);

    log.debug("Successfully added new transcription "+transcription.getAbsolutePath());

    return 0;
  }

}
