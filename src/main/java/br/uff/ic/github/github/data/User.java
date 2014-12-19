package br.uff.ic.github.github.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class User {

    private String login;
    private String password;

    private static List<User> users;
    private static int current;

    public static void init() {
        users = new ArrayList<>();
        users.add(createUser("maparao", "fake1234"));

//TODO: add logins
    }

    public static User nextUser() {
        current = (++current) % users.size();
        return users.get(current);
    }

    private static User createUser(String login, String password) {
        User user = new User();

        user.setLogin(login);
        user.setPassword(password);

        return user;
    }

    /**
     * @return the login
     */
    public String getLogin() {
        return login;
    }

    /**
     * @param login the login to set
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

}
