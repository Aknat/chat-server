package ua.kiev.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

public class PrivateListMap {

    private static final PrivateListMap msgListMap = new PrivateListMap();

    private final Gson gson;
    private final Map<String, PrivateMessageList> map = new LinkedHashMap<>();

    private PrivateListMap() {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    public Map<String, PrivateMessageList> getMap() {
        return map;
    }

    public static PrivateListMap getInstance() {
        return msgListMap;
    }

    public void putToMap(Message m) {

        if (map.size() == 0) {
            PrivateMessageList list1 = new PrivateMessageList();
            PrivateMessageList list2 = new PrivateMessageList();
            list1.add(m);
            list2.add(m);
            map.put(m.getTo(), list1);
            map.put(m.getFrom(), list2);

        } else {
            int receiverCount = 0;
            int senderCount = 0;

            for (Map.Entry<String, PrivateMessageList> entry : map.entrySet()) {
                if (entry.getKey().equals(m.getTo())) {
                    entry.getValue().add(m);
                    receiverCount = 1;
                }
                if (entry.getKey().equals(m.getFrom())) {
                    entry.getValue().add(m);
                    senderCount = 1;
                }
            }
            if (receiverCount == 0) {
                PrivateMessageList list = new PrivateMessageList();
                list.add(m);
                map.put(m.getTo(), list);
            }
            if (senderCount == 0) {
                PrivateMessageList list = new PrivateMessageList();
                list.add(m);
                map.put(m.getFrom(), list);
            }
        }
    }

    public PrivateMessageList extractPrivateMessageList(String user) {
        PrivateMessageList list = new PrivateMessageList();
        for (Map.Entry<String, PrivateMessageList> entry : map.entrySet()) {
            if (entry.getKey().equals(user)) {
                list = entry.getValue();
                break;
            }
        }
        return list;
    }

    @Override
    public String toString() {
        return "MessageListMap{" +
                "map=" + map +
                '}';
    }
}
