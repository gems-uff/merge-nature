/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import br.uff.ic.github.github.data.Language;
import br.uff.ic.github.github.data.Project;
import com.sun.corba.se.impl.orb.ORBVersionImpl;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author gleiph
 */
public class JPATest {

    public JPATest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void createPersist() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("Github");
        EntityManager manager = factory.createEntityManager();

        Project project = new Project();

        project.setId(0l);
        project.setCreatedAt("ini");
        project.setHtmlUrl("www...");
        project.setName("projectTest");
        project.setPriva(true);
        project.setUpdatedAt("final");
        project.setSearchUrl("www....");
        project.setDevelopers(15);

        List<Language> languages = new ArrayList<Language>();
        Language language = new Language();
        language.setName("Java");
        language.setPercentage(90);
        language.setSize(1098);

        languages.add(language);
        
        project.setLanguages(languages);

        
        manager.persist(language);
        manager.persist(project);

    }
}
