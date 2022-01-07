package ua.kiev.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PrivateMessageList {

    public List<Message> privateList = new LinkedList<>();
    private final Gson gson;

    PrivateMessageList() {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    public synchronized void add(Message m) {
        privateList.add(m);
    }

    public synchronized String toJSON(int n) {
        if (n >= privateList.size()) return null;
        return gson.toJson(new JsonMessages(privateList, n));
    }

    @Override
    public String toString() {
        return "PrivateMessageList{" +
                "privateList=" + privateList +
                '}';
    }
}
