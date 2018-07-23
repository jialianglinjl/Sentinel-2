package com.alibaba.csp.sentinel;

import java.lang.reflect.Method;
import java.util.List;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.Rule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;

/**
 * Conceptually, physical or logical resource that need protection should be
 * surrounded by an entry. The requests to this resource will be blocked if any
 * criteria is met, eg. when any {@link Rule}'s threshold is exceeded. Once blocked,
 * a {@link BlockException} will be thrown.
 *
 * <p>
 * To configure the criteria, we can use <code>XXXRuleManager.loadRules()</code> to add rules, eg.
 * {@link FlowRuleManager#loadRules(List)}, {@link DegradeRuleManager#loadRules(List)},
 * {@link SystemRuleManager#loadRules(List)}.
 * </p>
 *
 * <p>
 * Following code is an example, {@code "abc"} represent a unique name for the
 * protected resource:
 * </p>
 *
 * <pre>
 *  public void foo() {
 *     Entry entry = null;
 *     try {
 *        entry = SphU.entry("abc");
 *        // resource that need protection
 *     } catch (BlockException blockException) {
 *         // when goes there, it is blocked
 *         // add blocked handle logic here
 *     } catch (Throwable bizException) {
 *         // business exception
 *         Tracer.trace(bizException);
 *     } finally {
 *         // ensure finally be executed
 *         if (entry != null){
 *             entry.exit();
 *         }
 *     }
 *  }
 * </pre>
 *
 * <p>
 * Make sure {@code SphU.entry()} and {@link Entry#exit()} be paired in the same thread,
 * otherwise {@link ErrorEntryFreeException} will be thrown.
 * </p>
 *
 * @author jialiang.linjl
 * @see SphO
 */
public class SphU {

    private static final Object[] OBJECTS0 = new Object[0];

    /**
     * Checking all {@link Rule}s about the resource.
     *
     * @param name the unique name of the protected resource
     * @throws BlockException if the block criteria is met, eg. when any rule's threshold is exceeded.
     */
    public static Entry entry(String name) throws BlockException {
        return Env.sph.entry(name, EntryType.OUT, 1, OBJECTS0);
    }

    /**
     * Checking all {@link Rule}s about the protected method.
     *
     * @param method the protected method
     * @throws BlockException if the block criteria is met, eg. when any rule's threshold is exceeded.
     */
    public static Entry entry(Method method) throws BlockException {
        return Env.sph.entry(method, EntryType.OUT, 1, OBJECTS0);
    }

    /**
     * Checking all {@link Rule}s about the protected method.
     *
     * @param method the protected method
     * @param count  tokens required
     * @throws BlockException if the block criteria is met, eg. when any rule's threshold is exceeded.
     */
    public static Entry entry(Method method, int count) throws BlockException {
        return Env.sph.entry(method, EntryType.OUT, count, OBJECTS0);
    }

    /**
     * Checking all {@link Rule}s about the resource.
     *
     * @param name  the unique string for the resource
     * @param count tokens required
     * @throws BlockException if the block criteria is met, eg. when any rule's threshold is exceeded.
     */
    public static Entry entry(String name, int count) throws BlockException {
        return Env.sph.entry(name, EntryType.OUT, count, OBJECTS0);
    }

    /**
     * Checking all {@link Rule}s about the protected method.
     *
     * @param method the protected method
     * @param type   the resource is an inbound or an outbound method. This is used
     *               to mark whether it can be blocked when the system is unstable,
     *               only inbound traffic could be blocked by {@link SystemRule}
     * @throws BlockException if the block criteria is met, eg. when any rule's threshold is exceeded.
     */
    public static Entry entry(Method method, EntryType type) throws BlockException {
        return Env.sph.entry(method, type, 1, OBJECTS0);
    }

    /**
     * Checking all {@link Rule}s about the resource.
     *
     * @param name the unique name for the protected resource
     * @param type the resource is an inbound or an outbound method. This is used
     *             to mark whether it can be blocked when the system is unstable,
     *             only inbound traffic could be blocked by {@link SystemRule}
     * @throws BlockException if the block criteria is met, eg. when any rule's threshold is exceeded.
     */
    public static Entry entry(String name, EntryType type) throws BlockException {
        return Env.sph.entry(name, type, 1, OBJECTS0);
    }

    /**
     * Checking all {@link Rule}s about the protected method.
     *
     * @param method the protected method
     * @param type   the resource is an inbound or an outbound method. This is used
     *               to mark whether it can be blocked when the system is unstable,
     *               only inbound traffic could be blocked by {@link SystemRule}
     * @param count  tokens required
     * @throws BlockException if the block criteria is met, eg. when any rule's threshold is exceeded.
     */
    public static Entry entry(Method method, EntryType type, int count) throws BlockException {
        return Env.sph.entry(method, type, count, OBJECTS0);
    }

    /**
     * Checking all {@link Rule}s about the resource.
     *
     * @param name  the unique name for the protected resource
     * @param type  the resource is an inbound or an outbound method. This is used
     *              to mark whether it can be blocked when the system is unstable,
     *              only inbound traffic could be blocked by {@link SystemRule}
     * @param count tokens required
     * @throws BlockException if the block criteria is met, eg. when any rule's threshold is exceeded.
     */
    public static Entry entry(String name, EntryType type, int count) throws BlockException {
        return Env.sph.entry(name, type, count, OBJECTS0);
    }

    /**
     * Checking all {@link Rule}s about the protected method.
     *
     * @param method the protected method
     * @param type   the resource is an inbound or an outbound method. This is used
     *               to mark whether it can be blocked when the system is unstable,
     *               only inbound traffic could be blocked by {@link SystemRule}
     * @param count  tokens required
     * @param args   the parameters of the method.
     * @throws BlockException if the block criteria is met, eg. when any rule's threshold is exceeded.
     */
    public static Entry entry(Method method, EntryType type, int count, Object... args) throws BlockException {
        return Env.sph.entry(method, type, count, args);
    }

    /**
     * Checking all {@link Rule}s about the resource.
     *
     * @param name  the unique name for the protected resource
     * @param type  the resource is an inbound or an outbound method. This is used
     *              to mark whether it can be blocked when the system is unstable,
     *              only inbound traffic could be blocked by {@link SystemRule}
     * @param count tokens required
     * @param args  extra parameters.
     * @throws BlockException if the block criteria is met, eg. when any rule's threshold is exceeded.
     */
    public static Entry entry(String name, EntryType type, int count, Object... args) throws BlockException {
        return Env.sph.entry(name, type, count, args);
    }
}
