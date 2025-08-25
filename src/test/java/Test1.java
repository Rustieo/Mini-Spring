import minis.beans.BeansException;
import minis.context.ClassPathXmlApplicationContext;

public class Test1 {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = null;
        ctx = new
                ClassPathXmlApplicationContext("testBean.xml");
        AService aService = null;
        try {
            aService = (AService)ctx.getBean("aservice");
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
        aService.sayHello();
    }
}
