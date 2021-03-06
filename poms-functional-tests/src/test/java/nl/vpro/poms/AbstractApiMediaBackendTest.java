package nl.vpro.poms;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.Timeout;

import nl.vpro.api.client.utils.Config;
import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.Image;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.update.AssemblageConfig;
import nl.vpro.domain.media.update.LocationUpdate;
import nl.vpro.domain.media.update.MediaUpdate;
import nl.vpro.domain.media.update.ProgramUpdate;
import nl.vpro.domain.support.License;
import nl.vpro.rs.media.MediaRestClient;
import nl.vpro.util.IntegerVersion;
import nl.vpro.util.Version;

import static nl.vpro.domain.media.MediaBuilder.program;

/**
 * @author Michiel Meeuwissen
 * @since 1.0
 */
@Slf4j
public abstract class AbstractApiMediaBackendTest extends AbstractApiTest {

    public static final String    MID                = "WO_VPRO_025057";
    protected static final String MID_WITH_LOCATIONS = "WO_VPRO_025700";
    protected static final String ANOTHER_MID        = "WO_VPRO_4911154";


    protected static final MediaRestClient backend =
        MediaRestClient.configured(CONFIG.env(), CONFIG.getProperties(Config.Prefix.npo_backend_api))
            .followMerges(true)
            .validateInput(true)
            .lookupCrids(true)
            //.version("5.7")
            .build();


    protected static final MediaRestClient backend_authority  =
        MediaRestClient.configured(CONFIG.env(), CONFIG.getProperties(Config.Prefix.npo_backend_api))
            .followMerges(true)
            .validateInput(true)
            .lookupCrids(true)
            .owner(OwnerType.AUTHORITY)
            //.version("5.7")
            .build();
    protected static final String backendVersion = backend.getVersion();
    protected static IntegerVersion backendVersionNumber;


    static {
        try {
            backendVersionNumber = backend.getVersionNumber();
        } catch (Exception e) {
            backendVersionNumber = Version.of(0);

        }
        log.info("Using {} ({} -> {})", backend, backendVersion, backendVersionNumber);
    }

    public Image createImage() {
        Image image = new Image(OwnerType.BROADCASTER, ImageType.PICTURE, title);
        try {
            image.setImageUri("https://placeholdit.imgix.net/~text?txt=" + URLEncoder.encode(title, "UTF-8") + "&w=150&h=150");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        image.setLicense(License.CC_BY);
        image.setSourceName("placeholdit");
        image.setCredits(getClass().getName());
        return image;

    }

    public Segment createSegment() {
        return
            MediaBuilder.segment()
                .mainTitle(title)
                .ageRating(AgeRating.ALL)
                .start(Duration.ofSeconds(70))
                .avType(AVType.MIXED)
                .build();
    }

    public Location createLocation(int count) {
        return
            Location.builder()
                .avAttributes(AVAttributes.builder().avFileFormat(AVFileFormat.H264).build())
                //.platform(Platform.INTERNETVOD)
                .programUrl("https://www.vpro.nl/" + count)
                .build();

    }


    @Rule
    public Timeout globalTimeout = Timeout.millis(Duration.ofMinutes(15).toMillis());


    @Before
    public void abstractSetUp() {
        backend.setValidateInput(true);
        backend.setStealCrids(AssemblageConfig.Steal.IF_DELETED);
        backend.setLookupCrids(true);
    }

    @BeforeClass
    public static void checkMids() {
        try {
            {
                MediaUpdate<?> mediaUpdate = backend.get(MID);
                boolean needSet = false;
                if (mediaUpdate == null) {
                    log.info("No media found {}.  Now creating", MID);
                    mediaUpdate = ProgramUpdate.create();
                    ((ProgramUpdate) mediaUpdate).setType(ProgramType.CLIP);
                    mediaUpdate.setAVType(AVType.MIXED);
                    mediaUpdate.setMid(MID);
                    mediaUpdate.setAgeRating(AgeRating.ALL);
                    needSet = true;
                }
                if (! mediaUpdate.getBroadcasters().contains("VPRO")) {
                    mediaUpdate.setBroadcasters("VPRO");
                    needSet = true;
                }
                if (!Objects.equals(mediaUpdate.getMainTitle(), "testclip michiel")) {
                    mediaUpdate.setMainTitle("testclip michiel");
                    needSet = true;

                }
                if (needSet) {
                    backend.set(mediaUpdate);
                }
            }
            {
                MediaUpdate<?> mediaUpdate = backend.get(MID_WITH_LOCATIONS);
                if (mediaUpdate == null) {
                    mediaUpdate = ProgramUpdate.create();
                    ((ProgramUpdate) mediaUpdate).setType(ProgramType.CLIP);
                    mediaUpdate.setAVType(AVType.MIXED);
                    mediaUpdate.setBroadcasters("VPRO");
                    mediaUpdate.setMid(MID_WITH_LOCATIONS);
                    mediaUpdate.setMainTitle("Test");
                    mediaUpdate.setAgeRating(AgeRating.ALL);

                }
                if (mediaUpdate.getLocations().isEmpty()) {
                    log.info("No media found {} with locations.  Now creating", MID_WITH_LOCATIONS);
                    mediaUpdate.setLocations(LocationUpdate.builder()
                        .programUrl("http://content.omroep.nl/vpro/poms/world/15/04/88/63/NPO_bb.m4v")
                        .bitrate(678000)
                        .format(AVFileFormat.M4V)
                        .build());
                    backend.set(mediaUpdate);
                } else if (mediaUpdate.getLocations().stream().filter(l -> ! l.isUnderEmbargo()).collect(Collectors.toList()).isEmpty()){
                    log.info("All locations of {} are under embargo. This is incorrect. Publishing them all.", mediaUpdate);
                    for (LocationUpdate l : mediaUpdate.getLocations()) {
                        l.setPublishStartInstant(null);
                        l.setPublishStopInstant(null);
                    }
                    backend.set(mediaUpdate);
                }
            }
            ProgramUpdate anotherProgramUpdate = backend.get(ANOTHER_MID);
            if (anotherProgramUpdate == null) {
                log.info("No media found {}. Now creating", ANOTHER_MID);
                log.info(
                    backend.set(
                        ProgramUpdate.create(program()
                            .broadcasters("VPRO")
                            .mid(ANOTHER_MID)
                            .avType(AVType.VIDEO)
                            .type(ProgramType.CLIP)
                            .mainTitle("test"))
                    )
                );
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
