package ee.ioc.phon.tsab.cli;

import junit.framework.TestCase;

public class CLICatTest extends TestCase {

  static {
    System.setProperty("TSAB.HOME", "target/tsabtesthome");
    
  }

  public void testCatList() {
    assertEquals(0, new TsabCLI().handleMain(new String[]{"cat-list"}));
  }

  public void testCatAdd() {
    int newId = new TsabCLI().handleMain(new String[]{"cat-add","\"My New Category\""});
    assertTrue("New category id not received: "+newId, newId>0);    
  }

  public void testCatSub() {
    int newId = new TsabCLI().handleMain(new String[]{"cat-add","\"My New Category\""});
    assertTrue("New category id not received: "+newId, newId>0);
    int newSubId = new TsabCLI().handleMain(new String[]{"cat-add","\"My New Sub Category\"", ""+newId});
    assertTrue("New sub-category id not received: "+newSubId, newSubId>0);
  }

  public void testCatDel() {
    int newId = new TsabCLI().handleMain(new String[]{"cat-add","\"My New Category\""});
    assertTrue("New category id not received: "+newId, newId>0);
    int newSubId = new TsabCLI().handleMain(new String[]{"cat-add","\"My New Sub Category\"", ""+newId});
    assertTrue("New sub-category id not received: "+newSubId, newSubId>0);

    int ret = new TsabCLI().handleMain(new String[]{"cat-del",""+newId});
    
    assertTrue("Failed to remove category. Error code: "+ret, ret>=0);
    
  }


}
