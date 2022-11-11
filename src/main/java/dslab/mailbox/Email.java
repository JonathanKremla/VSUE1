package dslab.mailbox;

import java.util.List;

/**
 * POJO representing an Email with all its Content
 */
public class Email {
  private String from;
  private String to;
  private String subject;
  private String data;
  private List<String> domains;

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

  public List<String> getDomains() {
    return domains;
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

  public void setDomains(List<String> domains){
    this.domains=domains;
  }


  @Override
  public String toString() {
    return "Email{" +
            "from='" + from + '\'' +
            ", to='" + to + '\'' +
            ", subject='" + subject + '\'' +
            ", data='" + data + '\'' +
            ", domains='" + domains + '\'' +
            '}';
  }
}
