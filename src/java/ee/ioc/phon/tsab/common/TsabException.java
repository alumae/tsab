package ee.ioc.phon.tsab.common;

public class TsabException extends Exception {

  public TsabException(String msg) {
    super(msg);
  }

  public TsabException(String msg, Exception e) {
    super(msg, e);
  }

}
