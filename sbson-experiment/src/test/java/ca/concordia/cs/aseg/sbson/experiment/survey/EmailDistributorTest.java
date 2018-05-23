package ca.concordia.cs.aseg.sbson.experiment.survey;

import org.junit.Test;

import static org.junit.Assert.*;

public class EmailDistributorTest {

    private EmailDistributor setupEmailDistributor() {
        EmailDistributor emailDistributor = new EmailDistributor();
        return emailDistributor;
    }

    @Test
    public void cleanEmail() {
        String dirtyEmail = "elliseghan.. at encs.concordia.ca";
        String cleanEmail = "elliseghan@encs.concordia.ca";
       // String output = setupEmailDistributor().cleanEmail(dirtyEmail);
       // assertEquals(cleanEmail, output);
    }

    /*@Test
    public void sendEmail() {
       // setupEmailDistributor().sendEmail("elliseghan@gmail.com");
        setupEmailDistributor().sendEmail("sillellis@yahoo.com");
        assertTrue(true);
    }*/
}