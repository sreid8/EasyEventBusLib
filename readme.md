# EasyEventBusLib

EasyEventBusLib is an easy-to-use Java Event Bus library. EasyEventBusLib has the following features:
  - One interface to implement containing one method
  - Easily configurable Listener Notification Methods
  - Easily have a different Event Bus for different types of events

**EasyEventBusLib was designed to prevent the need to write annoying event notification code without relying on extremely complex event libaries that have features you'll never use**

### Documentation
Eventually, I'll get around to writing some wiki content. But until then, the javadocs in the code aren't half bad and I reference them when I need to look at how something works. They're rather detailed and get into how to use things like the Event Listener Notification Methods.

### Use in your code

Binary builds might be provided sometime in the future. At this point, only source is provided. Requires Java 1.7+ Though with minor modifictions, it could easily get brought down to 1.6 or possibly even 1.5.

```sh
git clone https://github.com/sreid8/easyeventbuslib
```
And it can be added to a Gradle Build easily.

In build.gradle:
```gradle
compile project(":easyeventbuslib")
```

In settings.gradle:
```gradle
project(":easyeventbuslib").projectDir = file("/path/to/easyevenbuslib")
```
### Use

Use is extremely simple.

1. Implement the IEventListener interface:
```java
public class Example implements IEventListener {
    @Override
    public boolean handleEvent(Event event) {
        //steps to handle event
        return true;
    }
}
```

2. Register your listeners:
```java
//get the registry
IEventListener someListenerImpl = new SomeListenerImpl();
EventListenerRegistry registry = EventListenerRegistryManager.getManager().getRegistry("regID");
registry.registerListener(someListenerImpl);
```

3. Send some events
```java
public class SomeEventSender {
    EventListenerRegistry registry = EventListenerRegistryManager.getRegistry("regID");
    public void someMethod() {
        this.registry.notifyListeners(new Event(this, SomeStateEnum.THIS_STATE));
    }
}
```

4. Unregister Listeners as needed
```java
registry.deregisterListener(someListenerImpl);
```

### Examples
I'm working on some examples, they'll be posted eventually and I'll link to that repo here when that happens.

### Contributing

I don't pretend to know everything. If you'd like to add something or improve something I've already done or write some better unit tests, go for it!

The wiki, defintely the wiki. That needs to be done. The javadocs are pretty good... they just need to be in wiki form.

### To Dos
- Wiki
- Examples
- Better Unit Tests
- Performance Improvements
- Better Thread Management
- Drop Java version requirement from 1.7 to 1.5/1.6

#### Building from source
1. Clone the repo
2. 
```sh
cd easyeventbuslib
gradle jar
```

### License

Apache License 2.0

https://www.apache.org/licenses/LICENSE-2.0
