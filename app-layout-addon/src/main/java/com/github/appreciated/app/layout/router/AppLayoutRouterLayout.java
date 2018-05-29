package com.github.appreciated.app.layout.router;

import com.github.appreciated.app.layout.behaviour.AppLayoutElementBase;
import com.github.appreciated.app.layout.builder.AbstractAppLayoutBuilderBase;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.RouterLayout;

@StyleSheet("frontend://com/github/appreciated/app-layout/app-layout.css")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
public abstract class AppLayoutRouterLayout extends Div implements RouterLayout {

    private AppLayoutElementBase configuration;
    private HasElement currentContent;

    public AppLayoutRouterLayout() {
        setSizeFull();
        loadConfiguration();
        UI.getCurrent().getSession().setAttribute("app-layout", this);
    }

    public void reloadConfiguration() {
        removeAll();
        loadConfiguration();
        if (currentContent != null) {
            showRouterLayoutContent(currentContent);
        }
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        currentContent = content;
        configuration.setAppLayoutContent(content);
    }

    private void loadConfiguration() {
        configuration = (AppLayoutElementBase) getConfiguration().build();
        add((Component) configuration);
    }

    public static AppLayoutRouterLayout getCurrent() {
        return (AppLayoutRouterLayout) UI.getCurrent().getSession().getAttribute("app-layout");
    }

    public void navigateTo() {

    }

    public abstract AbstractAppLayoutBuilderBase getConfiguration();

    public void closeDrawer() {
        configuration.closeDrawer();
    }

    public void toggleDrawer() {
        configuration.toggleDrawer();
    }

    public void openDrawer() {
        configuration.openDrawer();
    }

    public void closeDrawerIfNotPersistent() {
        configuration.closeDrawerIfNotPersistent();
    }

}
