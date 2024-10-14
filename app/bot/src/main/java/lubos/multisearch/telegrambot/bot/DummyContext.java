package lubos.multisearch.telegrambot.bot;

import org.telegram.telegrambots.abilitybots.api.db.DBContext;
import org.telegram.telegrambots.abilitybots.api.db.Var;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DummyContext implements DBContext {

    static final Map DUMMY_MAP = new DummyMap();

    @Override
    public <K, V> Map<K, V> getMap(String name) {
        return DUMMY_MAP;
    }

    @Override
    public <T> Set<T> getSet(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<T> getList(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Var<T> getVar(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String summary() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object backup() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean recover(Object backup) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String info(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void commit() {
    }

    @Override
    public boolean contains(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
    }

    static class DummyMap extends AbstractMap {

        @Override
        public Object remove(Object key) {
            return null;
        }

        @Override
        public Object put(Object key, Object value) {
            return null;
        }

        @Override
        public Object compute(Object key, BiFunction remappingFunction) {
            return null;
        }

        @Override
        public Object computeIfAbsent(Object key, Function mappingFunction) {
            return null;
        }

        @Override
        public Set<Entry> entrySet() {
            return null;
        }

        @Override
        public Set keySet() {
            return Collections.emptySet();
        }

    }

}