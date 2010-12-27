package ee.ioc.phon.tsab.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name="transcriptionfragment")
@Table(name="transcriptionfragment")
public class TranscriptionFragment {

  private Long id;
  private Transcription transcription;
  private TranscriptionTopic topic;
  private Long time;
  private String author;
  private String text;
  private String originalText;
  private String transientTopicDesc;
  private int transientTopicSeq;

  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @ManyToOne(optional=false,fetch=FetchType.EAGER)
  public Transcription getTranscription() {
    return transcription;
  }

  public void setTranscription(Transcription transcription) {
    this.transcription = transcription;
  }

  @ManyToOne(optional=true)
  public TranscriptionTopic getTopic() {
    return topic;
  }

  public void setTopic(TranscriptionTopic transTopic) {
    this.topic = transTopic;
  }

  
  @Column
  public Long getTime() {
    return time;
  }

  public void setTime(Long time) {
    this.time = time;
  }

  @Column
  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  @Column(length=10000)
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public void setTransientTopicDesc(String topic) {
    // Helps binding later
    this.transientTopicDesc = topic;
  }

  @Transient
  public String getTransientTopicId() {
    return transientTopicDesc;
  }

  @Transient
  public int getTransientTopicSeq() {
    return transientTopicSeq;
  }

  public void setTransientTopicSeq(int transientTopicSeq) {
    this.transientTopicSeq = transientTopicSeq;
  }

  @Column(name="original_text")
  public String getOriginalText() {
    return originalText;
  }

  public void setOriginalText(String originalText) {
    this.originalText = originalText;
  }
  
}
