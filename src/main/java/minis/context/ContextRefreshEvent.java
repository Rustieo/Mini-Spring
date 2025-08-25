package minis.context;

public class ContextRefreshEvent extends ApplicationContextEvent {
    private static final long serialVersionUID = 1L;
    public ContextRefreshEvent(ApplicationContext arg0) {
        super(arg0);
    }

    public String toString() {
        return this.msg;
    }
}
