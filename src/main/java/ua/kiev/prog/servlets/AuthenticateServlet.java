package ua.kiev.prog.servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.kiev.prog.User;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class AuthenticateServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        byte[] buf = requestBodyToArray(req);
        String bufStr = new String(buf, StandardCharsets.UTF_8);

        User user = User.fromJSON(bufStr);
        if (!isValidUser(user)) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        else if (!isValidPassword(user)) {
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,ms ")) +
                    "the user [" + user.getLogin() + "] provided invalid password");
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,ms ")) +
                    "the user [" + user.getLogin() + "] is allowed to chat");
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


    public boolean isValidPassword(User user) {
        File file = new File("/Users/Jupiter/Documents/Development/education/java/progua-pro/chat/chat-server/src/main/Users.csv");
        boolean isValidPassword = false;
        Map<String, String> users = new HashMap<>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String userStr = null;
        for (; ; ) {
            try {
                userStr = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (userStr == null) break;
            String[] entity = userStr.split(",");
            users.put(entity[0], entity[1]);
        }
        String currentUserLogin = user.getLogin();
        String currentUserPassword = user.getPassword();
        for (Map.Entry<String, String> entry : users.entrySet()) {
            if ((currentUserLogin.equals(entry.getKey())) && (currentUserPassword.equals(entry.getValue()))) {
                isValidPassword = true;
                System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,ms ")) +
                        "the user [" + user.getLogin() + "] is authenticated");
                break;
            } else continue;
        }
        return isValidPassword;
    }


    public boolean isValidUser(User user) {
        boolean isValid;

        if (user.getLogin() == null) isValid = false;
        else if (user.getPassword() == null) isValid = false;
        else if (user.getLogin().trim().isEmpty()) isValid = false;
        else if (user.getPassword().trim().isEmpty()) isValid = false;
        else isValid = true;

        return isValid;
    }
}


