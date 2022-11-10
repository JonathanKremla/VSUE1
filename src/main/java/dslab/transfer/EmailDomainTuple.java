package dslab.transfer;


import dslab.mailbox.Email;

import java.util.List;

public class EmailDomainTuple {
  Email first;
  List<String> second;

  public EmailDomainTuple(Email first, List<String> second){
    this.first = first;
    this.second = second;
  }

  public Email getFirst() {
    return first;
  }

  public List<String> getSecond() {
    return second;
  }

  public void setFirst(Email first) {
    this.first = first;
  }

  public void setSecond(List<String> second) {
    this.second = second;
  }
}
