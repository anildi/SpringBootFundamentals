//package ttl.larku.didemo.circular;
//
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
//import org.springframework.stereotype.Component;
//
//@Component
//class A {
//    private B b;
//    public A(B b) {
//        this.b = b;
////        System.out.println("A::Ctor, a class in A" + this.getClass() + ", a class in B: " + this.b.getA().getClass() + ", z: " + z);
//    }
//
//    public void doA() {
//        System.out.println("Doing A");
//    }
//
//    public void callB() {
//        b.doB();
//    }
//
////    @Autowired
//    public void setB(B b) {
//        this.b = b;
//    }
//}
//
//@Component
//class B {
//    private A a;
//    public B(A a) {
//        this.a = a;
//        System.out.println("a's class in B: " + this.a.getClass());
//    }
//
////    public B(@Lazy A a) {
////        this.a = a;
//////        System.out.println("a's class in B: " + this.a.getClass());
////    }
//
//    public void callA() {
//        a.doA();
//        System.out.println("In B::callA " + this.a.getClass());
//    }
//
//    public void doB() {
//        System.out.println("Doing B::doB");
//    }
//
//    public A getA() {
//        return a;
//    }
////    @Autowired
//    public void setA(A a) {
//        this.a = a;
//    }
//}
//
//class DriverJava
//{
//    public static void main(String[] args) {
////        B b  = new B();
////        A a = new A();
////        b.setA(a);
////        a.setB(b);
//    }
//}
//
//class CircularDriver
//{
//
//    public static void main(String[] args) {
//        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
//        context.scan("ttl.larku.didemo.circular");
//        context.refresh();
//
//        B b = context.getBean("b", B.class);
//        b.callA();
//
//        A a = context.getBean("a", A.class);
//        a.callB();
//    }
//}
