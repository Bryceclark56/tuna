package me.bc56.tuna.events;

import me.bc56.tuna.ThreadManager;
import me.bc56.tuna.events.type.Event;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventManagerTest {

    EventManager eventManager = new EventManager();
    @Mock EventReceiver mockEventReceiver;

    @Test
    void shouldEnqueueEvent() {
        UUID fakeProducerId = UUID.randomUUID();
        String fakeEventType = "Hi, Dad."; //TODO: Should I randomly generate the type string?
        Event testEvent = new Event(fakeProducerId, fakeEventType);

        //TODO: Should I test permutations of these options?
        EventFilter eventFilter = new EventFilter();
        eventFilter.addEventSource(fakeProducerId);
        eventFilter.addEventType(fakeEventType);

        eventManager.registerReceiver(mockEventReceiver, eventFilter);

        new Thread(() -> eventManager.submitEvent(testEvent)).start();
        eventManager.loop();

        verify(mockEventReceiver, times(1)).enqueue(testEvent);
    }

    @Test
    void shouldNotEnqueueEvent() {
        UUID fakeProducerId = UUID.randomUUID();
        String fakeEventType = "Hi, Dad."; //TODO: Should I randomly generate the type string?
        String otherFakeEventType = "Hi, Mom.";
        Event testEvent = new Event(fakeProducerId, otherFakeEventType);

        //TODO: Should I test permutations of these options?
        EventFilter eventFilter = new EventFilter();
        eventFilter.addEventSource(fakeProducerId);
        eventFilter.addEventType(fakeEventType);

        eventManager.registerReceiver(mockEventReceiver, eventFilter);

        new Thread(() -> eventManager.submitEvent(testEvent)).start();
        eventManager.loop();

        verify(mockEventReceiver, times(0)).enqueue(testEvent);
    }
}
