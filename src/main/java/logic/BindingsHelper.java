package logic;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.concurrent.Worker;

final class BindingsHelper {
    private BindingsHelper() {
    }

    static StringBinding stateText(Worker<?> worker) {
        return Bindings.createStringBinding(
                () -> "Состояние Service: " + worker.getState().name(),
                worker.stateProperty()
        );
    }
}
