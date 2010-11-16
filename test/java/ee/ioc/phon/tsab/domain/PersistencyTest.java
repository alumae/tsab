package ee.ioc.phon.tsab.domain;

import java.io.File;
import java.util.Iterator;

import junit.framework.TestCase;

import org.hibernate.classic.Session;

import ee.ioc.phon.tsab.dao.HibernateUtil;

public class PersistencyTest extends TestCase {
  
  static {
    System.setProperty("TSAB.HOME", "target/tsabtesthome");
    
    new File("target/tsabtesthome/lucene").mkdirs();
    new File("target/tsabtesthome/audio").mkdirs();
  }

  public void testBasicMapping() {

    try {
      // Begin unit of work
      HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();

      Session sess = HibernateUtil.getSessionFactory().getCurrentSession();

      Transcription newt = new Transcription();
      newt.setFn("asdfasdf.mp3");
      sess.save(newt);
      HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();

      sess = HibernateUtil.getSessionFactory().openSession();
      sess.beginTransaction();

      Iterator it = sess.createQuery("from Transcription").list().iterator();
      while (it.hasNext()) {
        Transcription object = (Transcription) it.next();
        System.out.println("Found: " + object.getId() + ";" + object.getFn());
      }

      // End unit of work
      sess.getTransaction().commit();
    } catch (Exception ex) {
      ex.printStackTrace();
      HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
    }

  }

  public void testDeleteScript() {

  }

  public void testListScript() {

  }

}
