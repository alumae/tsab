package ee.ioc.phon.tsab.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name="transcriptiontopic")
@Table(name="transcriptiontopic")
public class TranscriptionTopic {

  private Long id;
  private Transcription transcription;
  private String topicId;
  private String topicName;
  private Long time;

  private int transientSeq;
  private String transientTimeStr;
  
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @ManyToOne(optional=false)
  public Transcription getTranscription() {
    return transcription;
  }

  public void setTranscription(Transcription transcription) {
    this.transcription = transcription;
  }

  @Column(name="topic_id")
  public String getTopicId() {
    return topicId;
  }

  public void setTopicId(String topicId) {
    this.topicId = topicId;
  }

  @Column(name="topic_name")
  public String getTopicName() {
    return topicName;
  }

  public void setTopicName(String topicName) {
    this.topicName = topicName;
  }

  @Column
  public Long getTime() {
    return time;
  }

  public void setTime(Long time) {
    this.time = time;
  }

  @Transient
  public int getTransientSeq() {
    return transientSeq;
  }

  public void setTransientSeq(int transientSeq) {
    this.transientSeq = transientSeq;
  }

  @Transient
  public String getTransientTimeStr() {
    return transientTimeStr;
  }

  public void setTransientTimeStr(String transientTimeStr) {
    this.transientTimeStr = transientTimeStr;
  }


}
