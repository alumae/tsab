package ee.ioc.phon.tsab.common;

import java.io.File;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import ee.ioc.phon.tsab.dao.TsabDaoService;

public class Constants {

  private final static Logger log = Logger.getLogger(TsabDaoService.class);

  private final static String TSAB_HOME = "TSAB.HOME";

  public static String TSAB_HOME_PATH;

  public static File soundUploadFolder;

  public static File luceneFolder;

  public static int linesPerSearchEntry = 3;

  public static boolean createNewLuceneIndex = false;

  static {

    String dbHost = null;
    String dbUser = null;
    String dbPass = null;

    if (System.getProperty("TSAB.CLI") == null) {
      Context initCtx;
      try {
        initCtx = new InitialContext();
        Context envCtx = (Context) initCtx.lookup("java:comp/env");
        TSAB_HOME_PATH = (String) envCtx.lookup(Constants.TSAB_HOME);
        log.info("Setting " + TSAB_HOME + " to " + TSAB_HOME_PATH);

        dbHost = exposeEnv(envCtx, "TSAB.DB.HOST");
        log.debug("Exposing db host as:" + dbHost);
        dbUser = exposeEnv(envCtx, "TSAB.DB.USERNAME");
        log.debug("Exposing db username as:" + dbUser);
        dbPass = exposeEnv(envCtx, "TSAB.DB.PASSWORD");

      } catch (NamingException e) {
        log.error("Failed to look up env vars!", e);
      }
    }

    if (TSAB_HOME_PATH == null || TSAB_HOME_PATH.length() == 0) {
      TSAB_HOME_PATH = System.getProperty(TSAB_HOME);
    }

    if (dbHost == null || dbHost.length() == 0) {
      dbHost = System.getProperty("TSAB.DB.HOST");
    }

    if (dbUser == null || dbUser.length() == 0) {
      dbUser = System.getProperty("TSAB.DB.USERNAME");
    }

    if (dbPass == null || dbPass.length() == 0) {
      dbPass = System.getProperty("TSAB.DB.PASSWORD");
    }

    if (TSAB_HOME_PATH == null || TSAB_HOME_PATH.length() == 0) {
      RuntimeException rt = new RuntimeException(
          "ENV ERROR! TSAB.HOME environment variable not available - not starting!");
      log.error(rt);
      throw rt;
    }

    soundUploadFolder = new File(TSAB_HOME_PATH + "/audio");
    luceneFolder = new File(TSAB_HOME_PATH + "/lucene");

    if (!Constants.soundUploadFolder.isDirectory()) {
      RuntimeException rt = new RuntimeException("Sound upload folder not found: " + Constants.soundUploadFolder);
      log.error(rt);
      throw rt;
    }

    if (!Constants.luceneFolder.isDirectory()) {
      RuntimeException rt = new RuntimeException("Lucene folder not found: " + Constants.luceneFolder);
      log.error(rt);
      throw rt;
    }

  }

  public static void init() {
    // just forces static init
  }

  private static String exposeEnv(Context envCtx, String string) throws NamingException {
    String var = (String) envCtx.lookup(string);
    System.setProperty(string, var);
    return var;
  }

}
