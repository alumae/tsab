package ee.ioc.phon.tsab.domain;

public class Classifiers {

  //Make sure to update users.ftl when changing this!

  //(accept and provide corrections)
  public final static String USER_ROLE_SUPER = "0super";
  
  // provide corrections
  public final static String USER_ROLE_POWER = "1power";
  
  // suggest corrections, needs acceptance by superuser
  public final static String USER_ROLE_REGULAR = "2regular";

  public final static String CORRECTION_STATE_PENDING = "pending";
  public final static String CORRECTION_STATE_ACCEPTED = "accepted";
  public final static String CORRECTION_STATE_REJECTED = "rejected";
}
