package dslab.mailbox;

/**
 * POJO representing an Email with all its Content
 */
public class Email {
  private String from;
  private String to;
  private String subject;
  private String data;

  public Email(){}

  public Email(String from, String to, String subject, String data) {
    this.from = from;
    this.to = to;
    this.subject = subject;
    this.data = data;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getTo() {
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

}
