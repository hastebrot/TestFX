package org.testfx.service.query.impl;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;
import javafx.stage.Window;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public final class NodeQueryUtils {

    //---------------------------------------------------------------------------------------------
    // CONSTRUCTORS.
    //---------------------------------------------------------------------------------------------

    private NodeQueryUtils() {
        throw new UnsupportedOperationException();
    }

    //---------------------------------------------------------------------------------------------
    // STATIC METHODS.
    //---------------------------------------------------------------------------------------------

    public static Set<Node> rootsOfWindows(Collection<Window> windows) {
        return FluentIterable.from(windows)
            .<Node>transform((window) -> fromWindow(window))
            .toSet();
    }

    public static Set<Node> rootOfWindow(Window... windows) {
        return rootsOfWindows(ImmutableList.copyOf(windows));
    }

    public static Set<Node> rootOfStage(Stage... stages) {
        return FluentIterable.from(ImmutableList.copyOf(stages))
            .<Node>transform((stage) -> fromStage(stage))
            .toSet();
    }

    public static Set<Node> rootOfScene(Scene... scenes) {
        return FluentIterable.from(ImmutableList.copyOf(scenes))
            .<Node>transform((scene) -> fromScene(scene))
            .toSet();
    }

    public static Set<Node> rootOfPopupControl(PopupControl... popupControls) {
        return FluentIterable.from(ImmutableList.copyOf(popupControls))
            .<Node>transform((popupControl) -> fromPopupControl(popupControl))
            .toSet();
    }

    public static Function<Node, Set<Node>> bySelector(String selector) {
        return (parentNode) -> lookupWithSelector(parentNode, selector);
    }

    public static Function<Node, Set<Node>> byPredicate(Predicate<Node> predicate) {
        return (parentNode) -> lookupWithPredicate(parentNode, predicate);
    }

    public static Function<Node, Set<Node>> byText(String text) {
        return byPredicate(hasText(text));
    }

    public static Predicate<Node> hasId(String id) {
        return (node) -> hasNodeId(node, id);
    }

    public static Predicate<Node> hasText(String text) {
        return (node) -> hasNodeText(node, text);
    }

    public static Predicate<Node> isVisible() {
        return (node) -> isNodeVisible(node);
    }

    //---------------------------------------------------------------------------------------------
    // PRIVATE STATIC METHODS.
    //---------------------------------------------------------------------------------------------

    private static Parent fromWindow(Window window) {
        //return window?.scene?.root
        return window.getScene().getRoot();
    }

    private static Parent fromStage(Stage stage) {
        //return stage?.scene?.root
        return stage.getScene().getRoot();
    }

    private static Parent fromScene(Scene scene) {
        //return scene?.root
        return scene.getRoot();
    }

    private static Parent fromPopupControl(PopupControl popupControl) {
        //return popupControl?.scene?.root
        return popupControl.getScene().getRoot();
    }

    private static Set<Node> lookupWithSelector(Node parentNode,
                                                String selector) {
        return parentNode.lookupAll(selector);
    }

    private static Set<Node> lookupWithPredicate(Node parentNode,
                                                 Predicate<Node> predicate) {
        Set<Node> resultNodes = Sets.newLinkedHashSet();
        if (applyPredicateSafely(predicate, parentNode)) {
            resultNodes.add(parentNode);
        }
        if (parentNode instanceof Parent) {
            List<Node> childNodes = ((Parent) parentNode).getChildrenUnmodifiable();
            for (Node childNode : childNodes) {
                resultNodes.addAll(lookupWithPredicate(childNode, predicate));
            }
        }
        return ImmutableSet.copyOf(resultNodes);
    }

    private static <T> boolean applyPredicateSafely(Predicate<T> predicate,
                                                    T input) {
        // TODO: Test cases with ClassCastException.
        try {
            return predicate.apply(input);
        }
        catch (ClassCastException ignore) {
            return false;
        }
    }

    private static boolean hasNodeId(Node node, String id) {
        return Objects.equals(node.getId(), id);
    }

    private static boolean hasNodeText(Node node, String text) {
        // TODO: Test cases with node.getText() == null.
        if (node instanceof Labeled) {
            return Objects.equals(((Labeled) node).getText(), text);
        }
        else if (node instanceof TextInputControl) {
            return Objects.equals(((TextInputControl) node).getText(), text);
        }
        return false;
    }

    private static boolean isNodeVisible(Node node) {
        if (!node.isVisible() || !node.impl_isTreeVisible()) {
            return false;
        }
        return isNodeWithinSceneBounds(node);
    }

    private static boolean isNodeWithinSceneBounds(Node node) {
        Scene scene = node.getScene();
        Bounds nodeBounds = node.localToScene(node.getBoundsInLocal());
        return nodeBounds.intersects(0, 0, scene.getWidth(), scene.getHeight());
    }

}