package com.vladislav.todosclient.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vladislav.todosclient.utils.JwtUtils;

@CssImport("./styles/shared-styles.css")
public class MainLayout extends AppLayout {

    private final JwtUtils jwtUtils;

    public MainLayout(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        if (jwtUtils.getCurrentUserId().isEmpty()) {
            UI.getCurrent().getPage().setLocation("/login");
            return;
        }

        createHeader();
        createDrawer();
    }

    private void createHeader() {
        final H1 logo = new H1("TODO");
        logo.addClassName("logo");

        final HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.addClassName("header");

        addToNavbar(header);
    }

    private void createDrawer() {
        final RouterLink tasks = new RouterLink("All tasks", TasksView.class);
        tasks.setHighlightCondition(HighlightConditions.sameLocation());

        addToDrawer(new VerticalLayout(
                tasks
        ));
    }
}
