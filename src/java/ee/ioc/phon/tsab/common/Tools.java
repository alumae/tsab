package ee.ioc.phon.tsab.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import ee.ioc.phon.tsab.domain.Transcription;
import freemarker.template.utility.StringUtil;

public class Tools {

  private static final String DURATION = "Duration: ";
  private static final String START = ", start:";
  
  private final static Logger log = Logger.getLogger(Tools.class);

  public static void copyfile(File f1, String dtFile) {
    try {
      File f2 = new File(dtFile);
      InputStream in = new FileInputStream(f1);

      //For Overwrite the file.
      OutputStream out = new FileOutputStream(f2);

      byte[] buf = new byte[1024];
      int len;
      while ((len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
      }
      in.close();
      out.close();
    } catch (FileNotFoundException ex) {
      System.out.println(ex.getMessage() + " in the specified directory.");
      System.exit(0);
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  public static String toQueryResult(String query, String str) {

    String regexp = "";

    String[] qs = query.split(" ");
    for (String q : qs) {
      regexp += (regexp.length() > 0 ? "|" : "") + q;
    }

    Pattern myPattern = Pattern.compile("(" + regexp + ")", Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        | Pattern.UNICODE_CASE);
    Matcher myMatcher = myPattern.matcher(str);

    return myMatcher.replaceAll("<strong>$1</strong>");
  }

  public static boolean writeFile(String filename, InputStream inputStream) throws IOException {

    try {
      Constants.soundUploadFolder.mkdirs();
      File file = new File(Constants.soundUploadFolder.getAbsolutePath() + "/" + filename);

      FileOutputStream outFile = new FileOutputStream(file);
      int c;
      while ((c = inputStream.read()) != -1) {
        outFile.write(c);
      }

      outFile.close();
      return true;
    } catch (IOException e) {
      throw new IOException("File " + Constants.soundUploadFolder.getAbsolutePath() + "/" + filename + " not added: "
          + e.getMessage());
    }
  }

  public static void splitAudio(Transcription trans) throws TsabException {

    try {
      String file = Constants.soundUploadFolder.getAbsolutePath() + File.separator + trans.getFn();

      //1-min splits

      /*      System.out.println("Splitting audio: " + cmd);
            Process pp = Runtime.getRuntime().exec(cmd);
            pp.waitFor();
      */
      run("mp3splt", "-t", "1.0", "-o",trans.getFn()+"##@n",file + ".mp3","-d",
          Constants.soundUploadFolder.getAbsolutePath());
      
    } catch (Exception ioe) {
      throw new TsabException("Unable to split audio to 1min segments!", ioe);
    }

  }

  public static void convertOGGtoMP3(String oggPath, String mp3Path) throws TsabException {
    run("ffmpeg", "-i",oggPath,mp3Path);
  }

  public static long fetchAudioTrackLength(String filePath) throws TsabException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    run(bos, "ffmpeg", "-i",filePath+".mp3");
    String s = bos.toString();
    int start = s.indexOf(DURATION)+DURATION.length();
    int end = s.indexOf(START)-1;
    
    long total = 0;
    
    if (start<end && end<s.length()) {
      String toParse = s.substring(start, end);
      System.out.println("TO PARSE("+toParse+")");
      String mainpart = toParse.split("\\.")[0];
      String[] parts = mainpart.split(":");
      long lh = new Long(parts[0]).longValue();
      long lm = new Long(parts[1]).longValue();
      long ls = new Long(parts[2]).longValue();
      
      total = ls+lm*60+(lh*60*60);      
      System.out.println("H:"+lh+";M:"+lm+";S:"+ls+"; total sec:"+total);
      
    }    
    return total;
    
  }

  
  private static void run(String... cmd) throws TsabException {
    run(System.out, cmd);
  }
  
  private static void run(final OutputStream out, String... cmd) throws TsabException {

    StringBuffer j = new StringBuffer();
    for (String l:cmd) {
      j.append(l);
      j.append(" ");
    }
    log.debug("Running: " + j.toString());

    try {
      ProcessBuilder pb = new ProcessBuilder(cmd);
      pb.redirectErrorStream(true);
      final Process p = pb.start();

      final boolean[] stop = new boolean[] { false };

      new Thread(new Runnable() {

        @Override
        public void run() {
          InputStream is = p.getInputStream();
          byte[] buf = new byte[1024];
          int len;

          try {

            while (!stop[0]) {
              while ((len = is.read(buf)) > 0) {
                out.write(buf, 0, len);
              }

              Thread.currentThread().sleep(500);

            }

            is.close();

          } catch (IOException e) {
            e.printStackTrace();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

        }
      }).start();

      p.waitFor();
      stop[0] = true;

    } catch (Exception ioe) {
      throw new TsabException("Unable to run " + cmd, ioe);
    }
  }

  public static void updateAudioLength(Transcription t) throws TsabException {
    String fn = t.getFn();
    String realPath = Constants.soundUploadFolder + "/" + fn;
    
    long len = Tools.fetchAudioTrackLength(realPath);
    if (len>0) {
      t.setAudioLength(new Long(len));
    }
  }
  
}
