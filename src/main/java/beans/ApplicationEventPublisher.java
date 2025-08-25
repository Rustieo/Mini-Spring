package beans;

import beans.factory.ApplicationEvent;

public interface ApplicationEventPublisher {
    void publishEvent(ApplicationEvent event);
    void addApplicationListener(ApplicationListener listener);
}
