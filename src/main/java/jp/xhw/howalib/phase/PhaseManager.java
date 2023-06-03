package jp.xhw.howalib.phase;

import jp.xhw.howalib.HowaLib;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class PhaseManager<T> {

    private final Map<T, PhaseBase<T>> phaseMap;
    @Getter
    @Nullable
    private PhaseBase<T> currentPhase;

    public PhaseManager() {
        phaseMap = new HashMap<>();
    }

    public void update() {
        if (currentPhase != null) currentPhase.update();
    }

    public void addPhase(T key, PhaseBase<T> phase) {
        phase.setManager(this);
        phaseMap.put(key, phase);
        Method[] methods = phase.getClass().getMethods();
        for (Method method : methods) {
            PhaseEventHandler phaseEventHandler = method.getAnnotation(PhaseEventHandler.class);
            if (phaseEventHandler == null) continue;
            if (method.isBridge() || method.isSynthetic()) {
                continue;
            }
            Class<?> paramClass = method.getParameterTypes()[0];
            if (method.getParameterTypes().length != 1 || !Event.class.isAssignableFrom(paramClass)) {
                continue;
            }
            Class<? extends Event> eventClass = paramClass.asSubclass(Event.class);
            method.setAccessible(true);
            HowaLib.getInstance().getPlugin().getServer().getPluginManager()
                    .registerEvent(
                            eventClass,
                            phase,
                            phaseEventHandler.priority(),
                            (listener, event) -> {
                                try {
                                    if (!eventClass.isAssignableFrom(event.getClass())) {
                                        return;
                                    }
                                    if (currentPhase == null) return;
                                    if (currentPhase.equals(phase)) {
                                        method.invoke(listener, event);
                                    }
                                } catch (InvocationTargetException e) {
                                    throw new EventException(e.getCause());
                                } catch (Throwable t) {
                                    throw new EventException(t);
                                }
                            },
                            HowaLib.getInstance().getPlugin(),
                            phaseEventHandler.ignoreCancelled()
                    );
        }
    }

    public void changePhase(T key) {
        if (currentPhase != null) currentPhase.end();
        currentPhase = phaseMap.get(key);
        currentPhase.start();
    }

}
