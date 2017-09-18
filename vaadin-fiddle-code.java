package org.vaadin.vaadinfiddle;

import com.github.appreciated.builder.DrawerVariant;
import com.github.appreciated.builder.NavigationDrawerBuilder;
import com.github.appreciated.layout.drawer.AbstractNavigationDrawer;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import javax.servlet.annotation.WebServlet;

@Theme("mytheme")
@Title("App Layout Add-on Demo")
@Push
public class MyUI extends UI {

    private VerticalLayout left;

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = MyUI.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        left = new VerticalLayout();
        left.setMargin(false);
        setDrawerVariant(left, DrawerVariant.LEFT);
        setContent(left);
    }

    private void setDrawerVariant(VerticalLayout rightside, DrawerVariant variant) {
        rightside.removeAllComponents();

        AbstractNavigationDrawer drawer = NavigationDrawerBuilder.get()
                .withVariant(variant)
                .withTitle("App Layout Demo")
                .withAppBarElement(getVariantCombo(variant))
                .withNavigationElement(getMenuButton("Home", VaadinIcons.HOME))
                .withNavigationElement(getMenuButton("Charts", VaadinIcons.SPLINE_CHART))
                .withNavigationElement(getMenuButton("Contact", VaadinIcons.CONNECT))
                .withSection("More")
                .withNavigationElement(getMenuButton("More", VaadinIcons.PLUS))
                .withNavigationElement(getMenuButton("Menu", VaadinIcons.MENU))
                .withNavigationElement(getMenuButton("Elements", VaadinIcons.LIST))
                .withSection("Settings")
                .withNavigationElement(getMenuButton("Preferences", VaadinIcons.COG))
                .build();
        rightside.addComponent(drawer);
    }

    ComboBox getVariantCombo(DrawerVariant variant) {
        ComboBox<DrawerVariant> variants = new ComboBox<>();
        variants.addStyleNames(ValoTheme.COMBOBOX_BORDERLESS, ValoTheme.CHECKBOX_SMALL, ValoTheme.TEXTFIELD_ALIGN_RIGHT);
        variants.setWidth("300px");
        variants.setItems(DrawerVariant.LEFT,
                DrawerVariant.LEFT_OVERLAY,
                DrawerVariant.LEFT_RESPONSIVE,
                DrawerVariant.LEFT_RESPONSIVE_OVERLAY,
                DrawerVariant.LEFT_RESPONSIVE_OVERLAY_NO_APP_BAR,
                DrawerVariant.LEFT_RESPONSIVE_SMALL,
                DrawerVariant.LEFT_RESPONSIVE_SMALL_NO_APP_BAR);
        variants.setValue(variant);
        variants.addValueChangeListener(valueChangeEvent -> {
            setDrawerVariant(left, valueChangeEvent.getValue());
        });
        return variants;
    }

    private Button getBorderlessButtonWithIcon(VaadinIcons icon) {
        Button button = new Button(icon);
        button.setWidth("64px");
        button.setHeight("64px");
        button.addStyleNames(ValoTheme.BUTTON_BORDERLESS, ValoTheme.BUTTON_ICON_ONLY);
        return button;
    }

    private Button getMenuButton(String name, Resource icon) {
        Button button = new Button(name);
        button.setIcon(icon);
        button.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        button.addStyleName("app-layout-menu-button");
        button.addStyleName("no-border-radius"); // for material theme only
        button.setWidth(100, Sizeable.Unit.PERCENTAGE);
        //button.addClickListener(clickEvent -> UI.getCurrent().getNavigator().navigateTo(name));
        return button;
    }

    class TestView extends VerticalLayout implements View {
        TestView() {
            Label l = new Label("MyContent");
            addComponent(l);
            setComponentAlignment(l, Alignment.MIDDLE_CENTER);
        }
    }
}