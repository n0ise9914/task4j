# Usage

```java
class User {
    public String id;
}
public Test() {
    TaskManager manager = new TaskManager();
    manager.setMaximumPoolSize(300);
    manager.registerIdProvider(User.class, u -> ((User) u).id);
    User user = new User();
    user.id = "foo";
    manager.schedule("bar", new Task() {
        @Override
        public void execute() {
            
        }
    }, 5000L, 5000L, user);
    manager.kill("bar", user);
}
```
