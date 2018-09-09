package com.techcubing.server.handlers;

import com.sun.net.httpserver.HttpServer;
import java.lang.reflect.Constructor;
import org.reflections.Reflections;

import com.techcubing.server.framework.ServerState;

public class Handlers {
  public static void registerHandlers(HttpServer server, ServerState serverState) {
    Reflections reflections = new Reflections("com.techcubing.server.handlers");
    for (Class clazz : reflections.getTypesAnnotatedWith(Handler.class)) {
      Handler handlerAnnotation = (Handler) clazz.getAnnotation(Handler.class);
      try {
        Constructor cons = clazz.getConstructor(ServerState.class);
        BaseHandler handler = (BaseHandler) cons.newInstance(serverState);
        server.createContext(handlerAnnotation.path(), handler);
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println(
            "Failed to instantiate class " + clazz.getName() +
            " for path " + handlerAnnotation.path() + ". All handlers must have " +
            "a constructor with a single ServerState argument.");
      }
    }
  }
}
