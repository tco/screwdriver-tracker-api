package org.screwdriver.tracker.service;

import com.googlecode.flyway.core.Flyway;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.screwdriver.tracker.config.ApplicationConfig;
import org.screwdriver.tracker.config.DataSourceConfig;
import org.screwdriver.tracker.entity.Event;
import org.screwdriver.tracker.entity.EventParameter;
import org.screwdriver.tracker.repository.EventRepository;
import org.screwdriver.tracker.repository.TrackerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DataSourceConfig.class, ApplicationConfig.class })
public class RepositoryEventDataServiceIntegrationTest {

    @Autowired
    private IEventDataService eventDataService;

    @Autowired
    Flyway flyway;

    @Autowired
    private EntityManager em;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TrackerRepository trackerRepository;

    private Map<String, String> eventData;

    @Before
    public void setup() {
        flyway.clean();
        flyway.migrate();
        eventData = RepositoryEventDataServiceTest.createEventData();
        eventData.put(RepositoryEventDataServiceTest.KEY_MAC, RepositoryEventDataServiceTest.getMacForEventData(eventData));
        eventDataService.saveEvent(eventData);
    }

    @Test
    @Transactional
    public void shouldSaveTrackerEvent() {
        List<Event> events = eventRepository.findByTrackerTrackerCode(RepositoryEventDataServiceTest.VALUE_TRACKER_CODE);
        Event event = events.get(0);
        assertEquals(1, events.size());
        assertEquals(RepositoryEventDataServiceTest.VALUE_VERSION, event.getVersion());
        assertEquals(new DateTime(RepositoryEventDataServiceTest.VALUE_TIME), new DateTime(event.getEventTimestamp()));
        assertEventParameters(event.getEventParameters());
    }

    private void assertEventParameters(List<EventParameter> eventParameters) {
        assertEquals(2, eventParameters.size());
        Map<String, String> entries = new HashMap<>();
        for(EventParameter eventParameter : eventParameters) {
            entries.put(eventParameter.getKey(), eventParameter.getValue());
        }
        assertEquals(RepositoryEventDataServiceTest.VALUE_X_VARIABLE, entries.get(RepositoryEventDataServiceTest.KEY_X_VARIABLE));
        assertEquals(RepositoryEventDataServiceTest.VALUE_NONCE, entries.get(RepositoryEventDataServiceTest.KEY_NONCE));
    }

    @After
    public void teardown() {
        em.clear();
    }
}
