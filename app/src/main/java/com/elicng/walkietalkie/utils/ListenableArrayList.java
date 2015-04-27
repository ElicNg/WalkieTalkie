package com.elicng.walkietalkie.utils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Elic on 15-04-26.
 */
public class ListenableArrayList<E> extends ArrayList<E> {

    private ChangeListener changeListener;

    public void setChangeListener(ChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    @Override
    public E remove(int index) {
        E element = super.remove(index);
        advertiseChange();
        return element;
    }

    @Override
    public boolean remove(Object object) {
        boolean removed = super.remove(object);
        if (removed) {
            advertiseChange();
        }
        return removed;
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
        advertiseChange();
    }

    @Override
    public E set(int index, E object) {
        E element = super.set(index, object);
        advertiseChange();
        return element;
    }

    @Override
    public void clear() {
        super.clear();
        advertiseChange();
    }

    @Override
    public boolean add(E object) {
        boolean added = super.add(object);
        if (added) {
            advertiseChange();
        }
        return added;
    }

    @Override
    public void add(int index, E object) {
        super.add(index, object);
        advertiseChange();
    }

    @Override
    public boolean addAll(Collection collection) {
        boolean modified = super.addAll(collection);
        advertiseChange();
        if (modified) {
            advertiseChange();
        }
        return modified;
    }

    @Override
    public boolean addAll(int index, Collection collection) {
        boolean modified = super.addAll(index, collection);
        if (modified) {
            advertiseChange();
        }
        return modified;
    }

    private void advertiseChange() {
        if (changeListener != null) {
            changeListener.onChange();
        }
    }

    public interface ChangeListener {
        void onChange();
    }
}
