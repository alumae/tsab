package ee.ioc.phon.tsab.dao;

import ee.ioc.phon.tsab.common.Constants;

public class TsabDaoService {

  private final static TsabDao dao = new TsabDao();

  static {
    Constants.init();
  }

  public final static TsabDao getDao() {
    return dao;
  }

}
