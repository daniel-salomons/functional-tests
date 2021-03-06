package nl.vpro.poms.npoapi;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import nl.vpro.domain.api.media.RedirectEntry;
import nl.vpro.domain.api.media.RedirectList;
import nl.vpro.poms.AbstractApiTest;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Michiel Meeuwissen
 * @since 1.0
 */
@RunWith(Parameterized.class)
@Slf4j
public class ApiMediaRedirectsTest extends AbstractApiTest {


    private final RedirectEntry entry;

    public ApiMediaRedirectsTest(RedirectEntry entry) {
        this.entry = entry;
    }


    @Parameterized.Parameters
    public static Collection<Object[]> getDirects() {
        Response response = clients.getMediaService().redirects(null);
        try {
            assertThat(response.getStatus()).isEqualTo(200);
            RedirectList list = response.readEntity(RedirectList.class);
            assertThat(list).isNotEmpty();
            return list.getList().stream().map(e -> new Object[]{e}).collect(Collectors.toList());
        } finally {
            response.close();
        }
    }


    @Test
    public void testRedirect() {
        try {
            assertThat(clients.getMediaService().load(entry.getFrom(), null, null).getMid()).isEqualTo(entry.getTo());
        } catch (javax.ws.rs.NotFoundException nfe) {
            log.warn("For " + entry + ": " + nfe.getMessage());
        }
    }
}
