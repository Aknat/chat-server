package ua.kiev.prog.servlets;

import jakarta.servlet.http.*;
import ua.kiev.prog.Message;
import ua.kiev.prog.MessageList;
import ua.kiev.prog.PrivateListMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AddServlet extends HttpServlet {

    private MessageList msgList = MessageList.getInstance();
    private PrivateListMap msgListMap = PrivateListMap.getInstance();


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        byte[] buf = requestBodyToArray(req);
        String bufStr = new String(buf, StandardCharsets.UTF_8);

        Message msg = Message.fromJSON(bufStr);
        if (msg == null) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        else if (msg.getTo().isEmpty()) {
            msgList.add(msg);
            System.out.println(msg);
            System.out.println("all public messages: " + msgList.getList().toString());
        } else {
            msgListMap.putToMap(msg);
            System.out.println("all private messages: " + msgListMap.getMap().toString());
        }
    }

    private byte[] requestBodyToArray(HttpServletRequest req) throws IOException {
        InputStream is = req.getInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[10240];
        int r;

        do {
            r = is.read(buf);
            if (r > 0) bos.write(buf, 0, r);
        } while (r != -1);

        return bos.toByteArray();
    }
}
