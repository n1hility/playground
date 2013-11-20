package org.jboss.test;

import org.jboss.BogusKeyConcurrentMap;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jason T. Greene
 */
public class BogusKeyConcurrentMapTest {
    public static class Bogus {
        private Integer value;
        public static boolean breakme = false;

        public Bogus(Integer value) {
            this.value = value;
        }

        public int hashCode() {
            return breakme ? -1 : value.hashCode();
        }

        public boolean equals(Object o) {
            if (!(o instanceof Bogus)) {
                return false;
            }

            Bogus other = (Bogus)o;
            return other.value.equals(value);
        }
    }


    @Test
    public void testBogusRemove() {
        BogusKeyConcurrentMap<Bogus, Object> map = new BogusKeyConcurrentMap<Bogus, Object>();

        Bogus save = null;
        for (int i = 0; i < 1000; i++) {
            Bogus key = new Bogus(i);
            map.put(key, new Object());
            if (i == 737) save = key;
        }

        int hash = save.hashCode();

        Assert.assertNotNull(map.get(save));
        Bogus.breakme = true;

        Assert.assertNull(map.remove(save));
        Assert.assertEquals(1000, map.size());
        Assert.assertNotNull(map.removeDeadKey(hash, save));
        Assert.assertEquals(999, map.size());

        Bogus.breakme = false;

        Assert.assertNull(map.get(save));
    }
}
