
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.HashMap;


@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static File file = new File("users.txt");
    private static HashMap<String, String> sessionUsers = new HashMap<>(); // 5b - пользователт за сессию
    private static HashMap<String, Integer> countErr = new HashMap<>();  // количество неправильный попыток ввода пароля
    private static Integer N = 2;  // N раз можно ошибиться

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Cookie cookies[] = req.getCookies();
        if (session.getAttribute("login") != null) {
            req.setAttribute("login", session.getAttribute("login"));
            this.getServletContext().getRequestDispatcher("/welcome.jsp").forward(req, resp);
        }
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        Boolean remember = req.getParameter("box") == null ? false : true;  // чекбокс
// Раскоментировать, чтобы работали cookies
      /*if (login.equals("") && password.equals("")) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("login")) {
                    login = cookie.getValue();
                }
                if (cookie.getName().equals("password")) {
                    password = cookie.getValue();
                }
            }
        }*/
        try(FileInputStream fstream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));) {

            String userName = "";
            String userPassword = "";
            Boolean isExist = false;   // есть ли пользователь с таким login
            while (true){
                userName = br.readLine();
                if (userName == null) break;
                userPassword = br.readLine();
                System.out.println(userName);
                System.out.println(userPassword);
                System.out.println("\n\n\n\n\n");
                if (userName.equals(login)) {
                    isExist = true;
                    break;
                }
            }

            if (isExist) {
                if (password.equals(userPassword)) {
// Раскоментировать, чтобы работали cookies
                   /* Cookie loginCookie = new Cookie("login", userName);
                    Cookie passwordCookie = new Cookie("password", userPassword);
                    resp.addCookie(loginCookie);
                    resp.addCookie(passwordCookie);*/
                    if (remember) {
                        session.setAttribute("login", userName);
                        session.setAttribute("password", userPassword);
                    }
                    sessionUsers.put(login, password);
                    req.setAttribute("login", login);
                    this.getServletContext().getRequestDispatcher("/welcome.jsp").forward(req, resp);
                } else {
                    if (countErr.containsKey(login)) {
                        Integer count = countErr.get(login);
                        if (count >= N) {
                            req.setAttribute("login", login);
                            this.getServletContext().getRequestDispatcher("/change.jsp").forward(req, resp);
                            return;
                        } else {
                            count++;
                            countErr.put(login, count);
                        }
                    } else {
                        countErr.put(login, 1);
                    }
                    resp.sendRedirect("error.html");
                }
            } else {
                resp.sendRedirect("create.html");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        boolean isChange = req.getParameter("action").equals("change") ? true : false;
        if (isChange) { // если запро из change.jsp меняем пароль
            File newFile = new File("temp.txt");
            newFile.createNewFile();
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            FileWriter writer = new FileWriter(newFile);
            while (true) {
                String userLogin = reader.readLine();
                if (userLogin == null) break;
                String userPassword = reader.readLine();
                if (userLogin.equals(login)) {
                    continue;
                }
                writer.write(userLogin);
                writer.write(System.lineSeparator());
                writer.write(userPassword);
                writer.write(System.lineSeparator());
            }
            writer.write(login);
            writer.write(System.lineSeparator());
            writer.write(password);
            reader.close();
            writer.close();
            file.delete();
            newFile.renameTo(file);
            countErr.put(login, 0);
            resp.sendRedirect("login.html");
            return;
        }
        if (login.equals("") && password.equals(""))  {
            resp.sendRedirect("create.html");
            return;
        }
        try(FileWriter writer = new FileWriter(file, true)) {
            writer.write(System.lineSeparator());
            writer.write(login);
            writer.write(System.lineSeparator());
            writer.write(password);
            writer.flush();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        resp.sendRedirect("login.html");
    }
}
