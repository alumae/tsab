package ee.ioc.phon.tsab.web;

import ee.ioc.phon.tsab.domain.Category;

public class TsabWebUtil {

  public final static String getCategoryBreadcrumb(Category cat) {
    return printCatLinks(cat, "");
  }

  private static String printCatLinks(Category cat, String current) {
    if (cat == null) {
      return current;
    }
    String gt = cat.getParent() == null ? "" : " &gt; ";

    return printCatLinks(cat.getParent(), gt + "<a href='category?cat=" + cat.getId() + "'>" + cat.getTitle()
        + "</a>"+current);

  }

}
