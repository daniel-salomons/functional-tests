package nl.vpro.poms.integration;

import java.time.Duration;
import java.time.Instant;

import javax.xml.bind.JAXB;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import nl.vpro.domain.media.AVType;
import nl.vpro.domain.media.MediaBuilder;
import nl.vpro.domain.media.Segment;
import nl.vpro.domain.media.update.SegmentUpdate;
import nl.vpro.poms.AbstractApiTest;
import nl.vpro.util.DateUtils;
import nl.vpro.util.TimeUtils;

import static nl.vpro.poms.Utils.waitUntil;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assume.assumeNotNull;

/**
 * @author Michiel Meeuwissen
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MediaBackendSegmentsTest extends AbstractApiTest {

    private static final String MID = "WO_VPRO_025057";
    private static final Duration ACCEPTABLE_DURATION = Duration.ofMinutes(3);

    private static String segmentMid;


    @Before
    public void setup() {

    }

    @Test
    public void test01createSegment() {
        SegmentUpdate update = SegmentUpdate.create(
            MediaBuilder.segment()
                .avType(AVType.VIDEO)
                .broadcasters("VPRO")
                .midRef(MID)
                .start(Duration.ofMillis(0))
                .mainTitle(title));
        JAXB.marshal(update, System.out);
        segmentMid = backend.set(update);
        System.out.println("Created " + segmentMid);

    }

    @Test
    public void test02WaitFor() throws Exception {
        assumeNotNull(segmentMid);
        waitUntil(ACCEPTABLE_DURATION, () -> {
            SegmentUpdate up = backend.get(segmentMid);
            return up != null;
        });


    }


    @Test
    public void test03WaitForInFrontend() throws Exception {
        assumeNotNull(segmentMid);
        Segment[] segments = new Segment[1];
        waitUntil(ACCEPTABLE_DURATION_FRONTEND, () -> {
            segments[0] = mediaUtil.loadOrNull(segmentMid);
            return segments[0] != null && TimeUtils.isLarger(
                TimeUtils.between(DateUtils.toInstant(segments[0].getLastPublished()), Instant.now()), Duration.ofMinutes(20)) ;
        });
        assertThat(segments[0].getMidRef()).isEqualTo(MID);
        assertThat(segments[0].getMainTitle()).isEqualTo(title);


    }

    @Test
    @Ignore
    public void testSegment() {
        Segment segment = (Segment) clients.getMediaService().load("POMS_VPRO_1460016", null, null);
        assertThat(segment.getMidRef()).isNotNull();
    }


}
