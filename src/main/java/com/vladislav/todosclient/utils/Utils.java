package com.vladislav.todosclient.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vladislav.todosclient.views.LoginView;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Utils {
    private Utils() {
    }

    public static <T> Stream<T> stream(Iterator<T> iterator) {
        Iterable<T> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static <T extends Component> void navigateTo(Class<T> view) {
        final UI ui = UI.getCurrent();
        ui.navigate(view);
        ui.getPage().reload();
    }
}
