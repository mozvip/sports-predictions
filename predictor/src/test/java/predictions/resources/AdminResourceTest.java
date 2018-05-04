package predictions.resources;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import predictions.model.db.User;
import predictions.model.db.UserDAO;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AdminResourceTest {

    private static final UserDAO dao = mock(UserDAO.class);
    private static final HttpServletRequest request = mock(HttpServletRequest.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new AdminResource(dao))
            .build();

    private static List<User> allUsers = new ArrayList<>();
    private static User testUser = new User("test", "Test User", "test@testdomain.com", "password", null, null, 0, 0, 0, false, true);
    private static Map<String, User> users = new HashMap<>();

    @BeforeClass
    public static void setup() {

        for (int i=0; i<100; i++) {
            User testUser = new User("test", String.format("Test User %d", i), String.format("test%d@testdomain.com", i), "password", null, null, 0, 0, 0, false, true);
            allUsers.add(testUser);
        }

//         when(dao.toggleActive(eq("test"), eq("test@testdomain.com"))).then(testUser.setActive(!testUser.isActive()));

        when(dao.findUser(eq("test"), eq("test@testdomain.com"))).thenReturn(testUser);
    }

    @org.junit.Test
    public void getUsers() {
        resources.target("/admin/users").request().get();
        verify(dao).findAll("test");
    }

    @org.junit.Test
    public void getUsersWithNoPredictions() {
        resources.target("/admin/users").request().get();
        verify(dao).findUsersWithNoPredictions("test");
    }

    @org.junit.Test
    public void toggleActive() {
        resources.target("/admin/toggle-active").request().post(Entity.entity(testUser.getEmail(), MediaType.APPLICATION_JSON_TYPE));
        verify(dao).toggleActive("test", testUser.getEmail());
    }

    @org.junit.Test
    public void toggleAdmin() {
        resources.target("/admin/toggle-admin").request().post(Entity.entity(testUser.getEmail(), MediaType.APPLICATION_JSON_TYPE));
        verify(dao).toggleAdmin("test", testUser.getEmail());
    }
}