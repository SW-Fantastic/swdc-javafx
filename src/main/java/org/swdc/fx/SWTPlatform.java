package org.swdc.fx;

import jakarta.inject.Singleton;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tray;

@Singleton
public class SWTPlatform {

    private Display display;

    private Tray systemTray;

    private boolean ready;

    public SWTPlatform() {
        display = Display.getDefault();
        systemTray = display.getSystemTray();
    }

    public void eventLoop() {
        ready = true;
        while (ready && !systemTray.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        systemTray.dispose();
    }

    public void close() {
        this.ready = false;
    }

    public boolean isReady() {
        return ready;
    }

    public Display getDisplay() {
        return display;
    }

    public void runLater(Runnable runnable) {
        display.asyncExec(runnable);
    }

}
