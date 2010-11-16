package ee.ioc.phon.tsab.web;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import ee.ioc.phon.tsab.common.Constants;
import ee.ioc.phon.tsab.common.TsabException;
import ee.ioc.phon.tsab.dao.TsabDaoService;

public class TsabAudioDownloadServlet extends HttpServlet {

  private final static Logger log = Logger.getLogger(TsabAudioDownloadServlet.class);
  
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String filename = req.getParameter("id");
    String fragment = req.getParameter("fragment");
    
    Long id = new Long(filename);
    Long frag = new Long(fragment);
    
    String sfn;
    try {
      sfn = TsabDaoService.getDao().getTranscriptionById(id).getFn();
    } catch (TsabException e) {
      log.warn("Failed to load audio filename for id "+id, e);
      return;
    }
    
    String fn = sfn+"##"+frag+".mp3";
    String realPath = Constants.soundUploadFolder + "/" + fn;
    
    if (!new File(realPath).exists()) {
      fn = sfn+"##0"+frag+".mp3";
      realPath = Constants.soundUploadFolder + "/" + fn;
    }
    if (!new File(realPath).exists()) {
      fn = sfn+"##00"+frag+".mp3";
      realPath = Constants.soundUploadFolder + "/" + fn;
    }    
    
    sendAudio(req, resp, realPath, fn);
    
    //super.doGet(req, resp);
  }

  private void sendAudio(HttpServletRequest req, HttpServletResponse resp, String filename, String original_filename)
      throws IOException {
    File f = new File(filename);
    int length = 0;
    ServletOutputStream out = resp.getOutputStream();

    boolean ogg = false; // filename.endsWith(".ogg"); -- FOR NOW we support only mp3 playback as streaming is not working properly anyway and splitting multiple formats is resource-intensive
    String mimetype = ogg?"application/ogg":"audio/mpeg";

    resp.setContentType(mimetype);
    resp.setContentLength((int) f.length());
    resp.setHeader("Content-Disposition", "attachment; filename=\"" + original_filename + "\"");
    byte[] bbuf = new byte[10 * 1024];
    DataInputStream in = new DataInputStream(new FileInputStream(f));

    while ((in != null) && ((length = in.read(bbuf)) != -1)) {
      out.write(bbuf, 0, length);
      
      try {
        // Artificially delay the download process to test better buffering.
        // Make sure to remove it for production env ;-)
        if(false)
        {
        Thread.currentThread().sleep(100);
        }
      } catch (InterruptedException e) {
        // 
      }
      
    }

    in.close();
    out.flush();
    out.close();
    resp.flushBuffer();
  }
}
