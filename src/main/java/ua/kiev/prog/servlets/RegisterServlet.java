package ua.kiev.prog.servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import ua.kiev.prog.User;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        byte[] buf = requestBodyToArray(req);
        String bufStr = new String(buf, StandardCharsets.UTF_8);

        User user = User.fromJSON(bufStr);
        if (!isValidUser(user)) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        else if (isUserInFile(user)) {
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,ms ")) +
                    "the user already in file: " + user.getLogin());
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            saveUserToCSV(user);
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,ms ")) +
                    "the new user saved to file: " + user.getLogin());
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


    private static void printUser(String str) {
        Map<String, Object> map;
        map = new Gson().fromJson(str, new TypeToken<HashMap<String, String>>() {
        }.getType());
        String login = (String) map.get("login");
        System.out.println(map);
        System.out.println("login: " + login);
    }


    public boolean isUserInFile(User user) {
        File file = new File("/Users/Jupiter/Documents/Development/education/java/progua-pro/chat/chat-server/src/main/Users.csv");
        boolean isPresent = false;
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
        for (Map.Entry<String, String> entry : users.entrySet()) {
            if (currentUserLogin.equals(entry.getKey())) {
                isPresent = true;
                break;
            } else continue;

        }
        System.out.println(users);
        return isPresent;
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


    public void saveUserToCSV(User user) {
        String delimiter = ",";

        File file = new File("/Users/Jupiter/Documents/Development/education/java/progua-pro/chat/chat-server/src/main/Users.csv");

        try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) // дозапись
        {
            pw.println(user.getLogin() + delimiter + user.getPassword());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
