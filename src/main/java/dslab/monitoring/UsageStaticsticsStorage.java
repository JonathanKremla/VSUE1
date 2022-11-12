package dslab.monitoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class UsageStaticsticsStorage {
  //stores Usage statistics in form <host>:<port> <email-address>
  private static List<String> usageStatistics = new ArrayList<>();

  public static boolean add(String usageStatistic){
    usageStatistic = usageStatistic.split("\n")[0];
    if(!usageStatistic.matches("(.*):[0-9]* (.*)@(.*)")){
      return false;
    }
    usageStatistics.add(usageStatistic);
    return true;
  }

  public static List<String> addresses(){
    List<String> addresses = new ArrayList<>();
    HashMap<String, Integer> count = new HashMap<>();
    for (String usageStatistic : usageStatistics) {
      String address = usageStatistic.split(" ")[1];
      Integer c = count.get(address) == null ? 1 : count.get(address);
      count.put(address, c + 1);
      if (!addresses.contains(address)) {
        addresses.add(address);
      }
    }
    return addresses.stream().map(x -> x + " " + count.get(x)).collect(Collectors.toList());
  }

  public static List<String> servers(){
    List<String> servers = new ArrayList<>();
    HashMap<String, Integer> count = new HashMap<>();
    for (String usageStatistic : usageStatistics) {
      String server = usageStatistic.split(" ")[0];
      int c = count.get(server) == null ? 1 : count.get(server);
      count.put(server, c + 1);
      if (!servers.contains(server)) {
        servers.add(server);
      }
    }
    return servers.stream().map(x -> x + " " + count.get(x)).collect(Collectors.toList());

  }

}
