package nl.vpro.poms.npoapi;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import nl.vpro.domain.api.ApiScheduleEvent;
import nl.vpro.domain.api.SearchResultItem;
import nl.vpro.domain.api.media.*;
import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.Net;
import nl.vpro.domain.media.Schedule;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.poms.AbstractApiTest;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
public class ApiScheduleTest extends AbstractApiTest {


    private static LocalDate today = LocalDate.now(Schedule.ZONE_ID);

    static {
        log.info("Today : " + today);
    }

    public ApiScheduleTest() {


    }

    @Before
    public void setup() {

    }


    @Test
    public void list() {
        ScheduleResult o = clients.getScheduleService().list(today, null, null, null, "ASC", 0L, 240);
        assertThat(o.getSize()).isGreaterThan(10);
    }

    @Test
    public void listBroadcaster() {
        int sizeOfWeek = 0;
        List<ApiScheduleEvent> items = new ArrayList<>();
        for(LocalDate date = today; date.isAfter(today.minusDays(7)); date = date.minusDays(1)) {
            ScheduleResult o = clients.getScheduleService().listBroadcaster("VPRO", date, null, null, "broadcasters", "ASC", 0L, 240);
            sizeOfWeek += o.getSize();
            items.addAll(o.getItems());
        }
        assertThat(sizeOfWeek).isGreaterThan(10);
        for (ApiScheduleEvent item : items) {
            //log.info("item  " + i++ + " " + item.getMediaObject().getMid());
            assertThat(item.getParent().getBroadcasters()).contains(new Broadcaster("VPRO"));
        }
    }


    @Test
    public void listChannel() {
        ScheduleResult o = clients.getScheduleService().listChannel("NED1", today, null, null, null, "ASC", 0L, 240);
        assertThat(o.getSize()).isGreaterThan(10);
        for (ApiScheduleEvent item : o.getItems()) {
            assertThat(item.getChannel()).isEqualTo(Channel.NED1);
        }
    }

    @Test
    public void listNet() {
        ScheduleResult o = clients.getScheduleService().listNet("ZAPP", today, null, null, null, "ASC", 0L, 240);
        assertThat(o.getSize()).isGreaterThan(2);
        for (ApiScheduleEvent item : o.getItems()) {
            assertThat(item.getNet()).isNotNull();
            assertThat(item.getNet()).isEqualTo(new Net("ZAPP"));

        }
    }



    @Test
    public void nowForBroadcaster() {
        try {
            ApiScheduleEvent o = clients.getScheduleService().nowForBroadcaster("VPRO", null);
            assertThat(o.getParent().getBroadcasters()).contains(new Broadcaster("VPRO"));
        } catch (javax.ws.rs.NotFoundException nfe) {
            log.info("Ok, no current schedule for VPRO");
        }
    }

    @Test(expected = javax.ws.rs.NotFoundException.class)
    public void nowForBroadcasterNotFound() {
        clients.getScheduleService().nowForBroadcaster("TELEAC", null);
    }

    @Test
    public void nextForBroadcaster() {
        ApiScheduleEvent o = clients.getScheduleService().nextForBroadcaster("VPRO", null);
        log.info("{}", o);
        assertThat(o.getParent().getBroadcasters()).contains(new Broadcaster("VPRO"));


    }


    @Test
    public void nowForChannel() {
        try {
            ApiScheduleEvent o = clients.getScheduleService().nowForChannel("NED1", null);
            log.info("{}", o);
            assertThat(o.getChannel()).isEqualTo(Channel.NED1);
        } catch (javax.ws.rs.NotFoundException nfe) {
            log.warn("Ok, no current schedule for NED1");
        }

    }

    @Test(expected = javax.ws.rs.NotFoundException.class)
    public void nowForChannelNotFound() {
        ApiScheduleEvent o = clients.getScheduleService().nowForChannel("H1NL", null);
        log.error("Found {}", o);

    }

    @Test
    public void nextForChannel() {
        ApiScheduleEvent o = clients.getScheduleService().nextForChannel("NED1", null);
        log.info("{}", o);
        assertThat(o.getChannel()).isEqualTo(Channel.NED1);


    }

    @Test
    // https://jira.vpro.nl/browse/MSE-3533
    public void testWithProperties() {
        MediaForm mediaForm = MediaFormBuilder.form().mediaIds("NCRV_1347071").build();
        ScheduleForm form = ScheduleForm.from(mediaForm);
        ScheduleSearchResult result = clients.getScheduleService().find(form, null, "descendantOf", 0L, 4);
        assertThat(result.getItems().size()).isGreaterThanOrEqualTo(1);
        for (SearchResultItem<? extends ApiScheduleEvent> e : result) {
            // NCRV_1347071 is descendant!
            assertThat(e.getResult().getParent().getDescendantOf()).isNotEmpty();
        }
    }


}
