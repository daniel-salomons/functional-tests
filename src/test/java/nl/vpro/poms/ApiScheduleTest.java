package nl.vpro.poms;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import nl.vpro.domain.api.ApiScheduleEvent;
import nl.vpro.domain.api.media.ScheduleResult;
import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.Net;
import nl.vpro.domain.media.Schedule;
import nl.vpro.domain.user.Broadcaster;

import static org.assertj.core.api.Java6Assertions.assertThat;


public class ApiScheduleTest extends AbstractApiTest {


    private static LocalDate today = LocalDate.now(Schedule.ZONE_ID);

    static {
        System.out.println("Today : " + today);
    }

    public ApiScheduleTest() {


    }

    @Before
    public void setup() {

    }


    @Test
    public void list() throws Exception {
        ScheduleResult o = clients.getScheduleService().list(today, null, null, null, "ASC", 0L, 240);
        assertThat(o.getSize()).isGreaterThan(10);
    }

    @Test
    public void listBroadcaster() throws Exception {
        ScheduleResult o = clients.getScheduleService().listBroadcaster("VPRO", today, null, null, null, "ASC", 0L, 240);
        assertThat(o.getSize()).isGreaterThan(10);
        for (ApiScheduleEvent item : o.getItems()) {
            assertThat(item.getMediaObject().getBroadcasters()).contains(new Broadcaster("VPRO"));
        }
    }


    @Test
    public void listChannel() throws Exception {
        ScheduleResult o = clients.getScheduleService().listChannel("NED1", today, null, null, null, "ASC", 0L, 240);
        assertThat(o.getSize()).isGreaterThan(10);
        for (ApiScheduleEvent item : o.getItems()) {
            assertThat(item.getChannel()).isEqualTo(Channel.NED1);
        }
    }

    @Test
    public void listNet() throws Exception {
        ScheduleResult o = clients.getScheduleService().listNet("ZAPP", today, null, null, null, "ASC", 0L, 240);
        assertThat(o.getSize()).isGreaterThan(2);
        for (ApiScheduleEvent item : o.getItems()) {
            assertThat(item.getNet()).isEqualTo(new Net("ZAPP"));
        }
    }



    @Test
    public void nowForBroadcaster() throws Exception {
        ApiScheduleEvent o = clients.getScheduleService().nowForBroadcaster("VPRO", null);
        assertThat(o.getMediaObject().getBroadcasters()).contains(new Broadcaster("VPRO"));
    }

    @Test
    public void nextForBroadcaster() throws Exception {
        ApiScheduleEvent o = clients.getScheduleService().nextForBroadcaster("VPRO", null);
        System.out.println(o);
        assertThat(o.getMediaObject().getBroadcasters()).contains(new Broadcaster("VPRO"));


    }


    @Test
    public void nowForChannel() throws Exception {
        ApiScheduleEvent o = clients.getScheduleService().nowForChannel("NED1", null);
        System.out.println(o);
        assertThat(o.getChannel()).isEqualTo(Channel.NED1);


    }

    @Test
    public void nextForChannel() throws Exception {
        ApiScheduleEvent o = clients.getScheduleService().nextForChannel("NED1", null);
        System.out.println(o);
        assertThat(o.getChannel()).isEqualTo(Channel.NED1);


    }


}