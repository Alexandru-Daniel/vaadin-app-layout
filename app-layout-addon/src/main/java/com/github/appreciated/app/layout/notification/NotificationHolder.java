package com.github.appreciated.app.layout.notification;

import com.github.appreciated.app.layout.builder.interfaces.PairComponentFactory;
import com.github.appreciated.app.layout.notification.entitiy.Notification;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.io.Serializable;

/**
 * This Class is a controller for multiple {@link Notification} instances
 */

public abstract class NotificationHolder<T extends Notification> implements Serializable{

    private PairComponentFactory<NotificationHolder<T>, T> componentProvider;

    private List<T> notifications = new ArrayList<>();
    private List<NotificationsChangeListener<T>> notificationsChangeListeners = new ArrayList<>();
    private List<NotificationClickListener<T>> clickListeners = new ArrayList<>();
    private Notification recentNotification;
    private List<HasText> badgeHolderComponents = new ArrayList<>();
    private Comparator<T> comparator = Comparator.reverseOrder();

    public NotificationHolder(NotificationClickListener<T> listener, T... notifications) {
        this(listener);
        this.notifications.addAll(Arrays.asList(notifications));
    }

    public NotificationHolder(NotificationClickListener<T> listener) {
        if (listener != null) {
            addClickListener(listener);
        }
        setComponentProvider(getComponentProvider());
    }

    public NotificationHolder(T... notifications) {
        this((NotificationClickListener<T>) null);
        this.notifications.addAll(Arrays.asList(notifications));
    }

    public NotificationHolder(Collection<T> notifications) {
        this((NotificationClickListener<T>) null);
        this.notifications.addAll(notifications);
    }

    public void addClickListener(NotificationClickListener<T> listener) {
        this.clickListeners.add(listener);
    }

    abstract PairComponentFactory<NotificationHolder<T>, T> getComponentProvider();

    public void setComponentProvider(PairComponentFactory<NotificationHolder<T>, T> componentProvider) {
        this.componentProvider = componentProvider;
    }

    public NotificationHolder(NotificationClickListener<T> listener, Collection<T> notifications) {
        this(listener);
        this.notifications.addAll(notifications);
    }

    public int getNotificationSize() {
        return notifications.size();
    }

    public List<Component> getNotifications(boolean showAll) {
        List<T> components = getNotifications();
        if (!showAll) {
            components = components.size() > 4 ? components.subList(0, 4) : components;
        }
        return components.stream().sorted(comparator).map(this::getComponent).collect(Collectors.toList());
    }

    public List<T> getNotifications() {
        notifications.sort(comparator);
        return notifications;
    }

    private Component getComponent(T message) {
        return componentProvider.getComponent(this, message);
    }

    /**
     * Needs to be called from UI Thread otherwise there will be issues.
     * @param notification
     */
    public void addNotification(T notification) {
        recentNotification = notification;
        notifications.add(notification);
        notifyListeners();
        notifyAddListeners(notification);
        updateBadgeCaptions();
    }

    private void notifyListeners() {
        notificationsChangeListeners.forEach(listener -> listener.onNotificationChanges(this));
    }

    private void notifyAddListeners(T notification) {
        notificationsChangeListeners.forEach(listener -> listener.onNotificationAdded(notification));
    }

    public void updateBadgeCaptions() {
        badgeHolderComponents.forEach(this::updateBadgeCaption);
    }

    private void updateBadgeCaption(HasText hasText) {
        if (hasText != null) {
            int unread = getUnreadNotifications();
            String value;
            if (unread < 1) {
                value = String.valueOf(0);
            } else if (unread < 10) {
                value = String.valueOf(unread);
            } else {
                value = "9+";
            }
            hasText.setText(value);
            if (hasText instanceof Component) {
                ((Component) hasText).setVisible(unread > 0);
            }
        }
    }

    public int getUnreadNotifications() {
        return (int) notifications.stream().filter(notification -> !notification.isRead()).count();
    }

    public void clearNotifications() {
        notifications.clear();
        notifyListeners();
        updateBadgeCaptions();
    }

    public void addNotificationsChangeListener(NotificationsChangeListener<T> listener) {
        notificationsChangeListeners.add(listener);
    }

    public Component[] getNotificationViews(boolean showAll) {
        List<T> components = getNotifications();
        if (!showAll) {
            components = components.size() > 4 ? components.subList(0, 4) : components;
        }
        return components
                .stream()
                .sorted(comparator)
                .map(this::getComponent)
                .collect(Collectors.toList())
                .toArray(new Component[]{});
    }

    public void onNotificationClicked(T info) {
        notifyClickListeners(info);
        notifyListeners();
        updateBadgeCaptions();
    }

    private void notifyClickListeners(T info) {
        info.setRead(true);
        clickListeners.forEach(listener -> listener.onNotificationClicked(info));
    }

    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public void removeClickListener(NotificationClickListener<T> listener) {
        this.clickListeners.remove(listener);
    }

    public Notification getMostRecentNotification() {
        return recentNotification;
    }

    public void bind(HasText text) {
        addBadgeHolderComponent(text);
        updateBadgeCaptions();
    }

    private void addBadgeHolderComponent(HasText text) {
        this.badgeHolderComponents.add(text);
        updateBadgeCaption(text);
    }

    public void onNotificationDismissed(T info) {
        if (!info.isSticky()) {
            removeNotification(info);
        }
        notifyListeners();
    }

    public void removeNotification(T notification) {
        notifications.remove(notification);
        notifyListeners();
        notifyRemoveListeners(notification);
        updateBadgeCaptions();
    }

    private void notifyRemoveListeners(T notification) {
        notificationsChangeListeners.forEach(listener -> listener.onNotificationRemoved(notification));
    }

    public abstract Function<T, String> getDateTimeFormatter();

    public abstract void setDateTimeFormatter(Function<T, String> formatter);

    public interface NotificationClickListener<T> {
        void onNotificationClicked(T notification);
    }

}
