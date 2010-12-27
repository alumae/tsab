package ee.ioc.phon.tsab.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "transcription_fragment_correction")
@Table(name = "transcription_fragment_correction")
public class TranscriptionFragmentCorrection {
   
  private Long id;
  private TranscriptionFragment fragment;
  private User user;
  private String state;
  private Date submissionDate;
  private String text;
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @ManyToOne(optional=false)
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @ManyToOne(optional=false)
  public TranscriptionFragment getFragment() {
    return fragment;
  }

  public void setFragment(TranscriptionFragment fragment) {
    this.fragment = fragment;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  @Column(name="submission_date")
  public Date getSubmissionDate() {
    return submissionDate;
  }

  public void setSubmissionDate(Date submissionDate) {
    this.submissionDate = submissionDate;
  }

  @Column(length=10000)
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
