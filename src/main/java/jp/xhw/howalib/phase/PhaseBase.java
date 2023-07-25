package jp.xhw.howalib.phase;

import jp.xhw.howalib.HowaLib;
import lombok.Setter;
import org.bukkit.event.Listener;

import java.util.logging.Level;

@SuppressWarnings("unused")
public abstract class PhaseBase<T, Z> implements Listener {

    @Setter
    private PhaseManager<T, Z> manager;

    protected PhaseBase() {
    }

    public void start() {
        try {
            onStart();
        } catch (Exception e) {
            HowaLib.getInstance().getLogger().log(Level.SEVERE, "フェーズの開始中に例外が発生しました", e);
        }
    }

    public void update() {
        try {
            onUpdate();
        } catch (Exception e) {
            HowaLib.getInstance().getLogger().log(Level.SEVERE, "フェーズの更新中に例外が発生しました", e);
        }
    }

    public void end() {
        try {
            onEnd();
        } catch (Exception e) {
            HowaLib.getInstance().getLogger().log(Level.SEVERE, "フェーズの終了中に例外が発生しました", e);
        }
    }

    protected Z getData() {
        return manager.getData();
    }

    abstract protected void onStart();

    abstract protected void onUpdate();

    abstract protected void onEnd();

    protected void changeState(T key) {
        manager.changePhase(key);
    }

}
