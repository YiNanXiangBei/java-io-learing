package method;

/**
 * @author yinan
 * @date created in 上午10:57 18-9-26
 */
@FunctionalInterface
public interface GofFunction<T1, T2> {

    void execute(T1 tq, T2 t2);

}
