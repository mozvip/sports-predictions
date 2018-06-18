package predictions.resources;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.mockito.stubbing.Answer;
import predictions.model.GamesManager;
import predictions.model.db.ActualResultDAO;
import predictions.model.db.MatchPredictionDAO;
import predictions.model.db.User;
import predictions.model.db.UserDAO;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

public class AdminResourceTest {

    private static final UserDAO dao = mock(UserDAO.class);
    private static final MatchPredictionDAO matchPredictionDAO = mock(MatchPredictionDAO.class);
    private static final ActualResultDAO actualResultDAO = mock(ActualResultDAO.class);
    private static final GamesManager gamesManager = mock(GamesManager.class);
    private static final AdminResource adminResource = spy(new AdminResource(dao, matchPredictionDAO, actualResultDAO, gamesManager));

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(adminResource)
            .build();

    public static final String COMMUNITY = "test";

    private static List<User> allUsers = new ArrayList<>();
    private static User testUser = new User("test", "Test User", "test@testdomain.com", "password", null, null, 0, 0, 0, false, true, false);
    private static Map<String, User> users = new HashMap<>();

    @BeforeClass
    public static void setup() {

        when(adminResource.getCommunity()).thenReturn("test");

        for (int i=0; i<100; i++) {
            User testUser = new User(COMMUNITY, String.format("Test User %d", i), String.format("test%d@testdomain.com", i), "password", null, null, 0, 0, 0, false, true, false);
            allUsers.add(testUser);
        }

        doAnswer((Answer) invocation -> {
            System.out.println("test");
            return null;
        }).when(dao).toggleActive(eq(COMMUNITY), eq("test@testdomain.com"));

        when(dao.findUser(eq(COMMUNITY), eq(testUser.getEmail()))).thenReturn(testUser);
    }

    @org.junit.Test
    public void getUsers() {
        resources.target("/admin/users").request().get();
        verify(dao).findAll(COMMUNITY);
    }

    @org.junit.Test
    public void getUsersWithNoPredictions() {
        resources.target("/admin/users-no-prediction").request().get();
        verify(dao).findUsersWithNoPredictions(COMMUNITY);
    }

    @org.junit.Test
    public void toggleActive() {
        resources.target(String.format("/admin/toggle-active/%s", testUser.getEmail())).request().post(null);
        verify(dao).toggleActive(COMMUNITY, testUser.getEmail());
    }

    @org.junit.Test
    public void toggleAdmin() {
        resources.target(String.format("/admin/toggle-admin/%s", testUser.getEmail())).request().post(null);
        verify(dao).toggleAdmin(COMMUNITY, testUser.getEmail());
    }
}