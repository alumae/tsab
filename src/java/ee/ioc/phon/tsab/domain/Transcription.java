package ee.ioc.phon.tsab.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity(name="transcription")
@Table(name="transcription")
public class Transcription {

  private Long id;
  private String title;
  private Integer state; // "hidden" = 0, "public" = 1, "fresh" = 10
  private Date recorded;
  private Category category;
  private Long viewCount;
  private String fn;
  private Long audioLength; // seconds
  
  public Transcription() {
  }
  
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Integer getState() {
    return state;
  }

  public void setState(Integer state) {
    this.state = state;
  }

  public Date getRecorded() {
    return recorded;
  }

  public void setRecorded(Date recorded) {
    this.recorded = recorded;
  }

  @ManyToOne(optional=false)
  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public Long getViewCount() {
    return viewCount;
  }

  public void setViewCount(Long viewCount) {
    this.viewCount = viewCount;
  }

  public String getFn() {
    return fn;
  }

  public void setFn(String fn) {
    this.fn = fn;
  }

  @Override
  public String toString() {
    return "Transcription[id="+id+"; title="+title+"]";
  }

  public Long getAudioLength() {
    return audioLength;
  }

  public void setAudioLength(Long audioLength) {
    this.audioLength = audioLength;
  }

}
