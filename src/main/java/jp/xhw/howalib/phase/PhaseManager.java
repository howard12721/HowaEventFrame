package jp.xhw.howalib.phase;

import jp.xhw.howalib.HowaLib;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class PhaseManager<T> {

    private final Map<T, PhaseBase<T>> phaseMap;
    @Getter
    @Nullable
    private PhaseBase<T> currentPhase;
    private boolean started = false;

    public PhaseManager() {
        phaseMap = new HashMap<>();
    }

    public void update() {
        if (currentPhase != null) {
            if (!started) {
                currentPhase.onStart();
                started = true;
            }
            currentPhase.update();
        }
    }

    public void addPhase(T key, PhaseBase<T> phase) {
        final Plugin plugin = HowaLib.getInstance().getPlugin();

        phase.setManager(this);
        phaseMap.put(key, phase);
        if (currentPhase == null) currentPhase = phase;

        plugin.getServer().getPluginManager().registerEvents(phase, plugin);
        final Method[] methods = phase.getClass().getMethods();
        for (Method method : methods) {
            final PhaseEventHandler phaseEventHandler = method.getAnnotation(PhaseEventHandler.class);
            if (phaseEventHandler == null) continue;
            if (method.isBridge() || method.isSynthetic()) {
                continue;
            }
            final Class<?> paramClass = method.getParameterTypes()[0];
            if (method.getParameterTypes().length != 1 || !Event.class.isAssignableFrom(paramClass)) {
                continue;
            }
            final Class<? extends Event> eventClass = paramClass.asSubclass(Event.class);
            method.setAccessible(true);
            plugin.getServer().getPluginManager()
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
                            plugin,
                            phaseEventHandler.ignoreCancelled()
                    );
        }
    }

    public void changePhase(T key) {
        Bukkit.getScheduler().runTask(HowaLib.getInstance().getPlugin(), () -> {
            if (currentPhase != null) currentPhase.end();
            currentPhase = phaseMap.get(key);
            currentPhase.start();
        });
    }

}
